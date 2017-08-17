package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    // return " ORDER BY CLIENT_ID ";
    return " "; // sort manually. database won't optimize the sort.
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
    buf.append(" vw WHERE vw.CLIENT_ID > ? AND vw.CLIENT_ID <= ? ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" AND vw.LIMITED_ACCESS_CODE = 'N'  ");
    }

    buf.append(getJdbcOrderBy()).append(" OPTIMIZE FOR 1000000 ROWS FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Hand off all recs for same client id at same time.
   * 
   * @param grpRecs recs for same client id
   */
  protected void handOff(List<EsPersonReferral> grpRecs) {
    try {
      lock.readLock().unlock();
      lock.writeLock().lock();
      for (EsPersonReferral t : grpRecs) {
        LOGGER.trace("lock: queueTransform.putLast: client id {}", t.getClientId());
        queueTransform.putLast(t);
      }
      lock.readLock().lock();
    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.warn("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    } finally {
      lock.writeLock().unlock();
    }
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
  protected void extractPartitionRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.warn("BEGIN: extract thread {}", threadName);

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);

      final String sql = getInitialLoadQuery(getDBSchemaName()).replaceAll("\\s+", " ")
      // .replaceAll(":fromId", p.getLeft()).replaceAll(":toId", p.getRight())
      ;

      LOGGER.warn("SQL: {}", sql);
      enableParallelism(con);

      int cntr = 0;
      EsPersonReferral m;
      final List<EsPersonReferral> unsorted = new ArrayList<>(275000);

      try (PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(0);
        stmt.setString(1, p.getLeft());
        stmt.setString(2, p.getRight());

        final ResultSet rs = stmt.executeQuery(); // NOSONAR
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
      getPartitionRanges().stream().sequential().forEach(this::extractPartitionRange);
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

      // ret.add(Pair.of("Daaaaaaaaa", "DZZZZZZZZZ"));

      ret.add(Pair.of("aaaaaaaaaa", "AmtsRRw21w"));
      ret.add(Pair.of("AmtsRRw21w", "AzV0bnX3oq"));
      ret.add(Pair.of("AzV0bnX3oq", "ANoJt9tA15"));
      ret.add(Pair.of("ANoJt9tA15", "A0vPvc137S"));
      ret.add(Pair.of("A0vPvc137S", "BcZfUvvCVp"));
      ret.add(Pair.of("BcZfUvvCVp", "BqndGffI4c"));
      ret.add(Pair.of("BqndGffI4c", "BDHioFmCHH"));
      ret.add(Pair.of("BDHioFmCHH", "BP0LEsU191"));
      ret.add(Pair.of("BP0LEsU191", "B3wbhK99Bm"));
      ret.add(Pair.of("B3wbhK99Bm", "CfOGDDd37S"));
      ret.add(Pair.of("CfOGDDd37S", "Cs4Dq6537S"));
      ret.add(Pair.of("Cs4Dq6537S", "CF6badH2OJ"));
      ret.add(Pair.of("CF6badH2OJ", "CTFnbjb8p7"));
      ret.add(Pair.of("CTFnbjb8p7", "C68I5XW5ig"));
      ret.add(Pair.of("C68I5XW5ig", "DkvMcpF37S"));
      ret.add(Pair.of("DkvMcpF37S", "DxN9xUf37S"));
      ret.add(Pair.of("DxN9xUf37S", "DLoinonJPJ"));
      ret.add(Pair.of("DLoinonJPJ", "DXZt3aC2k5"));
      ret.add(Pair.of("DXZt3aC2k5", "EaImGhN37S"));
      ret.add(Pair.of("EaImGhN37S", "EmY38W690b"));
      ret.add(Pair.of("EmY38W690b", "EAqWXtPFjv"));
      ret.add(Pair.of("EAqWXtPFjv", "EN6OPma2Kc"));
      ret.add(Pair.of("EN6OPma2Kc", "E1HCUivAOM"));
      ret.add(Pair.of("E1HCUivAOM", "FdTPA5YCn9"));
      ret.add(Pair.of("FdTPA5YCn9", "FrltE3O10S"));
      ret.add(Pair.of("FrltE3O10S", "FDMEeqbF4m"));
      ret.add(Pair.of("FDMEeqbF4m", "FQVIcvR40S"));
      ret.add(Pair.of("FQVIcvR40S", "F3u5G1zIfw"));
      ret.add(Pair.of("F3u5G1zIfw", "GidWquI10S"));
      ret.add(Pair.of("GidWquI10S", "GuLYPFa2OJ"));
      ret.add(Pair.of("GuLYPFa2OJ", "GHVOG4c2Nd"));
      ret.add(Pair.of("GHVOG4c2Nd", "GVohhSE74E"));
      ret.add(Pair.of("GVohhSE74E", "G8DSoi136B"));
      ret.add(Pair.of("G8DSoi136B", "Hk5RXG541S"));
      ret.add(Pair.of("Hk5RXG541S", "HyFdYEDKE8"));
      ret.add(Pair.of("HyFdYEDKE8", "HL7ZxpZ36B"));
      ret.add(Pair.of("HL7ZxpZ36B", "HZxxwmX30A"));
      ret.add(Pair.of("HZxxwmX30A", "IbCLj326ob"));
      ret.add(Pair.of("IbCLj326ob", "Ipkpr4j43S"));
      ret.add(Pair.of("Ipkpr4j43S", "IBuZMMj34A"));
      ret.add(Pair.of("IBuZMMj34A", "INvgOo534A"));
      ret.add(Pair.of("INvgOo534A", "I1cHEf34qv"));
      ret.add(Pair.of("I1cHEf34qv", "JfJBxri8I2"));
      ret.add(Pair.of("JfJBxri8I2", "JrYWv1C8Sn"));
      ret.add(Pair.of("JrYWv1C8Sn", "JFpen7R44S"));
      ret.add(Pair.of("JFpen7R44S", "JSL3Kdh9GW"));
      ret.add(Pair.of("JSL3Kdh9GW", "J57aal9FB1"));
      ret.add(Pair.of("J57aal9FB1", "Kis1cfCA2T"));
      ret.add(Pair.of("Kis1cfCA2T", "KvY3KTc5Je"));
      ret.add(Pair.of("KvY3KTc5Je", "KJxVG3RBYj"));
      ret.add(Pair.of("KJxVG3RBYj", "KVy5uiY0SL"));
      ret.add(Pair.of("KVy5uiY0SL", "K84G8Qr197"));
      ret.add(Pair.of("K84G8Qr197", "Lme8s0LETd"));
      ret.add(Pair.of("Lme8s0LETd", "LyRJTCt7vm"));
      ret.add(Pair.of("LyRJTCt7vm", "LLJTeOF7as"));
      ret.add(Pair.of("LLJTeOF7as", "LYU5nrH1nH"));
      ret.add(Pair.of("LYU5nrH1nH", "MbLljCH2vk"));
      ret.add(Pair.of("MbLljCH2vk", "Mo6k5wH30A"));
      ret.add(Pair.of("Mo6k5wH30A", "MCnhhOs4lH"));
      ret.add(Pair.of("MCnhhOs4lH", "MPNWoBjCuS"));
      ret.add(Pair.of("MPNWoBjCuS", "M18m00E94v"));
      ret.add(Pair.of("M18m00E94v", "NgePlxg83S"));
      ret.add(Pair.of("NgePlxg83S", "Ns5YRRh40S"));
      ret.add(Pair.of("Ns5YRRh40S", "NFFmAnRBYK"));
      ret.add(Pair.of("NFFmAnRBYK", "NS5BQ7WCOQ"));
      ret.add(Pair.of("NS5BQ7WCOQ", "N5TIShj9z5"));
      ret.add(Pair.of("N5TIShj9z5", "Oh0VRYH33A"));
      ret.add(Pair.of("Oh0VRYH33A", "Ovk8qbU5E6"));
      ret.add(Pair.of("Ovk8qbU5E6", "OIQFpqN0Qg"));
      ret.add(Pair.of("OIQFpqN0Qg", "OWdrVLNKE8"));
      ret.add(Pair.of("OWdrVLNKE8", "O8lGxja8iJ"));
      ret.add(Pair.of("O8lGxja8iJ", "PlEAnVh01S"));
      ret.add(Pair.of("PlEAnVh01S", "PxENs2FBng"));
      ret.add(Pair.of("PxENs2FBng", "PKwExIqCtl"));
      ret.add(Pair.of("PKwExIqCtl", "PX0Wnti10S"));
      ret.add(Pair.of("PX0Wnti10S", "Qcyq1lc9Vh"));
      ret.add(Pair.of("Qcyq1lc9Vh", "QoVLW5Z01S"));
      ret.add(Pair.of("QoVLW5Z01S", "QCo2gNQ9j8"));
      ret.add(Pair.of("QCo2gNQ9j8", "QPXWXIP3ss"));
      ret.add(Pair.of("QPXWXIP3ss", "Q23qeWp4kv"));
      ret.add(Pair.of("Q23qeWp4kv", "RflMe3j5uj"));
      ret.add(Pair.of("RflMe3j5uj", "RsN3mp0DNc"));
      ret.add(Pair.of("RsN3mp0DNc", "RFWcRoo9xb"));
      ret.add(Pair.of("RFWcRoo9xb", "RSay9hq2Nl"));
      ret.add(Pair.of("RSay9hq2Nl", "R5wEVAh4nn"));
      ret.add(Pair.of("R5wEVAh4nn", "ShHlJai3dA"));
      ret.add(Pair.of("ShHlJai3dA", "SuUjfHf4tA"));
      ret.add(Pair.of("SuUjfHf4tA", "SG0eor307S"));
      ret.add(Pair.of("SG0eor307S", "SUwrIzR37S"));
      ret.add(Pair.of("SUwrIzR37S", "S6SDvaqAzf"));
      ret.add(Pair.of("S6SDvaqAzf", "TlgHrGr41S"));
      ret.add(Pair.of("TlgHrGr41S", "Tyww67H0ZX"));
      ret.add(Pair.of("Tyww67H0ZX", "TLNU80dBw8"));
      ret.add(Pair.of("TLNU80dBw8", "TYfo65Q5iN"));
      ret.add(Pair.of("TYfo65Q5iN", "UBvtsR810S"));
      ret.add(Pair.of("UBvtsR810S", "U9FXvkH36B"));
      ret.add(Pair.of("U9FXvkH36B", "0nxZPpi5D4"));
      ret.add(Pair.of("0nxZPpi5D4", "0BaKWm65qq"));
      ret.add(Pair.of("0BaKWm65qq", "0OL6amP7PC"));
      ret.add(Pair.of("0OL6amP7PC", "01fAGnjEou"));
      ret.add(Pair.of("01fAGnjEou", "ZZZZZZZZZZ"));

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
