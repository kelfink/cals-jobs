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
      "INSERT INTO #SCHEMA#.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "SELECT rc.FKREFERL_T, rc.FKCLIENT_T, rc.SENSTV_IND\n"
          + "FROM #SCHEMA#.VW_REFR_CLT rc\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  private AtomicInteger rowsRetrieved = new AtomicInteger(0);

  private AtomicInteger rowsExtracted = new AtomicInteger(0);

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
    buf.append(dbSchemaName);

    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" vw ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE vw.LIMITED_ACCESS_CODE = 'N'  ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString().replaceAll("\\s+", " ");
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
    LOGGER.debug("BEGIN");

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);

      final String sqlSelect = getInitialLoadQuery(getDBSchemaName());
      LOGGER.trace("SQL: {}", sqlSelect);
      enableParallelism(con);

      int cntr = 0;
      EsPersonReferral m;

      // Consider pre-allocating memory and reusing by means of object pool, thread local, etc.
      final List<EsPersonReferral> unsorted = new ArrayList<>(250000);

      try (
          PreparedStatement stmtInsert =
              con.prepareStatement(SQL_INSERT.replaceAll("#SCHEMA#", getDBSchemaName()));
          PreparedStatement stmtSelect = con.prepareStatement(sqlSelect)) {

        stmtInsert.setMaxRows(0);
        stmtInsert.setQueryTimeout(0);
        stmtInsert.setString(1, p.getLeft());
        stmtInsert.setString(2, p.getRight());

        final int clientReferralCount = stmtInsert.executeUpdate();
        LOGGER.debug("bundle client/referrals: {}", clientReferralCount);

        stmtSelect.setFetchSize(15000); // faster
        stmtSelect.setMaxRows(0);
        stmtSelect.setQueryTimeout(0);

        final ResultSet rs = stmtSelect.executeQuery(); // NOSONAR
        while (!fatalError && rs.next() && (m = extract(rs)) != null) {
          JobLogUtils.logEvery(++cntr, "retrieved", "bundle");
          JobLogUtils.logEvery(LOGGER, 50000, rowsRetrieved.incrementAndGet(), "Total read",
              "total read");
          unsorted.add(m);
        }

        con.commit();
      } finally {
        // Statement and connection close automatically.
      }

      unsorted.stream().sorted().collect(Collectors.groupingBy(EsPersonReferral::getClientId))
          .entrySet().stream().map(e -> normalizeSingle(e.getValue()))
          .forEach(this::addToIndexQueue);

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.info("DONE");
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");

    try {
      // This job normalizes **without** the transform thread.
      doneTransform = true;
      final List<ForkJoinTask<?>> tasks = new ArrayList<>();

      // Set thread pool size.
      final int cntReaderThreads = 1;
      // getOpts().getThreadCount() != 0L ? (int) getOpts().getThreadCount()
      // : Math.min(Math.min(Runtime.getRuntime().availableProcessors() - 2, 4), 2);
      ForkJoinPool forkJoinPool = new ForkJoinPool(cntReaderThreads);

      // Queue execution.
      for (Pair<String, String> p : getPartitionRanges()) {
        tasks.add(forkJoinPool.submit(() -> pullRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.join();
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      LOGGER.info("extracted {} ES referral rows", this.rowsExtracted.get());
      doneExtract = true;
    }

    LOGGER.info("DONE: main extract thread");
  }

  @Override
  protected boolean useTransformThread() {
    return false;
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return new ReferralJobRanges().getPartitionRanges(this);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    List<ElasticSearchPersonReferral> esPersonReferrals = referrals.getReferrals();
    esp.setReferrals(esPersonReferrals);
    // LOGGER.info("# of referrals: {}", esPersonReferrals.);

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
    JobLogUtils.logEvery(rowsExtracted.incrementAndGet(), "Extracted", "es person referrals");

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
