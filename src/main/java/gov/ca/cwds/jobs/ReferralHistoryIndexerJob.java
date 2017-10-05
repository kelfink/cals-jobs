package gov.ca.cwds.jobs;

import static gov.ca.cwds.jobs.util.transform.JobTransformUtils.ifNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.ibm.db2.jcc.DB2SystemMonitor;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.component.NeutronIntegerDefaults;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * <p>
 * Turn-around time for database objects is too long. Embed SQL in Java instead.
 * </p>
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral>
    implements JobResultSetAware<EsPersonReferral> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralHistoryIndexerJob.class);

  protected static final String INSERT_CLIENT_FULL =
      "INSERT INTO #SCHEMA#.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "\nSELECT rc.FKREFERL_T, rc.FKCLIENT_T, c.SENSTV_IND\nFROM #SCHEMA#.REFR_CLT rc\n"
          + "\nJOIN #SCHEMA#.CLIENT_T c on c.IDENTIFIER = rc.FKCLIENT_T\n"
          + "\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  protected static final String INSERT_CLIENT_LAST_CHG = "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)\n"
      + "WITH step1 AS (\nSELECT ALG.FKREFERL_T AS REFERRAL_ID\n"
      + " FROM #SCHEMA#.ALLGTN_T ALG  WHERE ALG.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step2 AS (\n"
      + " SELECT ALG.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.CLIENT_T C \n"
      + " JOIN #SCHEMA#.ALLGTN_T ALG ON (C.IDENTIFIER = ALG.FKCLIENT_0 OR C.IDENTIFIER = ALG.FKCLIENT_T)\n"
      + " WHERE C.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step3 AS (\n"
      + " SELECT RCT.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.REFR_CLT RCT \n"
      + " WHERE RCT.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step4 AS (\n"
      + " SELECT RFL.IDENTIFIER AS REFERRAL_ID FROM #SCHEMA#.REFERL_T RFL \n"
      + " WHERE RFL.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step5 AS (\n"
      + " SELECT RPT.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.REPTR_T RPT \n"
      + " WHERE RPT.IBMSNAP_LOGMARKER > ##TIMESTAMP##), hoard AS (\n"
      + " SELECT s1.REFERRAL_ID FROM STEP1 s1 UNION ALL\n"
      + " SELECT s2.REFERRAL_ID FROM STEP2 s2 UNION ALL\n"
      + " SELECT s3.REFERRAL_ID FROM STEP3 s3 UNION ALL\n"
      + " SELECT s4.REFERRAL_ID FROM STEP4 s4 UNION ALL\n"
      + " SELECT s5.REFERRAL_ID FROM STEP5 s5 )\n" + "SELECT DISTINCT g.REFERRAL_ID from hoard g ";

  protected static final String SELECT_CLIENT =
      "SELECT FKCLIENT_T, FKREFERL_T, SENSTV_IND FROM #SCHEMA#.GT_REFR_CLT RC";

  protected static final String SELECT_ALLEGATION = "SELECT \n"
      + " RC.FKREFERL_T         AS REFERRAL_ID," + " ALG.IDENTIFIER        AS ALLEGATION_ID,\n"
      + " ALG.ALG_DSPC          AS ALLEGATION_DISPOSITION,\n"
      + " ALG.ALG_TPC           AS ALLEGATION_TYPE,\n"
      + " ALG.LST_UPD_TS        AS ALLEGATION_LAST_UPDATED,\n"
      + " CLP.IDENTIFIER        AS PERPETRATOR_ID,\n"
      + " CLP.SENSTV_IND        AS PERPETRATOR_SENSITIVITY_IND,\n"
      + " TRIM(CLP.COM_FST_NM)  AS PERPETRATOR_FIRST_NM,\n"
      + " TRIM(CLP.COM_LST_NM)  AS PERPETRATOR_LAST_NM,\n"
      + " CLP.LST_UPD_TS        AS PERPETRATOR_LAST_UPDATED,\n"
      + " CLV.IDENTIFIER        AS VICTIM_ID,\n"
      + " CLV.SENSTV_IND        AS VICTIM_SENSITIVITY_IND,\n"
      + " TRIM(CLV.COM_FST_NM)  AS VICTIM_FIRST_NM," + " TRIM(CLV.COM_LST_NM)  AS VICTIM_LAST_NM,\n"
      + " CLV.LST_UPD_TS        AS VICTIM_LAST_UPDATED," + " CURRENT TIMESTAMP AS LAST_CHG \n"
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM #SCHEMA#.GT_REFR_CLT rc1) RC \n"
      + "JOIN #SCHEMA#.ALLGTN_T       ALG  ON ALG.FKREFERL_T = RC.FKREFERL_T \n"
      + "JOIN #SCHEMA#.CLIENT_T       CLV  ON CLV.IDENTIFIER = ALG.FKCLIENT_T \n"
      + "LEFT JOIN #SCHEMA#.CLIENT_T  CLP  ON CLP.IDENTIFIER = ALG.FKCLIENT_0 \n"
      + " FOR READ ONLY WITH UR ";

  protected static final String SELECT_REFERRAL = "SELECT RFL.IDENTIFIER        AS REFERRAL_ID,\n"
      + " RFL.REF_RCV_DT        AS START_DATE," + " RFL.REFCLSR_DT        AS END_DATE,\n"
      + " RFL.RFR_RSPC          AS REFERRAL_RESPONSE_TYPE,\n"
      + " RFL.LMT_ACSSCD        AS LIMITED_ACCESS_CODE,\n"
      + " RFL.LMT_ACS_DT        AS LIMITED_ACCESS_DATE,\n"
      + " TRIM(RFL.LMT_ACSDSC)  AS LIMITED_ACCESS_DESCRIPTION,\n"
      + " RFL.L_GVR_ENTC        AS LIMITED_ACCESS_GOVERNMENT_ENT,\n"
      + " RFL.LST_UPD_TS        AS REFERRAL_LAST_UPDATED,\n"
      + " TRIM(RPT.FKREFERL_T)  AS REPORTER_ID,\n"
      + " TRIM(RPT.RPTR_FSTNM)  AS REPORTER_FIRST_NM,\n"
      + " TRIM(RPT.RPTR_LSTNM)  AS REPORTER_LAST_NM,\n"
      + " RPT.LST_UPD_TS        AS REPORTER_LAST_UPDATED,\n"
      + " STP.IDENTIFIER        AS WORKER_ID,\n" + " TRIM(STP.FIRST_NM)    AS WORKER_FIRST_NM,\n"
      + " TRIM(STP.LAST_NM)     AS WORKER_LAST_NM,\n"
      + " STP.LST_UPD_TS        AS WORKER_LAST_UPDATED,\n"
      + " RFL.GVR_ENTC          AS REFERRAL_COUNTY,\n" + " CURRENT TIMESTAMP     AS LAST_CHG \n"
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM #SCHEMA#.GT_REFR_CLT rc1) RC \n"
      + "JOIN #SCHEMA#.REFERL_T          RFL  ON RFL.IDENTIFIER = RC.FKREFERL_T \n"
      + "LEFT JOIN #SCHEMA#.REPTR_T      RPT  ON RPT.FKREFERL_T = RFL.IDENTIFIER \n"
      + "LEFT JOIN #SCHEMA#.STFPERST     STP  ON RFL.FKSTFPERST = STP.IDENTIFIER ";

  /**
   * Allocate memory once for each thread and reuse per key range.
   * 
   * <p>
   * Note: <strong>use thread local variables sparingly</strong> because they stick to the thread.
   * This Neutron job reuses threads for performance, since thread creation is expensive.
   * </p>
   */
  protected transient ThreadLocal<List<EsPersonReferral>> allocAllegations = new ThreadLocal<>();

  protected transient ThreadLocal<Map<String, EsPersonReferral>> allocReferrals =
      new ThreadLocal<>();

  protected transient ThreadLocal<List<MinClientReferral>> allocClientReferralKeys =
      new ThreadLocal<>();

  protected transient ThreadLocal<List<EsPersonReferral>> allocReadyToNorm = new ThreadLocal<>();

  private final AtomicInteger rowsReadReferrals = new AtomicInteger(0);

  private final AtomicInteger rowsReadAllegations = new AtomicInteger(0);

  private final AtomicInteger nextThreadNum = new AtomicInteger(0);

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
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    EsPersonReferral.setOpts(getOpts()); // WARNING: change for continuous mode
    return EsPersonReferral.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_REFRL_ONLY";
  }

  @Override
  public String getJdbcOrderBy() {
    return ""; // sort manually since DB2 might not optimize the sort.
  }

  /**
   * @deprecated soon to be removed
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "REFERL_T";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    // Roll your own SQL. Turn-around on DB2 objects from other teams takes too long.

    final StringBuilder buf = new StringBuilder();
    buf.append(SELECT_REFERRAL.replaceAll("#SCHEMA#", dbSchemaName));

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE RFL.LMT_ACSSCD = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    final String ret = buf.toString().replaceAll("\\s+", " ");
    LOGGER.info("REFERRAL SQL: {}", ret);
    return ret;
  }

  /**
   * Synchronize grabbing connections from the connection pool to prevent deadlocks in C3P0.
   * 
   * @return a connection
   * @throws SQLException on database error
   * @throws InterruptedException on thread error
   */
  protected synchronized Connection getConnection() throws SQLException, InterruptedException {
    return jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection();
  }

  /**
   * Allocate memory once for this thread and reuse it per key range.
   */
  protected void allocateThreadMemory() {
    if (allocAllegations.get() == null) {
      allocAllegations.set(new ArrayList<>(150000));
      allocReadyToNorm.set(new ArrayList<>(150000));
      allocReferrals.set(new HashMap<>(99881)); // Prime
      allocClientReferralKeys.set(new ArrayList<>(12000));
    }
  }

  protected void readClients(final PreparedStatement stmtInsClient,
      final PreparedStatement stmtSelClient, final List<MinClientReferral> listClientReferralKeys,
      final Pair<String, String> p) throws SQLException {

    // Prepare client list.
    stmtInsClient.setMaxRows(0);
    stmtInsClient.setQueryTimeout(0);
    stmtInsClient.setString(1, p.getLeft());
    stmtInsClient.setString(2, p.getRight());

    final int cntInsClientReferral = stmtInsClient.executeUpdate();
    LOGGER.info("bundle client/referrals: {}", cntInsClientReferral);

    // Prepare retrieval.
    stmtSelClient.setMaxRows(0);
    stmtSelClient.setQueryTimeout(0);
    stmtSelClient.setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());

    LOGGER.info("pull client referral keys");
    final ResultSet rs = stmtSelClient.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next()) {
      listClientReferralKeys.add(MinClientReferral.extract(rs));
    }
  }

  protected void readReferrals(final PreparedStatement stmtSelReferral,
      final Map<String, EsPersonReferral> mapReferrals) throws SQLException {
    stmtSelReferral.setMaxRows(0);
    stmtSelReferral.setQueryTimeout(0);
    stmtSelReferral.setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull referrals");
    final ResultSet rs = stmtSelReferral.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = extractReferral(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "bundle referral");
      JobLogs.logEvery(LOGGER, 10000, rowsReadReferrals.incrementAndGet(), "Total read",
          "referrals");
      mapReferrals.put(m.getReferralId(), m);
    }
  }

  protected void readAllegations(final PreparedStatement stmtSelAllegation,
      final List<EsPersonReferral> listAllegations) throws SQLException {
    stmtSelAllegation.setMaxRows(0);
    stmtSelAllegation.setQueryTimeout(0);
    stmtSelAllegation.setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull allegations");
    final ResultSet rs = stmtSelAllegation.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = extractAllegation(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "bundle allegation");
      JobLogs.logEvery(LOGGER, 15000, rowsReadAllegations.incrementAndGet(), "Total read",
          "allegations");
      listAllegations.add(m);
    }
  }

  protected int normalizeQueryResults(final Map<String, EsPersonReferral> mapReferrals,
      final List<EsPersonReferral> listReadyToNorm,
      final Map<String, List<MinClientReferral>> mapReferralByClient,
      final Map<String, List<EsPersonReferral>> mapAllegationByReferral) {
    LOGGER.info("Normalize all: START");

    int cntr = 0;
    for (Map.Entry<String, List<MinClientReferral>> rc : mapReferralByClient.entrySet()) {
      // Loop referrals for this client:
      final String clientId = rc.getKey();
      if (StringUtils.isNotBlank(clientId)) {
        listReadyToNorm.clear();
        for (MinClientReferral rc1 : rc.getValue()) {
          final String referralId = rc1.referralId;
          final EsPersonReferral ref = mapReferrals.get(referralId);

          // Sealed and sensitive may be excluded.
          if (ref != null) {
            // Loop allegations for this referral:
            if (mapAllegationByReferral.containsKey(referralId)) {
              for (EsPersonReferral alg : mapAllegationByReferral.get(referralId)) {
                alg.mergeClientReferralInfo(clientId, ref);
                listReadyToNorm.add(alg);
              }
            } else {
              listReadyToNorm.add(ref);
            }
          }
        }

        final ReplicatedPersonReferrals repl = normalizeSingle(listReadyToNorm);
        if (repl != null) {
          ++cntr;
          repl.setClientId(clientId);
          addToIndexQueue(repl);
        }
      }
    }

    LOGGER.info("Normalize all: END");
    return cntr;
  }

  /**
   * Read records from a single partition. Then sort results on our own.
   * 
   * <p>
   * Each call of this method may run in its own thread.
   * </p>
   * 
   * @param p partition range to read
   * @return number of client documents affected
   */
  protected int pullRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.info("BEGIN");

    allocateThreadMemory();
    final List<EsPersonReferral> listAllegations = allocAllegations.get();
    final Map<String, EsPersonReferral> mapReferrals = allocReferrals.get();
    final List<MinClientReferral> listClientReferralKeys = allocClientReferralKeys.get();
    final List<EsPersonReferral> listReadyToNorm = allocReadyToNorm.get();

    // Clear collections, for safety.
    releaseLocalMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);

    try (final Connection con = getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      JobDB2Utils.enableParallelism(con);

      final DB2SystemMonitor monitor = JobDB2Utils.monitorStart(con);
      final String schema = getDBSchemaName();

      try (
          final PreparedStatement stmtInsClient = con.prepareStatement(
              INSERT_CLIENT_FULL.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelClient = con.prepareStatement(
              SELECT_CLIENT.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelReferral =
              con.prepareStatement(getInitialLoadQuery(schema).replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelAllegation = con.prepareStatement(
              SELECT_ALLEGATION.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim())) {

        readClients(stmtInsClient, stmtSelClient, listClientReferralKeys, p);
        readReferrals(stmtSelReferral, mapReferrals);
        readAllegations(stmtSelAllegation, listAllegations);

        JobDB2Utils.monitorStopAndReport(monitor);
        con.commit();

      } finally {
        // The statements and result sets close automatically.
      }

    } catch (Exception e) {
      markFailed();
      JobLogs.raiseError(LOGGER, e, "ERROR HANDING RANGE {} - {}: {}", p.getLeft(), p.getRight(),
          e.getMessage());
    }

    int cntr = 0;
    try {
      final Map<String, List<MinClientReferral>> mapReferralByClient = listClientReferralKeys
          .stream().sorted((e1, e2) -> e1.getClientId().compareTo(e2.getClientId()))
          .collect(Collectors.groupingBy(MinClientReferral::getClientId));
      listClientReferralKeys.clear();

      final Map<String, List<EsPersonReferral>> mapAllegationByReferral = listAllegations.stream()
          .sorted((e1, e2) -> e1.getReferralId().compareTo(e2.getReferralId()))
          .collect(Collectors.groupingBy(EsPersonReferral::getReferralId));
      listAllegations.clear();

      // For each client group:
      cntr = normalizeQueryResults(mapReferrals, listReadyToNorm, mapReferralByClient,
          mapAllegationByReferral);
    } finally {
      // Release heap objects early and often.
      releaseLocalMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);

      // Good time to *request* garbage collection, not *demand* it. GC runs in another thread.
      // SonarQube disagrees.
      // The catch: when many threads run, parallel GC may not get sufficient CPU cycles, until heap
      // memory is exhausted. Yes, this is a good place to drop a hint to GC that it *might* want to
      // clean up memory.
      System.gc(); // NOSONAR
    }

    LOGGER.info("DONE");
    return cntr;
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadRetrieveByJdbc() {
    Thread.currentThread().setName("read_main");
    LOGGER.info("BEGIN: main read thread");
    EsPersonReferral.setOpts(getOpts()); // NOTE: ok for one-shot JVM but not ok in continuous mode

    try {
      // This job normalizes **without** the transform thread.
      markTransformDone();

      // Init task list.
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.warn(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
      final List<ForkJoinTask<?>> tasks = new ArrayList<>(ranges.size());
      final ForkJoinPool threadPool = new ForkJoinPool(JobJdbcUtils.calcReaderThreads(getOpts()));

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        tasks.add(threadPool.submit(() -> pullRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.get();
      }

      LOGGER.info("read {} ES referral rows", this.rowsReadReferrals.get());

    } catch (Exception e) {
      markFailed();
      JobLogs.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      markRetrieveDone();
    }

    LOGGER.info("DONE: main read thread");
  }

  @Override
  public boolean useTransformThread() {
    return false;
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public boolean providesInitialKeyRanges() {
    return true;
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
  public List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return new ReferralJobRanges().getPartitionRanges(this);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    final List<ElasticSearchPersonReferral> esPersonReferrals = referrals.getReferrals();
    esp.setReferrals(esPersonReferrals);

    if (esPersonReferrals != null && !esPersonReferrals.isEmpty()) {
      buf.append(esPersonReferrals.stream().map(ElasticTransformer::jsonify)
          .sorted(String::compareTo).collect(Collectors.joining(",")));
    }

    buf.append("]}");

    final String updateJson = buf.toString();
    final String insertJson = mapper.writeValueAsString(esp);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  /**
   * IBM strongly recommends retrieving column results by position, not by column name.
   * 
   * @param rs referral result set
   * @return parent referral element
   * @throws SQLException on DB error
   */
  protected EsPersonReferral extractReferral(final ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    int columnIndex = 0;
    ret.setReferralId(rs.getString(++columnIndex));
    ret.setStartDate(rs.getDate(++columnIndex));
    ret.setEndDate(rs.getDate(++columnIndex));
    ret.setReferralResponseType(rs.getInt(++columnIndex));

    ret.setLimitedAccessCode(ifNull(rs.getString(++columnIndex)));
    ret.setLimitedAccessDate(rs.getDate(++columnIndex));
    ret.setLimitedAccessDescription(ifNull(rs.getString(++columnIndex)));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt(++columnIndex));
    ret.setReferralLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setReporterId(ifNull(rs.getString(++columnIndex)));
    ret.setReporterFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setReporterLastName(ifNull(rs.getString(++columnIndex)));
    ret.setReporterLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setWorkerId(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerLastName(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setCounty(rs.getInt(++columnIndex));
    ret.setLastChange(rs.getDate(++columnIndex));

    return ret;
  }

  /**
   * @param rs allegation result set
   * @return allegation side of referral
   * @throws SQLException database error
   */
  protected EsPersonReferral extractAllegation(final ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    int columnIndex = 0;
    ret.setReferralId(ifNull(rs.getString(++columnIndex)));
    ret.setAllegationId(ifNull(rs.getString(++columnIndex)));
    ret.setAllegationDisposition(rs.getInt(++columnIndex));
    ret.setAllegationType(rs.getInt(++columnIndex));
    ret.setAllegationLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setPerpetratorId(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorSensitivityIndicator(rs.getString(++columnIndex));
    ret.setPerpetratorFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorLastName(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setVictimId(ifNull(rs.getString(++columnIndex)));
    ret.setVictimSensitivityIndicator(rs.getString(++columnIndex));
    ret.setVictimFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setVictimLastName(ifNull(rs.getString(++columnIndex)));
    ret.setVictimLastUpdated(rs.getTimestamp(++columnIndex));

    return ret;
  }

  @Override
  public EsPersonReferral extract(final ResultSet rs) throws SQLException {
    return new EsPersonReferral(rs);
  }

  private void releaseLocalMemory(final List<EsPersonReferral> listAllegations,
      final Map<String, EsPersonReferral> mapReferrals,
      final List<MinClientReferral> listClientReferralKeys,
      final List<EsPersonReferral> listReadyToNorm) {
    listAllegations.clear();
    listClientReferralKeys.clear();
    listReadyToNorm.clear();
    mapReferrals.clear();
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(ReferralHistoryIndexerJob.class, args);
  }

}
