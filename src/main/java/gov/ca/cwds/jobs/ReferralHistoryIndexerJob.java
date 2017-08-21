package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral>
    implements JobResultSetAware<EsPersonReferral> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralHistoryIndexerJob.class);

  private static final String SQL_INSERT =
      "INSERT INTO CWDSDSM.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "SELECT rc.FKREFERL_T, rc.FKCLIENT_T, rc.SENSTV_IND\n"
          + "FROM CWDSDSM.CMP_REFR_CLT rc\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ReferralHistoryIndexerJob(ReplicatedPersonReferralsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsPersonReferral.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_REFERRAL_HIST";
  }

  @Override
  public String getJdbcOrderBy() {
    return " "; // sort manually cuz DB2 might not optimize the sort.
  }

  @Override
  protected String getLegacySourceTable() {
    return "REFERL_T";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT vw.* FROM ");
    // buf.append(dbSchemaName);
    buf.append("CWDSDSM"); // TODO: SPOOF until view created in replication schemas!
    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" vw ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE vw.LIMITED_ACCESS_CODE = 'N'  ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Read recs from a single partition. Must sort results because the database won't do it for us.
   * 
   * <p>
   * Each call of this method may run in its own thread.
   * </p>
   * 
   * @param p partition range to read
   */
  protected void pullRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.warn("BEGIN: extract thread {}", threadName);

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);

      final String sqlSelect = getInitialLoadQuery(getDBSchemaName()).replaceAll("\\s+", " ");
      LOGGER.trace("SQL: {}", sqlSelect);
      enableParallelism(con);

      int cntr = 0;
      EsPersonReferral m;
      final List<EsPersonReferral> unsorted = new ArrayList<>(50000);

      try (PreparedStatement stmtInsert = con.prepareStatement(SQL_INSERT);
          PreparedStatement stmtSelect = con.prepareStatement(sqlSelect)) {

        stmtInsert.setMaxRows(0);
        stmtInsert.setQueryTimeout(0);
        stmtInsert.setString(1, p.getLeft());
        stmtInsert.setString(2, p.getRight());

        final int referralCount = stmtInsert.executeUpdate();
        LOGGER.info("referral count: {}", referralCount);

        stmtSelect.setFetchSize(5000); // faster
        stmtSelect.setMaxRows(0);
        stmtSelect.setQueryTimeout(0);

        final ResultSet rs = stmtSelect.executeQuery(); // NOSONAR
        while (!fatalError && rs.next() && (m = extract(rs)) != null) {
          JobLogUtils.logEvery(++cntr, "Retrieved", "recs");
          unsorted.add(m);
        }

        con.commit();
      } finally {
        // Statement and connection close automatically.
      }

      unsorted.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential()
          .collect(Collectors.groupingBy(EsPersonReferral::getClientId)).entrySet().stream()
          .sequential().map(e -> normalizeSingle(e.getValue())).forEach(this::addToIndexQueue);

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.warn("DONE: Extract thread {}", threadName);
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");

    try {
      // This job normalizes without the transform thread.
      doneTransform = true;

      final List<Pair<String, String>> ranges = getPartitionRanges();
      final List<ForkJoinTask<?>> tasks = new ArrayList<>();

      ForkJoinPool forkJoinPool = new ForkJoinPool(3);
      for (Pair<String, String> p : ranges) {
        tasks.add(forkJoinPool.submit(() -> pullRange(p)));
      }

      for (ForkJoinTask<?> task : tasks) {
        task.join();
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: main extract thread");
  }

  @Override
  protected boolean useTransformThread() {
    return false;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, LARGE data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------

      ret.add(Pair.of("aaaaaaaaaa", "AbSJtEP38A"));
      ret.add(Pair.of("AbSJtEP38A", "AdhOZU94kv"));
      ret.add(Pair.of("AdhOZU94kv", "AeEVlHW195"));
      ret.add(Pair.of("AeEVlHW195", "Af4ASRZH2H"));
      ret.add(Pair.of("Af4ASRZH2H", "Ahr2YlV7Lu"));
      ret.add(Pair.of("Ahr2YlV7Lu", "AiPP7ZLIVF"));
      ret.add(Pair.of("AiPP7ZLIVF", "AkeFFE999d"));
      ret.add(Pair.of("AkeFFE999d", "AlzMon9IIL"));
      ret.add(Pair.of("AlzMon9IIL", "AmXE5sP49S"));
      ret.add(Pair.of("AmXE5sP49S", "AondG4UDv4"));
      ret.add(Pair.of("AondG4UDv4", "ApMI6ks193"));
      ret.add(Pair.of("ApMI6ks193", "Aq7MSXq14N"));
      ret.add(Pair.of("Aq7MSXq14N", "AtClmuf01S"));
      ret.add(Pair.of("AtClmuf01S", "Au9GYYTJkK"));
      ret.add(Pair.of("Au9GYYTJkK", "AwBDaDq5VY"));
      ret.add(Pair.of("AwBDaDq5VY", "Ax4YappBgV"));
      ret.add(Pair.of("Ax4YappBgV", "AzvCQvR01S"));
      ret.add(Pair.of("AzvCQvR01S", "AAWMtnO10S"));
      ret.add(Pair.of("AAWMtnO10S", "ACpB2xt30A"));
      ret.add(Pair.of("ACpB2xt30A", "ADQJM8n45H"));
      ret.add(Pair.of("ADQJM8n45H", "AFgp1BB36B"));
      ret.add(Pair.of("AFgp1BB36B", "AHhOvDA73o"));
      ret.add(Pair.of("AHhOvDA73o", "AJfT4ZTEyI"));
      ret.add(Pair.of("AJfT4ZTEyI", "AKENmlY3eH"));
      ret.add(Pair.of("AKENmlY3eH", "AL21xtx1DY"));
      ret.add(Pair.of("AL21xtx1DY", "ANrWYnO0SR"));
      ret.add(Pair.of("ANrWYnO0SR", "AOQQcV3B11"));
      ret.add(Pair.of("AOQQcV3B11", "AQdZFZbICh"));
      ret.add(Pair.of("AQdZFZbICh", "ARDqwsL41S"));
      ret.add(Pair.of("ARDqwsL41S", "AS1WYJl37S"));
      ret.add(Pair.of("AS1WYJl37S", "AUrDhrz8NV"));
      ret.add(Pair.of("AUrDhrz8NV", "AVRtG6L30A"));
      ret.add(Pair.of("AVRtG6L30A", "AXhdSCn15A"));
      ret.add(Pair.of("AXhdSCn15A", "AYJpompGvb"));
      ret.add(Pair.of("AYJpompGvb", "A0XHfyo97y"));
      ret.add(Pair.of("A0XHfyo97y", "A2phXJh04F"));
      ret.add(Pair.of("A2phXJh04F", "A3PCFWS9CR"));
      ret.add(Pair.of("A3PCFWS9CR", "A5g2Hi536B"));
      ret.add(Pair.of("A5g2Hi536B", "A6Hml1a9Vh"));
      ret.add(Pair.of("A6Hml1a9Vh", "A8bgmkA8SL"));
      ret.add(Pair.of("A8bgmkA8SL", "A9BA1m32q3"));
      ret.add(Pair.of("A9BA1m32q3", "Ba17qd743S"));
      ret.add(Pair.of("Ba17qd743S", "Bcuj9D99k6"));
      ret.add(Pair.of("Bcuj9D99k6", "BdUVwaAIfw"));
      ret.add(Pair.of("BdUVwaAIfw", "BfjYDFv6ob"));
      ret.add(Pair.of("BfjYDFv6ob", "BgMBRpJ37S"));
      ret.add(Pair.of("BgMBRpJ37S", "BiaQO5W5Cn"));
      ret.add(Pair.of("BiaQO5W5Cn", "BjChBiJCSc"));
      ret.add(Pair.of("BjChBiJCSc", "Bk0fdCtC1F"));
      ret.add(Pair.of("Bk0fdCtC1F", "BnhBmZm6CP"));
      ret.add(Pair.of("BnhBmZm6CP", "Bo2DhV6H0A"));
      ret.add(Pair.of("Bo2DhV6H0A", "BqqSkL02OJ"));
      ret.add(Pair.of("BqqSkL02OJ", "BrOsJCz01S"));
      ret.add(Pair.of("BrOsJCz01S", "BtePDyL3Ut"));
      ret.add(Pair.of("BtePDyL3Ut", "BuEom2O8mF"));
      ret.add(Pair.of("BuEom2O8mF", "Bv2DVtz3PB"));
      ret.add(Pair.of("Bv2DVtz3PB", "BxrLVBq199"));
      ret.add(Pair.of("BxrLVBq199", "ByQVuNB046"));
      ret.add(Pair.of("ByQVuNB046", "BACh5pO9mp"));
      ret.add(Pair.of("BACh5pO9mp", "BCRCdcD2jX"));
      ret.add(Pair.of("BCRCdcD2jX", "BEf7Jqu9sZ"));
      ret.add(Pair.of("BEf7Jqu9sZ", "BFGJ4TF0OV"));
      ret.add(Pair.of("BFGJ4TF0OV", "BG6WMOS6fi"));
      ret.add(Pair.of("BG6WMOS6fi", "BIx374x3ft"));
      ret.add(Pair.of("BIx374x3ft", "BJWY7nO5Cp"));
      ret.add(Pair.of("BJWY7nO5Cp", "BLl7dfB9us"));
      ret.add(Pair.of("BLl7dfB9us", "BMOddy25Dk"));
      ret.add(Pair.of("BMOddy25Dk", "BOcWrtfGnc"));
      ret.add(Pair.of("BOcWrtfGnc", "BPBS8qw3Ej"));
      ret.add(Pair.of("BPBS8qw3Ej", "BQ1ffEVBWO"));
      ret.add(Pair.of("BQ1ffEVBWO", "BSrh6L14kN"));
      ret.add(Pair.of("BSrh6L14kN", "BUpu6SH30A"));
      ret.add(Pair.of("BUpu6SH30A", "BWpTm6kEO2"));
      ret.add(Pair.of("BWpTm6kEO2", "BXO3BLYBJD"));
      ret.add(Pair.of("BXO3BLYBJD", "BZhyMBq8Op"));
      ret.add(Pair.of("BZhyMBq8Op", "B0GTCckEV7"));
      ret.add(Pair.of("B0GTCckEV7", "B186Lo9Cpt"));
      ret.add(Pair.of("B186Lo9Cpt", "B3Ahd0T36B"));
      ret.add(Pair.of("B3Ahd0T36B", "B41Uo574hX"));
      ret.add(Pair.of("B41Uo574hX", "B6ucitVH9Q"));
      ret.add(Pair.of("B6ucitVH9Q", "B7UvCHkD1N"));
      ret.add(Pair.of("B7UvCHkD1N", "B9lSbo6Ba4"));
      ret.add(Pair.of("B9lSbo6Ba4", "CaNOGGR72f"));
      ret.add(Pair.of("CaNOGGR72f", "Ccd4XtDJoa"));
      ret.add(Pair.of("Ccd4XtDJoa", "CdGC66O4bY"));
      ret.add(Pair.of("CdGC66O4bY", "Ce6CG8w947"));
      ret.add(Pair.of("Ce6CG8w947", "Cg5iUPh30A"));
      ret.add(Pair.of("Cg5iUPh30A", "Ci5z3xr6WR"));
      ret.add(Pair.of("Ci5z3xr6WR", "CkuVo7Y70F"));
      ret.add(Pair.of("CkuVo7Y70F", "ClTT29jFQ6"));
      ret.add(Pair.of("ClTT29jFQ6", "CngatsV6d7"));
      ret.add(Pair.of("CngatsV6d7", "CoFi9jC5q5"));
      ret.add(Pair.of("CoFi9jC5q5", "Cp2bLdEGzt"));
      ret.add(Pair.of("Cp2bLdEGzt", "CrqKVWc199"));
      ret.add(Pair.of("CrqKVWc199", "CsOb2W7A06"));
      ret.add(Pair.of("CsOb2W7A06", "Cucu9uD37S"));
      ret.add(Pair.of("Cucu9uD37S", "CvzBFBsJ5y"));
      ret.add(Pair.of("CvzBFBsJ5y", "CwWcc108DP"));
      ret.add(Pair.of("CwWcc108DP", "Cyll81GAnE"));
      ret.add(Pair.of("Cyll81GAnE", "CAxDNGa5Hc"));
      ret.add(Pair.of("CAxDNGa5Hc", "CBY2T5F3iu"));
      ret.add(Pair.of("CBY2T5F3iu", "CDpUgk5EyP"));
      ret.add(Pair.of("CDpUgk5EyP", "CERfHdA9UX"));
      ret.add(Pair.of("CERfHdA9UX", "CGgWa55F6N"));
      ret.add(Pair.of("CGgWa55F6N", "CHGKsUE6Cf"));
      ret.add(Pair.of("CHGKsUE6Cf", "CI7g91gC4r"));
      ret.add(Pair.of("CI7g91gC4r", "CKxN0427GE"));
      ret.add(Pair.of("CKxN0427GE", "CLXilJ23dA"));
      ret.add(Pair.of("CLXilJ23dA", "CNGbzhB30A"));
      ret.add(Pair.of("CNGbzhB30A", "CPUMOfCC8S"));
      ret.add(Pair.of("CPUMOfCC8S", "CRpQq6lK1Y"));
      ret.add(Pair.of("CRpQq6lK1Y", "CSO8Zo53hy"));
      ret.add(Pair.of("CSO8Zo53hy", "CUgsd2g0P3"));
      ret.add(Pair.of("CUgsd2g0P3", "CVJVNZq10S"));
      ret.add(Pair.of("CVJVNZq10S", "CW9MuF35Ps"));
      ret.add(Pair.of("CW9MuF35Ps", "CYA21bS4YD"));
      ret.add(Pair.of("CYA21bS4YD", "CZ17GmLBp1"));
      ret.add(Pair.of("CZ17GmLBp1", "C1sJZ53C7A"));
      ret.add(Pair.of("C1sJZ53C7A", "C2SJNfp9tb"));
      ret.add(Pair.of("C2SJNfp9tb", "C4f71Eh2k5"));
      ret.add(Pair.of("C4f71Eh2k5", "C6Jb28c4wm"));
      ret.add(Pair.of("C6Jb28c4wm", "C8gumz5DN8"));
      ret.add(Pair.of("C8gumz5DN8", "C9EzFYTC1a"));
      ret.add(Pair.of("C9EzFYTC1a", "Dce8bry5Jb"));
      ret.add(Pair.of("Dce8bry5Jb", "DdFqIYr8N6"));
      ret.add(Pair.of("DdFqIYr8N6", "De4A3wc0ZX"));
      ret.add(Pair.of("De4A3wc0ZX", "DgujHjY2O2"));
      ret.add(Pair.of("DgujHjY2O2", "DhRbu0S89j"));
      ret.add(Pair.of("DhRbu0S89j", "DjhpXLJ3aN"));
      ret.add(Pair.of("DjhpXLJ3aN", "DkGhViTBB0"));
      ret.add(Pair.of("DkGhViTBB0", "Dl4OYbB6AD"));
      ret.add(Pair.of("Dl4OYbB6AD", "DnujErR37S"));
      ret.add(Pair.of("DnujErR37S", "DoRci0o5Dn"));
      ret.add(Pair.of("DoRci0o5Dn", "Dqfs5NR36B"));
      ret.add(Pair.of("Dqfs5NR36B", "DrEo1IH37S"));
      ret.add(Pair.of("DrEo1IH37S", "Ds2Laef30A"));
      ret.add(Pair.of("Ds2Laef30A", "DvD1iSDAzW"));
      ret.add(Pair.of("DvD1iSDAzW", "Dw3fcSbCMi"));
      ret.add(Pair.of("Dw3fcSbCMi", "Dyrig0LCOk"));
      ret.add(Pair.of("Dyrig0LCOk", "DzPUGC70Yt"));
      ret.add(Pair.of("DzPUGC70Yt", "DBeXWR3Af4"));
      ret.add(Pair.of("DBeXWR3Af4", "DCGWraR9CR"));
      ret.add(Pair.of("DCGWraR9CR", "DD5z6suC42"));
      ret.add(Pair.of("DD5z6suC42", "DFvz8oT01S"));
      ret.add(Pair.of("DFvz8oT01S", "DGWupbV40S"));
      ret.add(Pair.of("DGWupbV40S", "DJpDTww2dT"));
      ret.add(Pair.of("DJpDTww2dT", "DK0Er5HBG3"));
      ret.add(Pair.of("DK0Er5HBG3", "DMqocyQ9j5"));
      ret.add(Pair.of("DMqocyQ9j5", "DNTy68PGlm"));
      ret.add(Pair.of("DNTy68PGlm", "DPlNqDYEDF"));
      ret.add(Pair.of("DPlNqDYEDF", "DQLORMaDt4"));
      ret.add(Pair.of("DQLORMaDt4", "DSgAqpj5Df"));
      ret.add(Pair.of("DSgAqpj5Df", "DTIn8bZJkK"));
      ret.add(Pair.of("DTIn8bZJkK", "DVa5u8cCGB"));
      ret.add(Pair.of("DVa5u8cCGB", "DWCnwCR5bT"));
      ret.add(Pair.of("DWCnwCR5bT", "DX43y0m9GW"));
      ret.add(Pair.of("DX43y0m9GW", "DZw5bxxJSV"));
      ret.add(Pair.of("DZw5bxxJSV", "D1jxupNAS2"));
      ret.add(Pair.of("D1jxupNAS2", "D2ItVZ65Co"));
      ret.add(Pair.of("D2ItVZ65Co", "D37xwbM5O4"));
      ret.add(Pair.of("D37xwbM5O4", "D5xy4en11P"));
      ret.add(Pair.of("D5xy4en11P", "D6YfZMf30A"));
      ret.add(Pair.of("D6YfZMf30A", "D8ndeiH83S"));
      ret.add(Pair.of("D8ndeiH83S", "D9LTEB063f"));
      ret.add(Pair.of("D9LTEB063f", "Ebbt8ihEry"));
      ret.add(Pair.of("Ebbt8ihEry", "EcBNiCL6ob"));
      ret.add(Pair.of("EcBNiCL6ob", "EdX85rJDWe"));
      ret.add(Pair.of("EdX85rJDWe", "EfpwXkADNc"));
      ret.add(Pair.of("EfpwXkADNc", "EgP4vqs195"));
      ret.add(Pair.of("EgP4vqs195", "Eid3ngV9ip"));
      ret.add(Pair.of("Eid3ngV9ip", "EjD4RIO1Hp"));
      ret.add(Pair.of("EjD4RIO1Hp", "Ek2dlng8Jb"));
      ret.add(Pair.of("Ek2dlng8Jb", "EmqDXtwBm5"));
      ret.add(Pair.of("EmqDXtwBm5", "En9lYEB8e7"));
      ret.add(Pair.of("En9lYEB8e7", "Eqspb8L54S"));
      ret.add(Pair.of("Eqspb8L54S", "ErSqaSH2OJ"));
      ret.add(Pair.of("ErSqaSH2OJ", "Eth8hNb45L"));
      ret.add(Pair.of("Eth8hNb45L", "EuJnETBF4L"));
      ret.add(Pair.of("EuJnETBF4L", "Ev9z9HT8kF"));
      ret.add(Pair.of("Ev9z9HT8kF", "ExBMtUZ4aK"));
      ret.add(Pair.of("ExBMtUZ4aK", "EyZPegt6eP"));
      ret.add(Pair.of("EyZPegt6eP", "EAoUaQW2wd"));
      ret.add(Pair.of("EAoUaQW2wd", "EBNPuytAyO"));
      ret.add(Pair.of("EBNPuytAyO", "EEqIr90EyI"));
      ret.add(Pair.of("EEqIr90EyI", "EFSKP19E4W"));
      ret.add(Pair.of("EFSKP19E4W", "EHjwpcC6VR"));
      ret.add(Pair.of("EHjwpcC6VR", "EIK2JVa199"));
      ret.add(Pair.of("EIK2JVa199", "EKcYH3UCpS"));
      ret.add(Pair.of("EKcYH3UCpS", "ELF5ODv6zU"));
      ret.add(Pair.of("ELF5ODv6zU", "EM6Dh06COB"));
      ret.add(Pair.of("EM6Dh06COB", "EOzbKKN2hL"));
      ret.add(Pair.of("EOzbKKN2hL", "EP0lVGU2ql"));
      ret.add(Pair.of("EP0lVGU2ql", "ERsgQBX5PE"));
      ret.add(Pair.of("ERsgQBX5PE", "ESTNeAz37S"));
      ret.add(Pair.of("ESTNeAz37S", "EUkz3RX9A4"));
      ret.add(Pair.of("EUkz3RX9A4", "EWRkcvx25X"));
      ret.add(Pair.of("EWRkcvx25X", "EYnZDCWC5M"));
      ret.add(Pair.of("EYnZDCWC5M", "EZMILo137S"));
      ret.add(Pair.of("EZMILo137S", "E1bxk7X3RT"));
      ret.add(Pair.of("E1bxk7X3RT", "E2AZZP39fm"));
      ret.add(Pair.of("E2AZZP39fm", "E3016wd5qq"));
      ret.add(Pair.of("E3016wd5qq", "E5qYxA6BYU"));
      ret.add(Pair.of("E5qYxA6BYU", "E6QalxiCGa"));
      ret.add(Pair.of("E6QalxiCGa", "E8eqiDh07S"));
      ret.add(Pair.of("E8eqiDh07S", "E9DCdKbAzi"));
      ret.add(Pair.of("E9DCdKbAzi", "Fa13XZuFQI"));
      ret.add(Pair.of("Fa13XZuFQI", "Fcrs2377jE"));
      ret.add(Pair.of("Fcrs2377jE", "FdRbA3WExD"));
      ret.add(Pair.of("FdRbA3WExD", "FfgDe3w8SL"));
      ret.add(Pair.of("FfgDe3w8SL", "FgGVa9fEcW"));
      ret.add(Pair.of("FgGVa9fEcW", "Fh9Iowq6cU"));
      ret.add(Pair.of("Fh9Iowq6cU", "FkKr6JR9V3"));
      ret.add(Pair.of("FkKr6JR9V3", "FmaqxXP26z"));
      ret.add(Pair.of("FmaqxXP26z", "FnAHnKM7YS"));
      ret.add(Pair.of("FnAHnKM7YS", "Fo0CEfS199"));
      ret.add(Pair.of("Fo0CEfS199", "FqqDfAz5Cp"));
      ret.add(Pair.of("FqqDfAz5Cp", "FrQX2F8BG3"));
      ret.add(Pair.of("FrQX2F8BG3", "Fth718L37S"));
      ret.add(Pair.of("Fth718L37S", "FuJLBnLAPD"));
      ret.add(Pair.of("FuJLBnLAPD", "Fv7LWEN2Ne"));
      ret.add(Pair.of("Fv7LWEN2Ne", "FxzC5EX5Fe"));
      ret.add(Pair.of("FxzC5EX5Fe", "Fy0ECpL3hy"));
      ret.add(Pair.of("Fy0ECpL3hy", "FArve3578v"));
      ret.add(Pair.of("FArve3578v", "FBOA7meKVv"));
      ret.add(Pair.of("FBOA7meKVv", "FDei6Cg3Ez"));
      ret.add(Pair.of("FDei6Cg3Ez", "FEDfvKbDdh"));
      ret.add(Pair.of("FEDfvKbDdh", "FF1TYn5JfK"));
      ret.add(Pair.of("FF1TYn5JfK", "FHpqbmC8kK"));
      ret.add(Pair.of("FHpqbmC8kK", "FIOWKns4kv"));
      ret.add(Pair.of("FIOWKns4kv", "FKdHYe37CO"));
      ret.add(Pair.of("FKdHYe37CO", "FLCoVu537S"));
      ret.add(Pair.of("FLCoVu537S", "FM1KVZ56gX"));
      ret.add(Pair.of("FM1KVZ56gX", "FOnJ0D56mA"));
      ret.add(Pair.of("FOnJ0D56mA", "FQQgQXsEte"));
      ret.add(Pair.of("FQQgQXsEte", "FSqaVAbC7S"));
      ret.add(Pair.of("FSqaVAbC7S", "FTQOE8v43S"));
      ret.add(Pair.of("FTQOE8v43S", "FVfSbDO199"));
      ret.add(Pair.of("FVfSbDO199", "FWG3gP997y"));
      ret.add(Pair.of("FWG3gP997y", "FX5lTQf43S"));
      ret.add(Pair.of("FX5lTQf43S", "FZv7cw9A05"));
      ret.add(Pair.of("FZv7cw9A05", "F0VEcEpHRd"));
      ret.add(Pair.of("F0VEcEpHRd", "F2lPiKyH6i"));
      ret.add(Pair.of("F2lPiKyH6i", "F3K0W1B3hy"));
      ret.add(Pair.of("F3K0W1B3hy", "F5bRsX4CVp"));
      ret.add(Pair.of("F5bRsX4CVp", "F68d2XJ54S"));
      ret.add(Pair.of("F68d2XJ54S", "F9bAVcZNMr"));
      ret.add(Pair.of("F9bAVcZNMr", "GazWqkT44S"));
      ret.add(Pair.of("GazWqkT44S", "GbZTkKxFfE"));
      ret.add(Pair.of("GbZTkKxFfE", "GeDrY2zCk8"));
      ret.add(Pair.of("GeDrY2zCk8", "Gf37d3p34A"));
      ret.add(Pair.of("Gf37d3p34A", "GhwNXUH37S"));
      ret.add(Pair.of("GhwNXUH37S", "GiXWcLlH1k"));
      ret.add(Pair.of("GiXWcLlH1k", "Gko1xCs4kN"));
      ret.add(Pair.of("Gko1xCs4kN", "GlPDkl44bn"));
      ret.add(Pair.of("GlPDkl44bn", "GnhAvLkC3n"));
      ret.add(Pair.of("GnhAvLkC3n", "GoJiO6CGsk"));
      ret.add(Pair.of("GoJiO6CGsk", "GqbeqKo7NP"));
      ret.add(Pair.of("GqbeqKo7NP", "GrBTUNl36B"));
      ret.add(Pair.of("GrBTUNl36B", "Gs4B8TOA6L"));
      ret.add(Pair.of("Gs4B8TOA6L", "GuvLbSdBm4"));
      ret.add(Pair.of("GuvLbSdBm4", "GwX2Y0d0ZX"));
      ret.add(Pair.of("GwX2Y0d0ZX", "GyvfQgF7PC"));
      ret.add(Pair.of("GyvfQgF7PC", "GzSxEG67XF"));
      ret.add(Pair.of("GzSxEG67XF", "GBg3aBDDV0"));
      ret.add(Pair.of("GBg3aBDDV0", "GCFTgDIDJn"));
      ret.add(Pair.of("GCFTgDIDJn", "GD3nhFB37S"));
      ret.add(Pair.of("GD3nhFB37S", "GFoMoXR1IE"));
      ret.add(Pair.of("GFoMoXR1IE", "GGMgJGg4l6"));
      ret.add(Pair.of("GGMgJGg4l6", "GIbh5Eq5E6"));
      ret.add(Pair.of("GIbh5Eq5E6", "GJ7pyOk1zf"));
      ret.add(Pair.of("GJ7pyOk1zf", "GL8RulR7l9"));
      ret.add(Pair.of("GL8RulR7l9", "GNArq1x15A"));
      ret.add(Pair.of("GNArq1x15A", "GOXY1QYBlQ"));
      ret.add(Pair.of("GOXY1QYBlQ", "GQnJq1T2R7"));
      ret.add(Pair.of("GQnJq1T2R7", "GRPJE6Z3K2"));
      ret.add(Pair.of("GRPJE6Z3K2", "GTe8DRD6f4"));
      ret.add(Pair.of("GTe8DRD6f4", "GUGRyXM7PC"));
      ret.add(Pair.of("GUGRyXM7PC", "GV5IazrE6H"));
      ret.add(Pair.of("GV5IazrE6H", "GXusHA130A"));
      ret.add(Pair.of("GXusHA130A", "GYT1LAX6st"));
      ret.add(Pair.of("GYT1LAX6st", "G0BrbFe26z"));
      ret.add(Pair.of("G0BrbFe26z", "G2UtA5f3TN"));
      ret.add(Pair.of("G2UtA5f3TN", "G4gq1K85DB"));
      ret.add(Pair.of("G4gq1K85DB", "G5EwpLW83S"));
      ret.add(Pair.of("G5EwpLW83S", "G62awLa5DD"));
      ret.add(Pair.of("G62awLa5DD", "G8phC5d4wm"));
      ret.add(Pair.of("G8phC5d4wm", "G9MdUnG8qb"));
      ret.add(Pair.of("G9MdUnG8qb", "HbaZlis5hk"));
      ret.add(Pair.of("HbaZlis5hk", "HcCJrnN5Dg"));
      ret.add(Pair.of("HcCJrnN5Dg", "Hd2WMMzA6L"));
      ret.add(Pair.of("Hd2WMMzA6L", "HfuomZfJPJ"));
      ret.add(Pair.of("HfuomZfJPJ", "HgVSHGyC6v"));
      ret.add(Pair.of("HgVSHGyC6v", "HioWIgD37S"));
      ret.add(Pair.of("HioWIgD37S", "HjP8OfiAdL"));
      ret.add(Pair.of("HjP8OfiAdL", "Hlj0Bnt8kK"));
      ret.add(Pair.of("Hlj0Bnt8kK", "HmL7g1qE0v"));
      ret.add(Pair.of("HmL7g1qE0v", "HofrlBM3tX"));
      ret.add(Pair.of("HofrlBM3tX", "HqszbZeCGa"));
      ret.add(Pair.of("HqszbZeCGa", "Hsf5rdyBp1"));
      ret.add(Pair.of("Hsf5rdyBp1", "HtGlUfXFty"));
      ret.add(Pair.of("HtGlUfXFty", "Hu3NGUgBBG"));
      ret.add(Pair.of("Hu3NGUgBBG", "HwtNBy1DyB"));
      ret.add(Pair.of("HwtNBy1DyB", "HxTJiJ75Fa"));
      ret.add(Pair.of("HxTJiJ75Fa", "HzhIiKc42E"));
      ret.add(Pair.of("HzhIiKc42E", "HAFi2z42Mh"));
      ret.add(Pair.of("HAFi2z42Mh", "HB3415x6yO"));
      ret.add(Pair.of("HB3415x6yO", "HDKgtbFIw9"));
      ret.add(Pair.of("HDKgtbFIw9", "HF3E9sK4Zj"));
      ret.add(Pair.of("HF3E9sK4Zj", "HHtSIQZ7PC"));
      ret.add(Pair.of("HHtSIQZ7PC", "HITV3dG2OJ"));
      ret.add(Pair.of("HITV3dG2OJ", "HKhy5mh5Dg"));
      ret.add(Pair.of("HKhy5mh5Dg", "HLIf7uz07S"));
      ret.add(Pair.of("HLIf7uz07S", "HM59q4YJmm"));
      ret.add(Pair.of("HM59q4YJmm", "HOvVhC5GzG"));
      ret.add(Pair.of("HOvVhC5GzG", "HPVAjCD5ae"));
      ret.add(Pair.of("HPVAjCD5ae", "HRlhON08rH"));
      ret.add(Pair.of("HRlhON08rH", "HSKZPLH3hy"));
      ret.add(Pair.of("HSKZPLH3hy", "HUaaQoz36B"));
      ret.add(Pair.of("HUaaQoz36B", "HVyatEX36B"));
      ret.add(Pair.of("HVyatEX36B", "HXzj0rMLwp"));
      ret.add(Pair.of("HXzj0rMLwp", "HZBpLsJ7HX"));
      ret.add(Pair.of("HZBpLsJ7HX", "H01pdCk9G1"));
      ret.add(Pair.of("H01pdCk9G1", "H2oPvHNA6L"));
      ret.add(Pair.of("H2oPvHNA6L", "H3LtQIrIBv"));
      ret.add(Pair.of("H3LtQIrIBv", "H5agtGQBPY"));
      ret.add(Pair.of("H5agtGQBPY", "H6wUM3Z2JJ"));
      ret.add(Pair.of("H6wUM3Z2JJ", "H7VfJTI6ob"));
      ret.add(Pair.of("H7VfJTI6ob", "H9j1JDC5XE"));
      ret.add(Pair.of("H9j1JDC5XE", "IaJcXzN36B"));
      ret.add(Pair.of("IaJcXzN36B", "IcbmKIl2gd"));
      ret.add(Pair.of("IcbmKIl2gd", "IdEgA4PAoH"));
      ret.add(Pair.of("IdEgA4PAoH", "Ie7BzmzAi2"));
      ret.add(Pair.of("Ie7BzmzAi2", "Igzk3me6eV"));
      ret.add(Pair.of("Igzk3me6eV", "Ih0rGVm1nH"));
      ret.add(Pair.of("Ih0rGVm1nH", "IjT0ePKA2T"));
      ret.add(Pair.of("IjT0ePKA2T", "Il6fijY4jJ"));
      ret.add(Pair.of("Il6fijY4jJ", "InuKC3x2My"));
      ret.add(Pair.of("InuKC3x2My", "IoUQhUd5Ch"));
      ret.add(Pair.of("IoUQhUd5Ch", "IqjWYni10S"));
      ret.add(Pair.of("IqjWYni10S", "IrID4mP43S"));
      ret.add(Pair.of("IrID4mP43S", "Is7V6PEFxz"));
      ret.add(Pair.of("Is7V6PEFxz", "IuwKzb38bA"));
      ret.add(Pair.of("IuwKzb38bA", "IvXp6Yr54S"));
      ret.add(Pair.of("IvXp6Yr54S", "Ixms6G6GpH"));
      ret.add(Pair.of("Ixms6G6GpH", "IyKyZMiGYZ"));
      ret.add(Pair.of("IyKyZMiGYZ", "IAdkDC863f"));
      ret.add(Pair.of("IAdkDC863f", "IBAxB5N54S"));
      ret.add(Pair.of("IBAxB5N54S", "ICXVVSC8SW"));
      ret.add(Pair.of("ICXVVSC8SW", "IEmreJUCQd"));
      ret.add(Pair.of("IEmreJUCQd", "IFKzxdKEro"));
      ret.add(Pair.of("IFKzxdKEro", "IG8xMrL9hu"));
      ret.add(Pair.of("IG8xMrL9hu", "IIvJ9070Ke"));
      ret.add(Pair.of("IIvJ9070Ke", "IJUA0JdKRD"));
      ret.add(Pair.of("IJUA0JdKRD", "ILhWz7h54S"));
      ret.add(Pair.of("ILhWz7h54S", "IMEWHlf8XT"));
      ret.add(Pair.of("IMEWHlf8XT", "IN3J40Q6bn"));
      ret.add(Pair.of("IN3J40Q6bn", "IPsBDNz7r0"));
      ret.add(Pair.of("IPsBDNz7r0", "IQ6qI4k8xK"));
      ret.add(Pair.of("IQ6qI4k8xK", "ITsDC1gGUs"));
      ret.add(Pair.of("ITsDC1gGUs", "IUUFATD4wm"));
      ret.add(Pair.of("IUUFATD4wm", "IWl2NRiCOQ"));
      ret.add(Pair.of("IWl2NRiCOQ", "IXNOUmz1zg"));
      ret.add(Pair.of("IXNOUmz1zg", "IZh4nGO0ZX"));
      ret.add(Pair.of("IZh4nGO0ZX", "I0JcJdb6sv"));
      ret.add(Pair.of("I0JcJdb6sv", "I19JAGJ2Nd"));
      ret.add(Pair.of("I19JAGJ2Nd", "I3x32NVCkN"));
      ret.add(Pair.of("I3x32NVCkN", "I4WoO2TC1E"));
      ret.add(Pair.of("I4WoO2TC1E", "I6l72WX43S"));
      ret.add(Pair.of("I6l72WX43S", "I7OkrOM6Au"));
      ret.add(Pair.of("I7OkrOM6Au", "Jalvzvt33A"));
      ret.add(Pair.of("Jalvzvt33A", "JbLctkl5eq"));
      ret.add(Pair.of("JbLctkl5eq", "JdaiD5u5Je"));
      ret.add(Pair.of("JdaiD5u5Je", "JfJq2kOH9Q"));
      ret.add(Pair.of("JfJq2kOH9Q", "Jg9dfvN30A"));
      ret.add(Pair.of("Jg9dfvN30A", "JivVE1J37S"));
      ret.add(Pair.of("JivVE1J37S", "JjXBrqlMTJ"));
      ret.add(Pair.of("JjXBrqlMTJ", "Jlmrc6W4jJ"));
      ret.add(Pair.of("Jlmrc6W4jJ", "JmMN6en36B"));
      ret.add(Pair.of("JmMN6en36B", "JodiBe3BzW"));
      ret.add(Pair.of("JodiBe3BzW", "JpDzRNcGF7"));
      ret.add(Pair.of("JpDzRNcGF7", "Jq4avLLDBp"));
      ret.add(Pair.of("Jq4avLLDBp", "JssdAL96Cf"));
      ret.add(Pair.of("JssdAL96Cf", "JtSr45h6Au"));
      ret.add(Pair.of("JtSr45h6Au", "Jvics9h44S"));
      ret.add(Pair.of("Jvics9h44S", "Jxg2vhi0ap"));
      ret.add(Pair.of("Jxg2vhi0ap", "JzfTWPF5O4"));
      ret.add(Pair.of("JzfTWPF5O4", "JAFajMA199"));
      ret.add(Pair.of("JAFajMA199", "JB7mK7RGzt"));
      ret.add(Pair.of("JB7mK7RGzt", "JDw1k8DFFi"));
      ret.add(Pair.of("JDw1k8DFFi", "JEZlYXa0No"));
      ret.add(Pair.of("JEZlYXa0No", "JGocEhS4c8"));
      ret.add(Pair.of("JGocEhS4c8", "JHOvWZSCVp"));
      ret.add(Pair.of("JHOvWZSCVp", "JJfPeVT5ae"));
      ret.add(Pair.of("JJfPeVT5ae", "JKHTbMXA2T"));
      ret.add(Pair.of("JKHTbMXA2T", "JNhgZRB30A"));
      ret.add(Pair.of("JNhgZRB30A", "JOFuUzp01S"));
      ret.add(Pair.of("JOFuUzp01S", "JP3rIrkMQ1"));
      ret.add(Pair.of("JP3rIrkMQ1", "JRsMLX5CEP"));
      ret.add(Pair.of("JRsMLX5CEP", "JSSSoEL36B"));
      ret.add(Pair.of("JSSSoEL36B", "JUhbUtS0ZX"));
      ret.add(Pair.of("JUhbUtS0ZX", "JVFp1XwNvL"));
      ret.add(Pair.of("JVFp1XwNvL", "JW4y9PXAYx"));
      ret.add(Pair.of("JW4y9PXAYx", "JYrYLp4LHs"));
      ret.add(Pair.of("JYrYLp4LHs", "JZSScy590Z"));
      ret.add(Pair.of("JZSScy590Z", "J1iTcUVLjy"));
      ret.add(Pair.of("J1iTcUVLjy", "J3r41emAPQ"));
      ret.add(Pair.of("J3r41emAPQ", "J5f8ZlC5qH"));
      ret.add(Pair.of("J5f8ZlC5qH", "J6HlgoAAhd"));
      ret.add(Pair.of("J6HlgoAAhd", "J77ewxZ2uj"));
      ret.add(Pair.of("J77ewxZ2uj", "J9xzt0T41S"));
      ret.add(Pair.of("J9xzt0T41S", "KaXxFYs2qN"));
      ret.add(Pair.of("KaXxFYs2qN", "Kcm9O8hMFQ"));
      ret.add(Pair.of("Kcm9O8hMFQ", "KdNeQJRASS"));
      ret.add(Pair.of("KdNeQJRASS", "KfcloaP37S"));
      ret.add(Pair.of("KfcloaP37S", "KgAFNjt37S"));
      ret.add(Pair.of("KgAFNjt37S", "Kh0Chte5xB"));
      ret.add(Pair.of("Kh0Chte5xB", "KjrphZmFQI"));
      ret.add(Pair.of("KjrphZmFQI", "KkQSYaZ0Q8"));
      ret.add(Pair.of("KkQSYaZ0Q8", "KmhacDb5fV"));
      ret.add(Pair.of("KmhacDb5fV", "KnHMISYGUi"));
      ret.add(Pair.of("KnHMISYGUi", "Ko47Vjz3qj"));
      ret.add(Pair.of("Ko47Vjz3qj", "KqJVPQ20kC"));
      ret.add(Pair.of("KqJVPQ20kC", "Ks6G1DN36B"));
      ret.add(Pair.of("Ks6G1DN36B", "KuxteFsJx4"));
      ret.add(Pair.of("KuxteFsJx4", "KvX8FIP37S"));
      ret.add(Pair.of("KvX8FIP37S", "KxqmCaP36B"));
      ret.add(Pair.of("KxqmCaP36B", "KyRf9cZ5xF"));
      ret.add(Pair.of("KyRf9cZ5xF", "KAhsRTq8d0"));
      ret.add(Pair.of("KAhsRTq8d0", "KBHcXcrAzf"));
      ret.add(Pair.of("KBHcXcrAzf", "KC9IVtQ0Z6"));
      ret.add(Pair.of("KC9IVtQ0Z6", "KEBMvsw9yH"));
      ret.add(Pair.of("KEBMvsw9yH", "KHcjS4lEAf"));
      ret.add(Pair.of("KHcjS4lEAf", "KIzumBf5DU"));
      ret.add(Pair.of("KIzumBf5DU", "KJXNv6NBix"));
      ret.add(Pair.of("KJXNv6NBix", "KLmFvht3hy"));
      ret.add(Pair.of("KLmFvht3hy", "KMK2Q6L37S"));
      ret.add(Pair.of("KMK2Q6L37S", "KN78qsj36B"));
      ret.add(Pair.of("KN78qsj36B", "KPxa9S534A"));
      ret.add(Pair.of("KPxa9S534A", "KQU30C1JPJ"));
      ret.add(Pair.of("KQU30C1JPJ", "KSjeQrS06Q"));
      ret.add(Pair.of("KSjeQrS06Q", "KTGJ5le5DL"));
      ret.add(Pair.of("KTGJ5le5DL", "KU5S9li4cM"));
      ret.add(Pair.of("KU5S9li4cM", "KWtUZ1V1ZC"));
      ret.add(Pair.of("KWtUZ1V1ZC", "KXTi2Gg03p"));
      ret.add(Pair.of("KXTi2Gg03p", "K0rJsBg5Du"));
      ret.add(Pair.of("K0rJsBg5Du", "K1UCh1gAnH"));
      ret.add(Pair.of("K1UCh1gAnH", "K3jKbP1LIQ"));
      ret.add(Pair.of("K3jKbP1LIQ", "K4KACmo5Dk"));
      ret.add(Pair.of("K4KACmo5Dk", "K6cw4hdCGa"));
      ret.add(Pair.of("K6cw4hdCGa", "K7EjBdx6t4"));
      ret.add(Pair.of("K7EjBdx6t4", "K85ISLd0ZX"));
      ret.add(Pair.of("K85ISLd0ZX", "LaxkLjtDEK"));
      ret.add(Pair.of("LaxkLjtDEK", "LbW1TYX9q0"));
      ret.add(Pair.of("LbW1TYX9q0", "LdlBM8g2Nl"));
      ret.add(Pair.of("LdlBM8g2Nl", "LeLnJ4N42P"));
      ret.add(Pair.of("LeLnJ4N42P", "Lf7kXS3JEQ"));
      ret.add(Pair.of("Lf7kXS3JEQ", "LhwtoSA5wz"));
      ret.add(Pair.of("LhwtoSA5wz", "LiUtWrj9Eo"));
      ret.add(Pair.of("LiUtWrj9Eo", "LkjUMqI10S"));
      ret.add(Pair.of("LkjUMqI10S", "LmJtIcqBJE"));
      ret.add(Pair.of("LmJtIcqBJE", "LomNskc2Ke"));
      ret.add(Pair.of("LomNskc2Ke", "LpNGrFc8kF"));
      ret.add(Pair.of("LpNGrFc8kF", "LredDke8SL"));
      ret.add(Pair.of("LredDke8SL", "LsG764I6d7"));
      ret.add(Pair.of("LsG764I6d7", "Lt8KWyb5O4"));
      ret.add(Pair.of("Lt8KWyb5O4", "LvAIbVVAPH"));
      ret.add(Pair.of("LvAIbVVAPH", "LwZ029AB3q"));
      ret.add(Pair.of("LwZ029AB3q", "LyveV3j0OV"));
      ret.add(Pair.of("LyveV3j0OV", "LzVBzHO1ls"));
      ret.add(Pair.of("LzVBzHO1ls", "LB1PHb32Mh"));
      ret.add(Pair.of("LB1PHb32Mh", "LDpx4UT74E"));
      ret.add(Pair.of("LDpx4UT74E", "LEPV7kL5Df"));
      ret.add(Pair.of("LEPV7kL5Df", "LGeXqOG2PY"));
      ret.add(Pair.of("LGeXqOG2PY", "LHCcqvcM9v"));
      ret.add(Pair.of("LHCcqvcM9v", "LIZGSY543S"));
      ret.add(Pair.of("LIZGSY543S", "LKnchP734A"));
      ret.add(Pair.of("LKnchP734A", "LLLPopR5qq"));
      ret.add(Pair.of("LLLPopR5qq", "LM9RepwGvb"));
      ret.add(Pair.of("LM9RepwGvb", "LOxPW7B4zP"));
      ret.add(Pair.of("LOxPW7B4zP", "LPWdjiaAi2"));
      ret.add(Pair.of("LPWdjiaAi2", "LRk3sKE5fa"));
      ret.add(Pair.of("LRk3sKE5fa", "LTtxW2rCDO"));
      ret.add(Pair.of("LTtxW2rCDO", "LViCxyj15A"));
      ret.add(Pair.of("LViCxyj15A", "LWF0iIn2Kc"));
      ret.add(Pair.of("LWF0iIn2Kc", "LX4nU4dBLd"));
      ret.add(Pair.of("LX4nU4dBLd", "LZtZVynGnc"));
      ret.add(Pair.of("LZtZVynGnc", "L0S0IGx5om"));
      ret.add(Pair.of("L0S0IGx5om", "L2nUO7m2Pg"));
      ret.add(Pair.of("L2nUO7m2Pg", "L3PxKMmAUp"));
      ret.add(Pair.of("L3PxKMmAUp", "L5frdkeHS8"));
      ret.add(Pair.of("L5frdkeHS8", "L6GXJuVI4a"));
      ret.add(Pair.of("L6GXJuVI4a", "L78Nm20947"));
      ret.add(Pair.of("L78Nm20947", "L95uTPt3eH"));
      ret.add(Pair.of("L95uTPt3eH", "MbtUvHGBVn"));
      ret.add(Pair.of("MbtUvHGBVn", "McTsIgE6lq"));
      ret.add(Pair.of("McTsIgE6lq", "MehGpzg9YH"));
      ret.add(Pair.of("MehGpzg9YH", "MgpXYV1FlU"));
      ret.add(Pair.of("MgpXYV1FlU", "MiftbbDGnc"));
      ret.add(Pair.of("MiftbbDGnc", "MjDfF335Q0"));
      ret.add(Pair.of("MjDfF335Q0", "Mk2DSxzEwG"));
      ret.add(Pair.of("Mk2DSxzEwG", "Mmrxjut7OF"));
      ret.add(Pair.of("Mmrxjut7OF", "MnQaqa2EU1"));
      ret.add(Pair.of("MnQaqa2EU1", "MphbnCE5Co"));
      ret.add(Pair.of("MphbnCE5Co", "MqIEiKl5Ah"));
      ret.add(Pair.of("MqIEiKl5Ah", "Mr7OzBp37S"));
      ret.add(Pair.of("Mr7OzBp37S", "MtwUoosA2s"));
      ret.add(Pair.of("MtwUoosA2s", "MuViPX1Kje"));
      ret.add(Pair.of("MuViPX1Kje", "Mwkme8xFTz"));
      ret.add(Pair.of("Mwkme8xFTz", "MxH9bnKEFl"));
      ret.add(Pair.of("MxH9bnKEFl", "MAhQycZCJt"));
      ret.add(Pair.of("MAhQycZCJt", "MBGwdpe4xT"));
      ret.add(Pair.of("MBGwdpe4xT", "MC6eNC06Y7"));
      ret.add(Pair.of("MC6eNC06Y7", "MExOQLyCDO"));
      ret.add(Pair.of("MExOQLyCDO", "MFWgJuy947"));
      ret.add(Pair.of("MFWgJuy947", "MHkM6FN37S"));
      ret.add(Pair.of("MHkM6FN37S", "MIKtEIKC8p"));
      ret.add(Pair.of("MIKtEIKC8p", "MJ9AfIC6Y7"));
      ret.add(Pair.of("MJ9AfIC6Y7", "MLz9b1j6Au"));
      ret.add(Pair.of("MLz9b1j6Au", "MNwufPm3oq"));
      ret.add(Pair.of("MNwufPm3oq", "MPxtjxJ5E6"));
      ret.add(Pair.of("MPxtjxJ5E6", "MQXIwFO193"));
      ret.add(Pair.of("MQXIwFO193", "MSnrDmg2OJ"));
      ret.add(Pair.of("MSnrDmg2OJ", "MTNkYCH30A"));
      ret.add(Pair.of("MTNkYCH30A", "MVcPxcA5Cx"));
      ret.add(Pair.of("MVcPxcA5Cx", "MWCm5lvAmN"));
      ret.add(Pair.of("MWCm5lvAmN", "MX0hwxu7OF"));
      ret.add(Pair.of("MX0hwxu7OF", "MZrlmFO9hu"));
      ret.add(Pair.of("MZrlmFO9hu", "M0SAoNcGmy"));
      ret.add(Pair.of("M0SAoNcGmy", "M2k9fKVF9R"));
      ret.add(Pair.of("M2k9fKVF9R", "M31kKko5nH"));
      ret.add(Pair.of("M31kKko5nH", "M6n00Ab5DU"));
      ret.add(Pair.of("M6n00Ab5DU", "M7NvHSG50Q"));
      ret.add(Pair.of("M7NvHSG50Q", "M9b5p8fAjA"));
      ret.add(Pair.of("M9b5p8fAjA", "NbjDzj91aS"));
      ret.add(Pair.of("NbjDzj91aS", "NcHckuy7D3"));
      ret.add(Pair.of("NcHckuy7D3", "Nd6JCpr3ft"));
      ret.add(Pair.of("Nd6JCpr3ft", "NfuzeuBEEu"));
      ret.add(Pair.of("NfuzeuBEEu", "NgT348sCnH"));
      ret.add(Pair.of("NgT348sCnH", "NiisKa915A"));
      ret.add(Pair.of("NiisKa915A", "NjH3Mb69A4"));
      ret.add(Pair.of("NjH3Mb69A4", "Nk67ddp2vI"));
      ret.add(Pair.of("Nk67ddp2vI", "Nmui567FhP"));
      ret.add(Pair.of("Nmui567FhP", "NnT5L3B36B"));
      ret.add(Pair.of("NnT5L3B36B", "Npid1Yv30A"));
      ret.add(Pair.of("Npid1Yv30A", "NqF5Zqs5Q0"));
      ret.add(Pair.of("NqF5Zqs5Q0", "Nr4RC2j3UO"));
      ret.add(Pair.of("Nr4RC2j3UO", "NuF993R5DI"));
      ret.add(Pair.of("NuF993R5DI", "Nv56pemGWM"));
      ret.add(Pair.of("Nv56pemGWM", "NxuFNVV6eZ"));
      ret.add(Pair.of("NxuFNVV6eZ", "NyRSWaP4F3"));
      ret.add(Pair.of("NyRSWaP4F3", "NAilhOjJPV"));
      ret.add(Pair.of("NAilhOjJPV", "NBIH29s5DD"));
      ret.add(Pair.of("NBIH29s5DD", "NC6L9t1IeJ"));
      ret.add(Pair.of("NC6L9t1IeJ", "NEup0dK0P3"));
      ret.add(Pair.of("NEup0dK0P3", "NFTjywU3oq"));
      ret.add(Pair.of("NFTjywU3oq", "NIuu8je45L"));
      ret.add(Pair.of("NIuu8je45L", "NJVUQVO5Q3"));
      ret.add(Pair.of("NJVUQVO5Q3", "NLmIIdP89j"));
      ret.add(Pair.of("NLmIIdP89j", "NMK4Hru5Cp"));
      ret.add(Pair.of("NMK4Hru5Cp", "NOatfd7AUR"));
      ret.add(Pair.of("NOatfd7AUR", "NPAQNlp30A"));
      ret.add(Pair.of("NPAQNlp30A", "NQZsMTA6m3"));
      ret.add(Pair.of("NQZsMTA6m3", "NSp8AfZ2Ke"));
      ret.add(Pair.of("NSp8AfZ2Ke", "NTO7lsuC8p"));
      ret.add(Pair.of("NTO7lsuC8p", "NVd4vx334A"));
      ret.add(Pair.of("NVd4vx334A", "NWE27FPAcg"));
      ret.add(Pair.of("NWE27FPAcg", "NX1p8HT9YA"));
      ret.add(Pair.of("NX1p8HT9YA", "NZSnl0QCMm"));
      ret.add(Pair.of("NZSnl0QCMm", "N1mB7w4Lsn"));
      ret.add(Pair.of("N1mB7w4Lsn", "N2OjMpY7CO"));
      ret.add(Pair.of("N2OjMpY7CO", "N4dJgRWEtq"));
      ret.add(Pair.of("N4dJgRWEtq", "N5FxnUX49S"));
      ret.add(Pair.of("N5FxnUX49S", "N66eAXWAN6"));
      ret.add(Pair.of("N66eAXWAN6", "N8vulXN40S"));
      ret.add(Pair.of("N8vulXN40S", "N9T2dID5DQ"));
      ret.add(Pair.of("N9T2dID5DQ", "ObiLnHOIOX"));
      ret.add(Pair.of("ObiLnHOIOX", "OcFGU1BBjD"));
      ret.add(Pair.of("OcFGU1BBjD", "Od2XoEGAzW"));
      ret.add(Pair.of("Od2XoEGAzW", "OftlTTSGsk"));
      ret.add(Pair.of("OftlTTSGsk", "OgRcWNWBLP"));
      ret.add(Pair.of("OgRcWNWBLP", "OieuRmN0RA"));
      ret.add(Pair.of("OieuRmN0RA", "OjCD2qP74v"));
      ret.add(Pair.of("OjCD2qP74v", "Ok0Pqjs4c1"));
      ret.add(Pair.of("Ok0Pqjs4c1", "OmPzDAY4pu"));
      ret.add(Pair.of("OmPzDAY4pu", "Oo2BcHmILt"));
      ret.add(Pair.of("Oo2BcHmILt", "OqoSLHt5Du"));
      ret.add(Pair.of("OqoSLHt5Du", "OrNwXn4B3p"));
      ret.add(Pair.of("OrNwXn4B3p", "Otd2e1E1VV"));
      ret.add(Pair.of("Otd2e1E1VV", "OuBQAYmClV"));
      ret.add(Pair.of("OuBQAYmClV", "Ov3JaM6199"));
      ret.add(Pair.of("Ov3JaM6199", "OxqYJHqBiU"));
      ret.add(Pair.of("OxqYJHqBiU", "OyQXvpJCVp"));
      ret.add(Pair.of("OyQXvpJCVp", "OAgCnAq8kL"));
      ret.add(Pair.of("OAgCnAq8kL", "OCJhPJB30A"));
      ret.add(Pair.of("OCJhPJB30A", "OEkVspF5vj"));
      ret.add(Pair.of("OEkVspF5vj", "OFLRZcC5om"));
      ret.add(Pair.of("OFLRZcC5om", "OHdjGc543S"));
      ret.add(Pair.of("OHdjGc543S", "OIEQbGT30A"));
      ret.add(Pair.of("OIEQbGT30A", "OJ6wv9X5D7"));
      ret.add(Pair.of("OJ6wv9X5D7", "OLyBMHa2jX"));
      ret.add(Pair.of("OLyBMHa2jX", "OMYlF5F0P3"));
      ret.add(Pair.of("OMYlF5F0P3", "OOoiFtK5Cv"));
      ret.add(Pair.of("OOoiFtK5Cv", "OPOVhYK2Nd"));
      ret.add(Pair.of("OPOVhYK2Nd", "ORfHHd45DL"));
      ret.add(Pair.of("ORfHHd45DL", "OSGFTOQ5nH"));
      ret.add(Pair.of("OSGFTOQ5nH", "OViDVxa5E6"));
      ret.add(Pair.of("OViDVxa5E6", "OWGY21rEag"));
      ret.add(Pair.of("OWGY21rEag", "OX5tHdx8xK"));
      ret.add(Pair.of("OX5tHdx8xK", "OZt3xAK4kN"));
      ret.add(Pair.of("OZt3xAK4kN", "O0SsOCLFvr"));
      ret.add(Pair.of("O0SsOCLFvr", "O2iX4Fw4iv"));
      ret.add(Pair.of("O2iX4Fw4iv", "O3JQHH03nX"));
      ret.add(Pair.of("O3JQHH03nX", "O49NLe92OJ"));
      ret.add(Pair.of("O49NLe92OJ", "O6yrnYRK1c"));
      ret.add(Pair.of("O6yrnYRK1c", "O7WMTARDNc"));
      ret.add(Pair.of("O7WMTARDNc", "O9mH8ziBDj"));
      ret.add(Pair.of("O9mH8ziBDj", "PaNq5W03UQ"));
      ret.add(Pair.of("PaNq5W03UQ", "PccDcxC7PN"));
      ret.add(Pair.of("PccDcxC7PN", "PdztBRuAFi"));
      ret.add(Pair.of("PdztBRuAFi", "PeWY5H17iE"));
      ret.add(Pair.of("PeWY5H17iE", "PglSIKq5ID"));
      ret.add(Pair.of("PglSIKq5ID", "PiWtqu2Dys"));
      ret.add(Pair.of("PiWtqu2Dys", "Pkk3EzkNy6"));
      ret.add(Pair.of("Pkk3EzkNy6", "PlIvdTOJV4"));
      ret.add(Pair.of("PlIvdTOJV4", "Pm5X2eY196"));
      ret.add(Pair.of("Pm5X2eY196", "PotFRre10S"));
      ret.add(Pair.of("PotFRre10S", "PpR3lY1I5J"));
      ret.add(Pair.of("PpR3lY1I5J", "PrfFvuy6eP"));
      ret.add(Pair.of("PrfFvuy6eP", "PsEfjIz2PY"));
      ret.add(Pair.of("PsEfjIz2PY", "Pt0ilFI3cq"));
      ret.add(Pair.of("Pt0ilFI3cq", "PvoQEqgA7h"));
      ret.add(Pair.of("PvoQEqgA7h", "PwNer1dA05"));
      ret.add(Pair.of("PwNer1dA05", "PyaeFpD3bR"));
      ret.add(Pair.of("PyaeFpD3bR", "PzWIhNa0Z6"));
      ret.add(Pair.of("PzWIhNa0Z6", "PBp54d7IYj"));
      ret.add(Pair.of("PBp54d7IYj", "PCRytOn63O"));
      ret.add(Pair.of("PCRytOn63O", "PEjIWk1A5Z"));
      ret.add(Pair.of("PEjIWk1A5Z", "PFKGCwI8Sn"));
      ret.add(Pair.of("PFKGCwI8Sn", "PHcgZRg0ZJ"));
      ret.add(Pair.of("PHcgZRg0ZJ", "PIDOeB707S"));
      ret.add(Pair.of("PIDOeB707S", "PJ4yVuQ6nO"));
      ret.add(Pair.of("PJ4yVuQ6nO", "PLwuU5u3oW"));
      ret.add(Pair.of("PLwuU5u3oW", "PMXHgiM63f"));
      ret.add(Pair.of("PMXHgiM63f", "PPobNKdGrU"));
      ret.add(Pair.of("PPobNKdGrU", "PQ2gV8tH1h"));
      ret.add(Pair.of("PQ2gV8tH1h", "PSshfjJ77R"));
      ret.add(Pair.of("PSshfjJ77R", "PTRMGZA7US"));
      ret.add(Pair.of("PTRMGZA7US", "PVgzSn37Lu"));
      ret.add(Pair.of("PVgzSn37Lu", "PWHcOrC5DO"));
      ret.add(Pair.of("PWHcOrC5DO", "PX3KMDx7wW"));
      ret.add(Pair.of("PX3KMDx7wW", "PZr73tB23F"));
      ret.add(Pair.of("PZr73tB23F", "P0Rybux2SP"));
      ret.add(Pair.of("P0Rybux2SP", "P2fQnFx37S"));
      ret.add(Pair.of("P2fQnFx37S", "P3D6BBJBf7"));
      ret.add(Pair.of("P3D6BBJBf7", "P44C0uu9FG"));
      ret.add(Pair.of("P44C0uu9FG", "P7IKbb1FQq"));
      ret.add(Pair.of("P7IKbb1FQq", "P9aBZSKIGi"));
      ret.add(Pair.of("P9aBZSKIGi", "QaB5Vcj0BT"));
      ret.add(Pair.of("QaB5Vcj0BT", "Qdc5ZOyAzW"));
      ret.add(Pair.of("Qdc5ZOyAzW", "QeEmmW130A"));
      ret.add(Pair.of("QeEmmW130A", "Qf1kSvb5bT"));
      ret.add(Pair.of("Qf1kSvb5bT", "QhuIpAP33A"));
      ret.add(Pair.of("QhuIpAP33A", "QiVCO218DV"));
      ret.add(Pair.of("QiVCO218DV", "Qkk0NMM9ip"));
      ret.add(Pair.of("Qkk0NMM9ip", "QlJnfAA5qH"));
      ret.add(Pair.of("QlJnfAA5qH", "Qm8FVQaLjy"));
      ret.add(Pair.of("Qm8FVQaLjy", "QowZLkY5Cn"));
      ret.add(Pair.of("QowZLkY5Cn", "QpYd4kb37S"));
      ret.add(Pair.of("QpYd4kb37S", "QroFC2l0KD"));
      ret.add(Pair.of("QroFC2l0KD", "QsPnL9N40S"));
      ret.add(Pair.of("QsPnL9N40S", "Qvq1Yod6gl"));
      ret.add(Pair.of("Qvq1Yod6gl", "QwMO2AH3dA"));
      ret.add(Pair.of("QwMO2AH3dA", "Qycrf0CA05"));
      ret.add(Pair.of("Qycrf0CA05", "QzCgqcIA05"));
      ret.add(Pair.of("QzCgqcIA05", "QA21ZdKCpD"));
      ret.add(Pair.of("QA21ZdKCpD", "QCuIYWk4wm"));
      ret.add(Pair.of("QCuIYWk4wm", "QDW2bGmAYx"));
      ret.add(Pair.of("QDW2bGmAYx", "QFoDmkkENr"));
      ret.add(Pair.of("QFoDmkkENr", "QGPUw9z7vm"));
      ret.add(Pair.of("QGPUw9z7vm", "QIGi4tW6wz"));
      ret.add(Pair.of("QIGi4tW6wz", "QKRWuv45p7"));
      ret.add(Pair.of("QKRWuv45p7", "QMiJqqK7w8"));
      ret.add(Pair.of("QMiJqqK7w8", "QNIOEsS0mF"));
      ret.add(Pair.of("QNIOEsS0mF", "QO5QfWBDNa"));
      ret.add(Pair.of("QO5QfWBDNa", "QQvhEub3oP"));
      ret.add(Pair.of("QQvhEub3oP", "QRSYDpDC9a"));
      ret.add(Pair.of("QRSYDpDC9a", "QTgEslN30A"));
      ret.add(Pair.of("QTgEslN30A", "QUGvditCOQ"));
      ret.add(Pair.of("QUGvditCOQ", "QV7lgo72QH"));
      ret.add(Pair.of("QV7lgo72QH", "QXwIXMgJy6"));
      ret.add(Pair.of("QXwIXMgJy6", "QYWAiBN43S"));
      ret.add(Pair.of("QYWAiBN43S", "Q0u00Yc5Ch"));
      ret.add(Pair.of("Q0u00Yc5Ch", "Q2y5mNt07S"));
      ret.add(Pair.of("Q2y5mNt07S", "Q30rOtu199"));
      ret.add(Pair.of("Q30rOtu199", "Q5sbM04CJD"));
      ret.add(Pair.of("Q5sbM04CJD", "Q6S9YVV40S"));
      ret.add(Pair.of("Q6S9YVV40S", "Q8jTKNVAzW"));
      ret.add(Pair.of("Q8jTKNVAzW", "Q9Lzq3G199"));
      ret.add(Pair.of("Q9Lzq3G199", "RbalMsLE6H"));
      ret.add(Pair.of("RbalMsLE6H", "RcBhdMX9Z3"));
      ret.add(Pair.of("RcBhdMX9Z3", "Rd1hZ1EFhR"));
      ret.add(Pair.of("Rd1hZ1EFhR", "RfruTeH0AR"));
      ret.add(Pair.of("RfruTeH0AR", "RgSos3jF2y"));
      ret.add(Pair.of("RgSos3jF2y", "Rij21h0Idh"));
      ret.add(Pair.of("Rij21h0Idh", "RjKqWDC9Z9"));
      ret.add(Pair.of("RjKqWDC9Z9", "Rla0uE86Cf"));
      ret.add(Pair.of("Rla0uE86Cf", "RmBbBWtFce"));
      ret.add(Pair.of("RmBbBWtFce", "Rn2RfOIAbA"));
      ret.add(Pair.of("Rn2RfOIAbA", "RqBMjMU2PY"));
      ret.add(Pair.of("RqBMjMU2PY", "RrY1xgH37S"));
      ret.add(Pair.of("RrY1xgH37S", "RtmBk8o2jX"));
      ret.add(Pair.of("RtmBk8o2jX", "RuLOcdKFgm"));
      ret.add(Pair.of("RuLOcdKFgm", "Rv9f75e5gS"));
      ret.add(Pair.of("Rv9f75e5gS", "RxuqdV9BjD"));
      ret.add(Pair.of("RxuqdV9BjD", "RyQMpYnA05"));
      ret.add(Pair.of("RyQMpYnA05", "RAecxm610S"));
      ret.add(Pair.of("RAecxm610S", "RBBDUYA21t"));
      ret.add(Pair.of("RBBDUYA21t", "REc7xp4Knx"));
      ret.add(Pair.of("REc7xp4Knx", "RFDamry42E"));
      ret.add(Pair.of("RFDamry42E", "RG1oIWWCtT"));
      ret.add(Pair.of("RG1oIWWCtT", "RIqhqHE5DD"));
      ret.add(Pair.of("RIqhqHE5DD", "RJRIisgI54"));
      ret.add(Pair.of("RJRIisgI54", "RLfHjhdCqG"));
      ret.add(Pair.of("RLfHjhdCqG", "RMCBgDqC5M"));
      ret.add(Pair.of("RMCBgDqC5M", "RN3F8i75Cz"));
      ret.add(Pair.of("RN3F8i75Cz", "RPtFaHT7CO"));
      ret.add(Pair.of("RPtFaHT7CO", "RQSlAEOEGo"));
      ret.add(Pair.of("RQSlAEOEGo", "RShULKx5Du"));
      ret.add(Pair.of("RShULKx5Du", "RTHUFzV30A"));
      ret.add(Pair.of("RTHUFzV30A", "RU5oSLYAkK"));
      ret.add(Pair.of("RU5oSLYAkK", "RXJuBwd9gv"));
      ret.add(Pair.of("RXJuBwd9gv", "RZa9YdS7D7"));
      ret.add(Pair.of("RZa9YdS7D7", "R0AKnSbFHK"));
      ret.add(Pair.of("R0AKnSbFHK", "R1YWu397Jo"));
      ret.add(Pair.of("R1YWu397Jo", "R3n2SEpCQv"));
      ret.add(Pair.of("R3n2SEpCQv", "R4NfBilG8d"));
      ret.add(Pair.of("R4NfBilG8d", "R6bbpxa4p0"));
      ret.add(Pair.of("R6bbpxa4p0", "R7yr1MH07S"));
      ret.add(Pair.of("R7yr1MH07S", "R8WHaq921t"));
      ret.add(Pair.of("R8WHaq921t", "SalOODoCIz"));
      ret.add(Pair.of("SalOODoCIz", "SbNgM2D07S"));
      ret.add(Pair.of("SbNgM2D07S", "Sde8ZMY4vN"));
      ret.add(Pair.of("Sde8ZMY4vN", "SeFKkUW3bh"));
      ret.add(Pair.of("SeFKkUW3bh", "Sf7rmrlBn5"));
      ret.add(Pair.of("Sf7rmrlBn5", "Shyd5HA4m3"));
      ret.add(Pair.of("Shyd5HA4m3", "SjXE5M9DNc"));
      ret.add(Pair.of("SjXE5M9DNc", "SlwSzq08mC"));
      ret.add(Pair.of("SlwSzq08mC", "SmVOGKS2S9"));
      ret.add(Pair.of("SmVOGKS2S9", "Sojp61aF9W"));
      ret.add(Pair.of("Sojp61aF9W", "SpIp0olKIz"));
      ret.add(Pair.of("SpIp0olKIz", "Sq75H5V11K"));
      ret.add(Pair.of("Sq75H5V11K", "Ssvcxaa7B5"));
      ret.add(Pair.of("Ssvcxaa7B5", "StTPUO1KW2"));
      ret.add(Pair.of("StTPUO1KW2", "SviY6qX41S"));
      ret.add(Pair.of("SviY6qX41S", "SwHgcx92Nd"));
      ret.add(Pair.of("SwHgcx92Nd", "Sx7uUuHDr8"));
      ret.add(Pair.of("Sx7uUuHDr8", "Szw7Ws82P4"));
      ret.add(Pair.of("Szw7Ws82P4", "SAUXdTf4nn"));
      ret.add(Pair.of("SAUXdTf4nn", "SCjOxewAcg"));
      ret.add(Pair.of("SCjOxewAcg", "SDI2pft36B"));
      ret.add(Pair.of("SDI2pft36B", "SE6mPzO5Dp"));
      ret.add(Pair.of("SE6mPzO5Dp", "SGwVckA0nk"));
      ret.add(Pair.of("SGwVckA0nk", "SHWkCuy7Vl"));
      ret.add(Pair.of("SHWkCuy7Vl", "SJmFiEc3oP"));
      ret.add(Pair.of("SJmFiEc3oP", "SKMbSgD41S"));
      ret.add(Pair.of("SKMbSgD41S", "SMbuS4pFQ6"));
      ret.add(Pair.of("SMbuS4pFQ6", "SNBPkAw2PY"));
      ret.add(Pair.of("SNBPkAw2PY", "SO2Y4sw1El"));
      ret.add(Pair.of("SO2Y4sw1El", "SRE6988D3U"));
      ret.add(Pair.of("SRE6988D3U", "SS6E2KT1nH"));
      ret.add(Pair.of("SS6E2KT1nH", "SUxzWmv36B"));
      ret.add(Pair.of("SUxzWmv36B", "SVY39L25bY"));
      ret.add(Pair.of("SVY39L25bY", "SXqHRUP37S"));
      ret.add(Pair.of("SXqHRUP37S", "SYTgdtp05S"));
      ret.add(Pair.of("SYTgdtp05S", "S0jOwIV0AR"));
      ret.add(Pair.of("S0jOwIV0AR", "S1IdGE0EcW"));
      ret.add(Pair.of("S1IdGE0EcW", "S24afKB3VR"));
      ret.add(Pair.of("S24afKB3VR", "S4sslpd1zr"));
      ret.add(Pair.of("S4sslpd1zr", "S5O9wiv49S"));
      ret.add(Pair.of("S5O9wiv49S", "S8oxMJe21t"));
      ret.add(Pair.of("S8oxMJe21t", "S9N0nLz6fK"));
      ret.add(Pair.of("S9N0nLz6fK", "TbflL1YG0n"));
      ret.add(Pair.of("TbflL1YG0n", "Tc7E1j93K2"));
      ret.add(Pair.of("Tc7E1j93K2", "TfgrmAZ5qq"));
      ret.add(Pair.of("TfgrmAZ5qq", "TgFqIvAJmm"));
      ret.add(Pair.of("TgFqIvAJmm", "Th4s0jB30A"));
      ret.add(Pair.of("Th4s0jB30A", "Tjq9aHd7hD"));
      ret.add(Pair.of("Tjq9aHd7hD", "TkSlG9h37S"));
      ret.add(Pair.of("TkSlG9h37S", "TmiyXidGLi"));
      ret.add(Pair.of("TmiyXidGLi", "TnG9XTF8mB"));
      ret.add(Pair.of("TnG9XTF8mB", "To7Uu4SNjZ"));
      ret.add(Pair.of("To7Uu4SNjZ", "TqwfOvgJma"));
      ret.add(Pair.of("TqwfOvgJma", "TrVNf3xHQj"));
      ret.add(Pair.of("TrVNf3xHQj", "TtkwFNZ02u"));
      ret.add(Pair.of("TtkwFNZ02u", "TuH7guH44S"));
      ret.add(Pair.of("TuH7guH44S", "Txhf2XN4o6"));
      ret.add(Pair.of("Txhf2XN4o6", "TyHO2ou8xK"));
      ret.add(Pair.of("TyHO2ou8xK", "Tz6yWBN30A"));
      ret.add(Pair.of("Tz6yWBN30A", "TBwlA9F9gO"));
      ret.add(Pair.of("TBwlA9F9gO", "TCWlusb48S"));
      ret.add(Pair.of("TCWlusb48S", "TEkmvq83qP"));
      ret.add(Pair.of("TEkmvq83qP", "TFIs7hF5xB"));
      ret.add(Pair.of("TFIs7hF5xB", "TG72vzcAi2"));
      ret.add(Pair.of("TG72vzcAi2", "TIx4j0hCGB"));
      ret.add(Pair.of("TIx4j0hCGB", "TJ6herAAKK"));
      ret.add(Pair.of("TJ6herAAKK", "TMwtcYo9d2"));
      ret.add(Pair.of("TMwtcYo9d2", "TNXDETV9rU"));
      ret.add(Pair.of("TNXDETV9rU", "TPqnZ4L5Jb"));
      ret.add(Pair.of("TPqnZ4L5Jb", "TQRB1w6Ef6"));
      ret.add(Pair.of("TQRB1w6Ef6", "TSkC2bnGsk"));
      ret.add(Pair.of("TSkC2bnGsk", "TTK2z2pMDO"));
      ret.add(Pair.of("TTK2z2pMDO", "TVdeWcY9ip"));
      ret.add(Pair.of("TVdeWcY9ip", "TWDdtuhGlm"));
      ret.add(Pair.of("TWDdtuhGlm", "TX5JZOtC87"));
      ret.add(Pair.of("TX5JZOtC87", "TZxmSn330A"));
      ret.add(Pair.of("TZxmSn330A", "T0WIxr1DjK"));
      ret.add(Pair.of("T0WIxr1DjK", "T3s5OnNCCS"));
      ret.add(Pair.of("T3s5OnNCCS", "T4RpNHF54S"));
      ret.add(Pair.of("T4RpNHF54S", "T6e6QsL33A"));
      ret.add(Pair.of("T6e6QsL33A", "T7A7OPqD0M"));
      ret.add(Pair.of("T7A7OPqD0M", "T8ZcIBl41S"));
      ret.add(Pair.of("T8ZcIBl41S", "UAn8kJD8mC"));
      ret.add(Pair.of("UAn8kJD8mC", "UBNkS7i9TP"));
      ret.add(Pair.of("UBNkS7i9TP", "UDeaf6O7g2"));
      ret.add(Pair.of("UDeaf6O7g2", "U0zmtqU199"));
      ret.add(Pair.of("U0zmtqU199", "U101kUE199"));
      ret.add(Pair.of("U101kUE199", "U3rBFGzD1N"));
      ret.add(Pair.of("U3rBFGzD1N", "U4N9ErzCxH"));
      ret.add(Pair.of("U4N9ErzCxH", "U6eXklM6qq"));
      ret.add(Pair.of("U6eXklM6qq", "U7EeM7fGUs"));
      ret.add(Pair.of("U7EeM7fGUs", "U83TCYl5DE"));
      ret.add(Pair.of("U83TCYl5DE", "0baFcB89hu"));
      ret.add(Pair.of("0baFcB89hu", "0c7xfST30A"));
      ret.add(Pair.of("0c7xfST30A", "0exkk9y00D"));
      ret.add(Pair.of("0exkk9y00D", "0fZkH6C7GG"));
      ret.add(Pair.of("0fZkH6C7GG", "0hqJehVFXb"));
      ret.add(Pair.of("0hqJehVFXb", "0iRHVwxCXj"));
      ret.add(Pair.of("0iRHVwxCXj", "0kjAw7ICaR"));
      ret.add(Pair.of("0kjAw7ICaR", "0lNOU4e3hy"));
      ret.add(Pair.of("0lNOU4e3hy", "0nfOI9y3Xv"));
      ret.add(Pair.of("0nfOI9y3Xv", "0oHeHVPFYx"));
      ret.add(Pair.of("0oHeHVPFYx", "0p9xmgT49S"));
      ret.add(Pair.of("0p9xmgT49S", "0rByvgjCpS"));
      ret.add(Pair.of("0rByvgjCpS", "0s3qWPEE6u"));
      ret.add(Pair.of("0s3qWPEE6u", "0vDfAms8Kw"));
      ret.add(Pair.of("0vDfAms8Kw", "0w3RCjq199"));
      ret.add(Pair.of("0w3RCjq199", "0yrdNA25bT"));
      ret.add(Pair.of("0yrdNA25bT", "0zRi7Eq8fn"));
      ret.add(Pair.of("0zRi7Eq8fn", "0Bh5y9c0Yt"));
      ret.add(Pair.of("0Bh5y9c0Yt", "0CGIyZ84kN"));
      ret.add(Pair.of("0CGIyZ84kN", "0D7iWQqGJ1"));
      ret.add(Pair.of("0D7iWQqGJ1", "0FwGS2s10S"));
      ret.add(Pair.of("0FwGS2s10S", "0GWWTCABNQ"));
      ret.add(Pair.of("0GWWTCABNQ", "0JwcBHDC4Z"));
      ret.add(Pair.of("0JwcBHDC4Z", "0KWMDI43aN"));
      ret.add(Pair.of("0KWMDI43aN", "0Mmbz1WA16"));
      ret.add(Pair.of("0Mmbz1WA16", "0NMuuiWCOp"));
      ret.add(Pair.of("0NMuuiWCOp", "0PdQ67gLiK"));
      ret.add(Pair.of("0PdQ67gLiK", "0QBTKN8KHZ"));
      ret.add(Pair.of("0QBTKN8KHZ", "0R3NN87Cro"));
      ret.add(Pair.of("0R3NN87Cro", "0Ttif5N07S"));
      ret.add(Pair.of("0Ttif5N07S", "0UUKewr36B"));
      ret.add(Pair.of("0UUKewr36B", "0WiEZAR5Q0"));
      ret.add(Pair.of("0WiEZAR5Q0", "0XI4XbJCKh"));
      ret.add(Pair.of("0XI4XbJCKh", "0Y9v53NG7K"));
      ret.add(Pair.of("0Y9v53NG7K", "00yxDgP40S"));
      ret.add(Pair.of("00yxDgP40S", "01YcPDIDv4"));
      ret.add(Pair.of("01YcPDIDv4", "03pQtAV2Pz"));
      ret.add(Pair.of("03pQtAV2Pz", "04Os4Ks42E"));
      ret.add(Pair.of("04Os4Ks42E", "06dAJNoHmo"));
      ret.add(Pair.of("06dAJNoHmo", "07DnRpp07S"));
      ret.add(Pair.of("07DnRpp07S", "0850cUr30A"));
      ret.add(Pair.of("0850cUr30A", "1avNVLt01S"));
      ret.add(Pair.of("1avNVLt01S", "1bYSleO8Op"));
      ret.add(Pair.of("1bYSleO8Op", "1dqiFn85qR"));
      ret.add(Pair.of("1dqiFn85qR", "1eTcPbd33A"));
      ret.add(Pair.of("1eTcPbd33A", "1gm87tvA17"));
      ret.add(Pair.of("1gm87tvA17", "1hNXBvl6d2"));
      ret.add(Pair.of("1hNXBvl6d2", "1jfCO957zE"));
      ret.add(Pair.of("1jfCO957zE", "1kIdbUd2OJ"));
      ret.add(Pair.of("1kIdbUd2OJ", "1l9khkJ3Un"));
      ret.add(Pair.of("1l9khkJ3Un", "1oeqqRn7B5"));
      ret.add(Pair.of("1oeqqRn7B5", "1qagm43ETd"));
      ret.add(Pair.of("1qagm43ETd", "1ryvDZT5Cp"));
      ret.add(Pair.of("1ryvDZT5Cp", "1sYUlGL01S"));
      ret.add(Pair.of("1sYUlGL01S", "1unZOFlC7A"));
      ret.add(Pair.of("1unZOFlC7A", "1vNarPYNQw"));
      ret.add(Pair.of("1vNarPYNQw", "1xdC8kp2II"));
      ret.add(Pair.of("1xdC8kp2II", "1yCnmuP30A"));
      ret.add(Pair.of("1yCnmuP30A", "1z0NpiJAYx"));
      ret.add(Pair.of("1z0NpiJAYx", "1BCvr338n3"));
      ret.add(Pair.of("1BCvr338n3", "1D31UXa5Co"));
      ret.add(Pair.of("1D31UXa5Co", "1FsCtTW10S"));
      ret.add(Pair.of("1FsCtTW10S", "1GT3nNJ2en"));
      ret.add(Pair.of("1GT3nNJ2en", "1IkjIiA3hy"));
      ret.add(Pair.of("1IkjIiA3hy", "1JIIhkX43S"));
      ret.add(Pair.of("1JIIhkX43S", "1La64AwA15"));
      ret.add(Pair.of("1La64AwA15", "1MABM1GG4F"));
      ret.add(Pair.of("1MABM1GG4F", "1N0t7qE6eS"));
      ret.add(Pair.of("1N0t7qE6eS", "1PqyVE34n3"));
      ret.add(Pair.of("1PqyVE34n3", "1QSfWh226z"));
      ret.add(Pair.of("1QSfWh226z", "1SiNNLw45L"));
      ret.add(Pair.of("1SiNNLw45L", "1TI8uGgBPZ"));
      ret.add(Pair.of("1TI8uGgBPZ", "1WkQdEK7GG"));
      ret.add(Pair.of("1WkQdEK7GG", "1XMkX3K4ui"));
      ret.add(Pair.of("1XMkX3K4ui", "1Zej61Z6t4"));
      ret.add(Pair.of("1Zej61Z6t4", "10GWGGKMTC"));
      ret.add(Pair.of("10GWGGKMTC", "118nq266Cf"));
      ret.add(Pair.of("118nq266Cf", "13yipDn7a9"));
      ret.add(Pair.of("13yipDn7a9", "14YqdIn1DY"));
      ret.add(Pair.of("14YqdIn1DY", "16mANOK8SL"));
      ret.add(Pair.of("16mANOK8SL", "17LaesiIOX"));
      ret.add(Pair.of("17LaesiIOX", "19chyYPFj8"));
      ret.add(Pair.of("19chyYPFj8", "2aCdOphE1A"));
      ret.add(Pair.of("2aCdOphE1A", "2b4z9Kj43S"));
      ret.add(Pair.of("2b4z9Kj43S", "2dxBj33BD4"));
      ret.add(Pair.of("2dxBj33BD4", "2eZ0vWE0Ke"));
      ret.add(Pair.of("2eZ0vWE0Ke", "2gsWio56mW"));
      ret.add(Pair.of("2gsWio56mW", "2iucEzcNQh"));
      ret.add(Pair.of("2iucEzcNQh", "2kuvhHW4YD"));
      ret.add(Pair.of("2kuvhHW4YD", "2lVqgzLG6l"));
      ret.add(Pair.of("2lVqgzLG6l", "2nksfg37DX"));
      ret.add(Pair.of("2nksfg37DX", "2oJGzIeA06"));
      ret.add(Pair.of("2oJGzIeA06", "2p8OZnS5qH"));
      ret.add(Pair.of("2p8OZnS5qH", "2rAZr506CQ"));
      ret.add(Pair.of("2rAZr506CQ", "2s0t7HX5Dk"));
      ret.add(Pair.of("2s0t7HX5Dk", "2uqL0giC1F"));
      ret.add(Pair.of("2uqL0giC1F", "2vRlks68ZL"));
      ret.add(Pair.of("2vRlks68ZL", "2xfPXSq6cR"));
      ret.add(Pair.of("2xfPXSq6cR", "2yGLjl34p3"));
      ret.add(Pair.of("2yGLjl34p3", "2z59oRd54S"));
      ret.add(Pair.of("2z59oRd54S", "2Bs26aX5On"));
      ret.add(Pair.of("2Bs26aX5On", "2CS4WU7BD4"));
      ret.add(Pair.of("2CS4WU7BD4", "2EjViTJ30A"));
      ret.add(Pair.of("2EjViTJ30A", "2FJCvo24Zu"));
      ret.add(Pair.of("2FJCvo24Zu", "2G9DDZB30A"));
      ret.add(Pair.of("2G9DDZB30A", "2IzwVdw5Je"));
      ret.add(Pair.of("2IzwVdw5Je", "2JW64Yc10S"));
      ret.add(Pair.of("2JW64Yc10S", "2Lm2EM541S"));
      ret.add(Pair.of("2Lm2EM541S", "2MK42QsCtD"));
      ret.add(Pair.of("2MK42QsCtD", "2ObRVzj7CO"));
      ret.add(Pair.of("2ObRVzj7CO", "2QKCBbkJEQ"));
      ret.add(Pair.of("2QKCBbkJEQ", "2ScMhw106Q"));
      ret.add(Pair.of("2ScMhw106Q", "2TGPDCn2NJ"));
      ret.add(Pair.of("2TGPDCn2NJ", "2VbhRtF5vM"));
      ret.add(Pair.of("2VbhRtF5vM", "2WEaTa1DWM"));
      ret.add(Pair.of("2WEaTa1DWM", "2X564BqDdh"));
      ret.add(Pair.of("2X564BqDdh", "2ZwYHA210S"));
      ret.add(Pair.of("2ZwYHA210S", "20XAFvbCMX"));
      ret.add(Pair.of("20XAFvbCMX", "22njtAu2hL"));
      ret.add(Pair.of("22njtAu2hL", "23LB8LN2S9"));
      ret.add(Pair.of("23LB8LN2S9", "25cGsio9xL"));
      ret.add(Pair.of("25cGsio9xL", "27v6vZ79Zp"));
      ret.add(Pair.of("27v6vZ79Zp", "29esj226JF"));
      ret.add(Pair.of("29esj226JF", "3aDYCDNE1A"));
      ret.add(Pair.of("3aDYCDNE1A", "3de7rtK199"));
      ret.add(Pair.of("3de7rtK199", "3eG7My35Ch"));
      ret.add(Pair.of("3eG7My35Ch", "3f6f3yPAS2"));
      ret.add(Pair.of("3f6f3yPAS2", "3hu6D6LFMw"));
      ret.add(Pair.of("3hu6D6LFMw", "3iU5KsJIlb"));
      ret.add(Pair.of("3iU5KsJIlb", "3klDDmA9q0"));
      ret.add(Pair.of("3klDDmA9q0", "3lMZjQv34A"));
      ret.add(Pair.of("3lMZjQv34A", "3ncbYw5H6i"));
      ret.add(Pair.of("3ncbYw5H6i", "3oBBbzo9E1"));
      ret.add(Pair.of("3oBBbzo9E1", "3p0LFlG3F8"));
      ret.add(Pair.of("3p0LFlG3F8", "3rqtfwA2KA"));
      ret.add(Pair.of("3rqtfwA2KA", "3sSJVmX5Du"));
      ret.add(Pair.of("3sSJVmX5Du", "3uKcp3B34A"));
      ret.add(Pair.of("3uKcp3B34A", "3wZpG8S7Us"));
      ret.add(Pair.of("3wZpG8S7Us", "3ypVMRC0P3"));
      ret.add(Pair.of("3ypVMRC0P3", "3zP0c0dIUP"));
      ret.add(Pair.of("3zP0c0dIUP", "3BeKzTtCQd"));
      ret.add(Pair.of("3BeKzTtCQd", "3CENm0v54S"));
      ret.add(Pair.of("3CENm0v54S", "3D2bMnK3n0"));
      ret.add(Pair.of("3D2bMnK3n0", "3Fp0cFO0Ke"));
      ret.add(Pair.of("3Fp0cFO0Ke", "3GM7pJT1nH"));
      ret.add(Pair.of("3GM7pJT1nH", "3IbpltR07S"));
      ret.add(Pair.of("3IbpltR07S", "3KuXknN1LU"));
      ret.add(Pair.of("3KuXknN1LU", "3MeV8qX30A"));
      ret.add(Pair.of("3MeV8qX30A", "3NHLHK883S"));
      ret.add(Pair.of("3NHLHK883S", "3PafDdgCDO"));
      ret.add(Pair.of("3PafDdgCDO", "3QAQVuB6V1"));
      ret.add(Pair.of("3QAQVuB6V1", "3R4K4gi9GW"));
      ret.add(Pair.of("3R4K4gi9GW", "3TvSnWO9ck"));
      ret.add(Pair.of("3TvSnWO9ck", "3UVZTfqCpS"));
      ret.add(Pair.of("3UVZTfqCpS", "3Wnj4WoAQn"));
      ret.add(Pair.of("3Wnj4WoAQn", "3XPDIHq5ra"));
      ret.add(Pair.of("3XPDIHq5ra", "3ZkHAMj37S"));
      ret.add(Pair.of("3ZkHAMj37S", "31rS4Ha26h"));
      ret.add(Pair.of("31rS4Ha26h", "33mx4v9AF8"));
      ret.add(Pair.of("33mx4v9AF8", "34NpG0KFvr"));
      ret.add(Pair.of("34NpG0KFvr", "36doGx3CDD"));
      ret.add(Pair.of("36doGx3CDD", "37BNIsn3oW"));
      ret.add(Pair.of("37BNIsn3oW", "381o4o70OV"));
      ret.add(Pair.of("381o4o70OV", "4aontYx0Ut"));
      ret.add(Pair.of("4aontYx0Ut", "4bLYWcD36B"));
      ret.add(Pair.of("4bLYWcD36B", "4ddyeTPGF7"));
      ret.add(Pair.of("4ddyeTPGF7", "4eFHXA930A"));
      ret.add(Pair.of("4eFHXA930A", "4f7UclK8mB"));
      ret.add(Pair.of("4f7UclK8mB", "4hyVmYA72f"));
      ret.add(Pair.of("4hyVmYA72f", "4iWLtBG2kl"));
      ret.add(Pair.of("4iWLtBG2kl", "4knfbRx3LL"));
      ret.add(Pair.of("4knfbRx3LL", "4lOtqVm2Nl"));
      ret.add(Pair.of("4lOtqVm2Nl", "4neJQgF7vm"));
      ret.add(Pair.of("4neJQgF7vm", "4oNa99HCPl"));
      ret.add(Pair.of("4oNa99HCPl", "4rg7AtV30A"));
      ret.add(Pair.of("4rg7AtV30A", "4sJj1go9xb"));
      ret.add(Pair.of("4sJj1go9xb", "4t9kCf70KD"));
      ret.add(Pair.of("4t9kCf70KD", "4vBT2eDA17"));
      ret.add(Pair.of("4vBT2eDA17", "4w1OoZRFMw"));
      ret.add(Pair.of("4w1OoZRFMw", "4ysVvAtCNU"));
      ret.add(Pair.of("4ysVvAtCNU", "4zUJIxtA16"));
      ret.add(Pair.of("4zUJIxtA16", "4BkCW7K5Dk"));
      ret.add(Pair.of("4BkCW7K5Dk", "4CLyhBY5Oc"));
      ret.add(Pair.of("4CLyhBY5Oc", "4Fk7A670Yt"));
      ret.add(Pair.of("4Fk7A670Yt", "4GKwMhzAOq"));
      ret.add(Pair.of("4GKwMhzAOq", "4H8d3sfC1F"));
      ret.add(Pair.of("4H8d3sfC1F", "4JyZWJ06nO"));
      ret.add(Pair.of("4JyZWJ06nO", "4KWxrrF36B"));
      ret.add(Pair.of("4KWxrrF36B", "4MkMGuX36B"));
      ret.add(Pair.of("4MkMGuX36B", "4NKfT7g6kB"));
      ret.add(Pair.of("4NKfT7g6kB", "4O8EH8k9j5"));
      ret.add(Pair.of("4O8EH8k9j5", "4Qytken37S"));
      ret.add(Pair.of("4Qytken37S", "4RWhy8w06K"));
      ret.add(Pair.of("4RWhy8w06K", "4TkdfFcGmy"));
      ret.add(Pair.of("4TkdfFcGmy", "4UJX6er9j6"));
      ret.add(Pair.of("4UJX6er9j6", "4V9AcUgB7S"));
      ret.add(Pair.of("4V9AcUgB7S", "4YKm5eT37S"));
      ret.add(Pair.of("4YKm5eT37S", "40aFt3KEh4"));
      ret.add(Pair.of("40aFt3KEh4", "41AGGH337S"));
      ret.add(Pair.of("41AGGH337S", "420VYP337S"));
      ret.add(Pair.of("420VYP337S", "44sgh0nBG3"));
      ret.add(Pair.of("44sgh0nBG3", "45P7C6536B"));
      ret.add(Pair.of("45P7C6536B", "47h12qBBVn"));
      ret.add(Pair.of("47h12qBBVn", "48H5SfV12z"));
      ret.add(Pair.of("48H5SfV12z", "4972CKZAYx"));
      ret.add(Pair.of("4972CKZAYx", "5bwN5WG5Ch"));
      ret.add(Pair.of("5bwN5WG5Ch", "5cWiDs86d7"));
      ret.add(Pair.of("5cWiDs86d7", "5em7Z3K10S"));
      ret.add(Pair.of("5em7Z3K10S", "5fO4Jhn12o"));
      ret.add(Pair.of("5fO4Jhn12o", "5hguJQRDga"));
      ret.add(Pair.of("5hguJQRDga", "5iGckCz21w"));
      ret.add(Pair.of("5iGckCz21w", "5licsho7x1"));
      ret.add(Pair.of("5licsho7x1", "5mKQNUK996"));
      ret.add(Pair.of("5mKQNUK996", "5obkwJI7D3"));
      ret.add(Pair.of("5obkwJI7D3", "5pC5ynV8qL"));
      ret.add(Pair.of("5pC5ynV8qL", "5q1BrXP6WY"));
      ret.add(Pair.of("5q1BrXP6WY", "5stmWlF36B"));
      ret.add(Pair.of("5stmWlF36B", "5tUxc1l2Un"));
      ret.add(Pair.of("5tUxc1l2Un", "5vnLec530A"));
      ret.add(Pair.of("5vnLec530A", "5wQwPkPBbF"));
      ret.add(Pair.of("5wQwPkPBbF", "5yhYhDl43S"));
      ret.add(Pair.of("5yhYhDl43S", "5zI03hj63f"));
      ret.add(Pair.of("5zI03hj63f", "5A8tVXkIw6"));
      ret.add(Pair.of("5A8tVXkIw6", "5CvNMtg2My"));
      ret.add(Pair.of("5CvNMtg2My", "5DSaLuz4sB"));
      ret.add(Pair.of("5DSaLuz4sB", "5Fg0A0z2KA"));
      ret.add(Pair.of("5Fg0A0z2KA", "5GGsJdQ5ez"));
      ret.add(Pair.of("5GGsJdQ5ez", "5H5g39EAhd"));
      ret.add(Pair.of("5H5g39EAhd", "5JrJTI5Egm"));
      ret.add(Pair.of("5JrJTI5Egm", "5KOuU621GK"));
      ret.add(Pair.of("5KOuU621GK", "5MdhERKDxO"));
      ret.add(Pair.of("5MdhERKDxO", "5NBC8db3dA"));
      ret.add(Pair.of("5NBC8db3dA", "5OYyOZI8XT"));
      ret.add(Pair.of("5OYyOZI8XT", "5Q1UAsHDdh"));
      ret.add(Pair.of("5Q1UAsHDdh", "5S0eNZF9GW"));
      ret.add(Pair.of("5S0eNZF9GW", "5Up2ZPf5ij"));
      ret.add(Pair.of("5Up2ZPf5ij", "5VQjIQr54S"));
      ret.add(Pair.of("5VQjIQr54S", "5XfpmMr37S"));
      ret.add(Pair.of("5XfpmMr37S", "5YGPIGDF4m"));
      ret.add(Pair.of("5YGPIGDF4m", "5Z4R5Kj37S"));
      ret.add(Pair.of("5Z4R5Kj37S", "51vYYsc2KA"));
      ret.add(Pair.of("51vYYsc2KA", "52YUGKd0p9"));
      ret.add(Pair.of("52YUGKd0p9", "54qyW7H7dY"));
      ret.add(Pair.of("54qyW7H7dY", "55ShSzkGvb"));
      ret.add(Pair.of("55ShSzkGvb", "57IqXjgGMH"));
      ret.add(Pair.of("57IqXjgGMH", "59WjXbs16c"));
      ret.add(Pair.of("59WjXbs16c", "6bllBKI7LI"));
      ret.add(Pair.of("6bllBKI7LI", "6cKPSz65om"));
      ret.add(Pair.of("6cKPSz65om", "6fn3lkwJV4"));
      ret.add(Pair.of("6fn3lkwJV4", "6gRLXu537S"));
      ret.add(Pair.of("6gRLXu537S", "6ijrhhi6gX"));
      ret.add(Pair.of("6ijrhhi6gX", "6jOwZoxGUs"));
      ret.add(Pair.of("6jOwZoxGUs", "6lhmeX55Dk"));
      ret.add(Pair.of("6lhmeX55Dk", "6mIx0VS2JO"));
      ret.add(Pair.of("6mIx0VS2JO", "6ocZFlx5SF"));
      ret.add(Pair.of("6ocZFlx5SF", "6pDl7qyCVp"));
      ret.add(Pair.of("6pDl7qyCVp", "6q43Yyf43S"));
      ret.add(Pair.of("6q43Yyf43S", "6svgXr92k3"));
      ret.add(Pair.of("6svgXr92k3", "6tZxsm4Dq8"));
      ret.add(Pair.of("6tZxsm4Dq8", "6vp5NZe5Cn"));
      ret.add(Pair.of("6vp5NZe5Cn", "6xSNJas2bX"));
      ret.add(Pair.of("6xSNJas2bX", "6zqTnE99oD"));
      ret.add(Pair.of("6zqTnE99oD", "6ARlSATGvb"));
      ret.add(Pair.of("6ARlSATGvb", "6CfOGYZIfw"));
      ret.add(Pair.of("6CfOGYZIfw", "6DGHZ2DHbe"));
      ret.add(Pair.of("6DGHZ2DHbe", "6E3Tou101S"));
      ret.add(Pair.of("6E3Tou101S", "6GsFtk9GiK"));
      ret.add(Pair.of("6GsFtk9GiK", "6HQ5tdy48S"));
      ret.add(Pair.of("6HQ5tdy48S", "6JfSFxQIRj"));
      ret.add(Pair.of("6JfSFxQIRj", "6K4fBPo8Bp"));
      ret.add(Pair.of("6K4fBPo8Bp", "6Ng7imM1DY"));
      ret.add(Pair.of("6Ng7imM1DY", "6OEA7seC5j"));
      ret.add(Pair.of("6OEA7seC5j", "6P4eSuAAR1"));
      ret.add(Pair.of("6P4eSuAAR1", "6RqSZkn30A"));
      ret.add(Pair.of("6RqSZkn30A", "6SQlRBZ2gp"));
      ret.add(Pair.of("6SQlRBZ2gp", "6UfDQwFA7N"));
      ret.add(Pair.of("6UfDQwFA7N", "6VDK95y1Kr"));
      ret.add(Pair.of("6VDK95y1Kr", "6W4zYX91LU"));
      ret.add(Pair.of("6W4zYX91LU", "6YtpqQwHA7"));
      ret.add(Pair.of("6YtpqQwHA7", "6ZRmw4n30A"));
      ret.add(Pair.of("6ZRmw4n30A", "61iBe4BBMb"));
      ret.add(Pair.of("61iBe4BBMb", "63ScLxZKcZ"));
      ret.add(Pair.of("63ScLxZKcZ", "65jiLS123x"));
      ret.add(Pair.of("65jiLS123x", "66JSgueIOX"));
      ret.add(Pair.of("66JSgueIOX", "675RpQzEh0"));
      ret.add(Pair.of("675RpQzEh0", "69x7NsgD5N"));
      ret.add(Pair.of("69x7NsgD5N", "7aWpfXO11U"));
      ret.add(Pair.of("7aWpfXO11U", "7cmolLb996"));
      ret.add(Pair.of("7cmolLb996", "7dLNioiFhP"));
      ret.add(Pair.of("7dLNioiFhP", "7fbvPipBQj"));
      ret.add(Pair.of("7fbvPipBQj", "7gAV88vJSV"));
      ret.add(Pair.of("7gAV88vJSV", "7h1ub8t7T6"));
      ret.add(Pair.of("7h1ub8t7T6", "7jpKvBa2PS"));
      ret.add(Pair.of("7jpKvBa2PS", "7kPyvimCaR"));
      ret.add(Pair.of("7kPyvimCaR", "7meUyPJ01S"));
      ret.add(Pair.of("7meUyPJ01S", "7nFan0xHjE"));
      ret.add(Pair.of("7nFan0xHjE", "7o5YcTIGlm"));
      ret.add(Pair.of("7o5YcTIGlm", "7q7vEgL37S"));
      ret.add(Pair.of("7q7vEgL37S", "7s4nScFED6"));
      ret.add(Pair.of("7s4nScFED6", "7uwCQzy1KZ"));
      ret.add(Pair.of("7uwCQzy1KZ", "7vWqJ8tHsF"));
      ret.add(Pair.of("7vWqJ8tHsF", "7xmWP2l6d7"));
      ret.add(Pair.of("7xmWP2l6d7", "7yOm6nzCGB"));
      ret.add(Pair.of("7yOm6nzCGB", "7AfrcQP4nn"));
      ret.add(Pair.of("7AfrcQP4nn", "7BGUTldE7n"));
      ret.add(Pair.of("7BGUTldE7n", "7C6m5PZ1FJ"));
      ret.add(Pair.of("7C6m5PZ1FJ", "7EEzPnwAzZ"));
      ret.add(Pair.of("7EEzPnwAzZ", "7HaG9zZEgm"));
      ret.add(Pair.of("7HaG9zZEgm", "7ICWOFW4y1"));
      ret.add(Pair.of("7ICWOFW4y1", "7J3EVmb34A"));
      ret.add(Pair.of("7J3EVmb34A", "7LtsLXm5zg"));
      ret.add(Pair.of("7LtsLXm5zg", "7MTuouA7GE"));
      ret.add(Pair.of("7MTuouA7GE", "7OjaWQi9ck"));
      ret.add(Pair.of("7OjaWQi9ck", "7PHSkWP30A"));
      ret.add(Pair.of("7PHSkWP30A", "7RaFBGj44S"));
      ret.add(Pair.of("7RaFBGj44S", "7SBcIpEDNc"));
      ret.add(Pair.of("7SBcIpEDNc", "7T4fOGD02u"));
      ret.add(Pair.of("7T4fOGD02u", "7Vu8T3J37S"));
      ret.add(Pair.of("7Vu8T3J37S", "7WWOni8Dq8"));
      ret.add(Pair.of("7WWOni8Dq8", "7ZzGwaQ7Ah"));
      ret.add(Pair.of("7ZzGwaQ7Ah", "700mlxj1tm"));
      ret.add(Pair.of("700mlxj1tm", "72pvR0L8kK"));
      ret.add(Pair.of("72pvR0L8kK", "73ODq8q4tA"));
      ret.add(Pair.of("73ODq8q4tA", "75fgGW92AW"));
      ret.add(Pair.of("75fgGW92AW", "76F6EtfLDp"));
      ret.add(Pair.of("76F6EtfLDp", "778jKnz2ex"));
      ret.add(Pair.of("778jKnz2ex", "79xuOlQCOl"));
      ret.add(Pair.of("79xuOlQCOl", "8aWoaKPGTf"));
      ret.add(Pair.of("8aWoaKPGTf", "8cmBbKD30A"));
      ret.add(Pair.of("8cmBbKD30A", "8dLcPmb9Vh"));
      ret.add(Pair.of("8dLcPmb9Vh", "8e9GZ3C00G"));
      ret.add(Pair.of("8e9GZ3C00G", "8gzqTX25AY"));
      ret.add(Pair.of("8gzqTX25AY", "8hZ1JDf84f"));
      ret.add(Pair.of("8hZ1JDf84f", "8jsfXQaAJs"));
      ret.add(Pair.of("8jsfXQaAJs", "8lq88wT9fL"));
      ret.add(Pair.of("8lq88wT9fL", "8nq9YrtCOq"));
      ret.add(Pair.of("8nq9YrtCOq", "8oTKgWLC1a"));
      ret.add(Pair.of("8oTKgWLC1a", "8qhIurL6d2"));
      ret.add(Pair.of("8qhIurL6d2", "8rHgmhT5vM"));
      ret.add(Pair.of("8rHgmhT5vM", "8s8jTN54cg"));
      ret.add(Pair.of("8s8jTN54cg", "8uy813h3oW"));
      ret.add(Pair.of("8uy813h3oW", "8v0gEEz2Ld"));
      ret.add(Pair.of("8v0gEEz2Ld", "8xoiZQs5eq"));
      ret.add(Pair.of("8xoiZQs5eq", "8yQmIla1Kr"));
      ret.add(Pair.of("8yQmIla1Kr", "8AiBVFR43S"));
      ret.add(Pair.of("8AiBVFR43S", "8B3hXIE5Ds"));
      ret.add(Pair.of("8B3hXIE5Ds", "8DuRtAL5ET"));
      ret.add(Pair.of("8DuRtAL5ET", "8EVapy9EfU"));
      ret.add(Pair.of("8EVapy9EfU", "8GmTc36JrX"));
      ret.add(Pair.of("8GmTc36JrX", "8HNOn4P9oN"));
      ret.add(Pair.of("8HNOn4P9oN", "8JdObZ343S"));
      ret.add(Pair.of("8JdObZ343S", "8KEoHM3G2i"));
      ret.add(Pair.of("8KEoHM3G2i", "8L4kY2m06Q"));
      ret.add(Pair.of("8L4kY2m06Q", "8NwysVkCtl"));
      ret.add(Pair.of("8NwysVkCtl", "8OW5jO530A"));
      ret.add(Pair.of("8OW5jO530A", "8QnBTEH30A"));
      ret.add(Pair.of("8QnBTEH30A", "8RUSMUsGmy"));
      ret.add(Pair.of("8RUSMUsGmy", "8UnO1vIBm2"));
      ret.add(Pair.of("8UnO1vIBm2", "8VNLevQ8dA"));
      ret.add(Pair.of("8VNLevQ8dA", "8Xd4kQm7zE"));
      ret.add(Pair.of("8Xd4kQm7zE", "8YC8gy4Mo2"));
      ret.add(Pair.of("8YC8gy4Mo2", "8Z2F8Wv33A"));
      ret.add(Pair.of("8Z2F8Wv33A", "81sVNeVDY1"));
      ret.add(Pair.of("81sVNeVDY1", "82SpNXlJXe"));
      ret.add(Pair.of("82SpNXlJXe", "84ip4iB7PN"));
      ret.add(Pair.of("84ip4iB7PN", "85H8U3GEh4"));
      ret.add(Pair.of("85H8U3GEh4", "869LKKL6mA"));
      ret.add(Pair.of("869LKKL6mA", "88zwytZ4az"));
      ret.add(Pair.of("88zwytZ4az", "9aQJJQ20Pd"));
      ret.add(Pair.of("9aQJJQ20Pd", "9cfWtYx5XE"));
      ret.add(Pair.of("9cfWtYx5XE", "9dEONy9HoC"));
      ret.add(Pair.of("9dEONy9HoC", "9gbbfAtGZR"));
      ret.add(Pair.of("9gbbfAtGZR", "9hECSRIEof"));
      ret.add(Pair.of("9hECSRIEof", "9i2EcQQ8by"));
      ret.add(Pair.of("9i2EcQQ8by", "9ksEJJSH4s"));
      ret.add(Pair.of("9ksEJJSH4s", "9lRBLHt44S"));
      ret.add(Pair.of("9lRBLHt44S", "9nhc72mAUp"));
      ret.add(Pair.of("9nhc72mAUp", "9oFNQeXBJI"));
      ret.add(Pair.of("9oFNQeXBJI", "9p5vLJB37S"));
      ret.add(Pair.of("9p5vLJB37S", "9rvTiE530A"));
      ret.add(Pair.of("9rvTiE530A", "9sVvebo9Uu"));
      ret.add(Pair.of("9sVvebo9Uu", "9ukA22H9Zx"));
      ret.add(Pair.of("9ukA22H9Zx", "9vJxCa9Akn"));
      ret.add(Pair.of("9vJxCa9Akn", "9w9qCkmMJZ"));
      ret.add(Pair.of("9w9qCkmMJZ", "9zJBKRSDnw"));
      ret.add(Pair.of("9zJBKRSDnw", "9Bcypg930A"));
      ret.add(Pair.of("9Bcypg930A", "9CEGSJu3Eu"));
      ret.add(Pair.of("9CEGSJu3Eu", "9D56SPZ37S"));
      ret.add(Pair.of("9D56SPZ37S", "9FwGegaCPl"));
      ret.add(Pair.of("9FwGegaCPl", "9G0SqcCEc5"));
      ret.add(Pair.of("9G0SqcCEc5", "9IrE6d8Krt"));
      ret.add(Pair.of("9IrE6d8Krt", "9JTth7IAnE"));
      ret.add(Pair.of("9JTth7IAnE", "9LmLNKs0N3"));
      ret.add(Pair.of("9LmLNKs0N3", "9NPDzG896v"));
      ret.add(Pair.of("9NPDzG896v", "9Pl3aSM0ow"));
      ret.add(Pair.of("9Pl3aSM0ow", "9QLe9c1BFV"));
      ret.add(Pair.of("9QLe9c1BFV", "9R9IyJ0EX4"));
      ret.add(Pair.of("9R9IyJ0EX4", "9TyQxWlC3m"));
      ret.add(Pair.of("9TyQxWlC3m", "9UZdYA29fm"));
      ret.add(Pair.of("9UZdYA29fm", "9WptePCHj9"));
      ret.add(Pair.of("9WptePCHj9", "9XPthuv1zr"));
      ret.add(Pair.of("9XPthuv1zr", "9ZeW5jLIpC"));
      ret.add(Pair.of("9ZeW5jLIpC", "90DSGfCDTW"));
      ret.add(Pair.of("90DSGfCDTW", "914XN2o2O2"));
      ret.add(Pair.of("914XN2o2O2", "937ODFB7x8"));
      ret.add(Pair.of("937ODFB7x8", "957yaVk9Vh"));
      ret.add(Pair.of("957yaVk9Vh", "97zNFNfIaC"));
      ret.add(Pair.of("97zNFNfIaC", "980Hzyo59o"));
      ret.add(Pair.of("980Hzyo59o", "9999999999"));

    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    /**
     * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
     * sensitive flag must be deleted.
     */
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected ReplicatedPersonReferrals normalizeSingle(List<EsPersonReferral> recs) {
    return normalize(recs).get(0);
  }

  @Override
  protected List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    List<ElasticSearchPersonReferral> esPersonReferrals = referrals.geReferrals();
    esp.setReferrals(esPersonReferrals);

    if (esPersonReferrals != null && !esPersonReferrals.isEmpty()) {
      try {
        buf.append(esPersonReferrals.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.error("ERROR SERIALIZING REFERRALS", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String updateJson = buf.toString();
    final String insertJson = mapper.writeValueAsString(esp);
    LOGGER.trace("insertJson: {}", insertJson);
    LOGGER.trace("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  public EsPersonReferral extract(ResultSet rs) throws SQLException {
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(ifNull(rs.getString("REFERRAL_ID")));
    referral.setClientId(ifNull(rs.getString("CLIENT_ID")));

    referral.setStartDate(rs.getDate("START_DATE"));
    referral.setEndDate(rs.getDate("END_DATE"));
    referral.setLastChange(rs.getDate("LAST_CHG"));
    referral.setCounty(rs.getInt("REFERRAL_COUNTY"));
    referral.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    referral.setReferralLastUpdated(rs.getTimestamp("REFERRAL_LAST_UPDATED"));

    referral.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    referral.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    referral.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    referral.setAllegationLastUpdated(rs.getTimestamp("ALLEGATION_LAST_UPDATED"));

    referral.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    referral.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    referral.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    referral.setPerpetratorLastUpdated(rs.getTimestamp("PERPETRATOR_LAST_UPDATED"));
    referral.setPerpetratorSensitivityIndicator(rs.getString("PERPETRATOR_SENSITIVITY_IND"));

    referral.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    referral.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    referral.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    referral.setReporterLastUpdated(rs.getTimestamp("REPORTER_LAST_UPDATED"));

    referral.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    referral.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    referral.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    referral.setVictimLastUpdated(rs.getTimestamp("VICTIM_LAST_UPDATED"));
    referral.setVictimSensitivityIndicator(rs.getString("VICTIM_SENSITIVITY_IND"));

    referral.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    referral.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    referral.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    referral.setWorkerLastUpdated(rs.getTimestamp("WORKER_LAST_UPDATED"));

    referral.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    referral.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    referral.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    referral.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return referral;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReferralHistoryIndexerJob.class, args);
  }
}
