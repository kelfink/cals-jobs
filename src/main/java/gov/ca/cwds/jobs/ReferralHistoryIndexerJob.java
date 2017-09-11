package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.ibm.db2.jcc.DB2Connection;
import com.ibm.db2.jcc.DB2SystemMonitor;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiMarker;
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

  // Database turn-around time is too long. Roll your own.

  /**
   * Common timestamp format for legacy DB.
   */
  public static final String LEGACY_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  private static final String INSERT_CLIENT_FULL =
      "INSERT INTO #SCHEMA#.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)"
          + "\nSELECT rc.FKREFERL_T, rc.FKCLIENT_T, c.SENSTV_IND\nFROM #SCHEMA#.REFR_CLT rc"
          + "\nJOIN #SCHEMA#.CLIENT_T c on c.IDENTIFIER = rc.FKCLIENT_T"
          + "\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  private static final String INSERT_CLIENT_LAST_CHG = "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)\n"
      + "WITH step1 AS (\nSELECT ALG.FKREFERL_T AS REFERRAL_ID "
      + " FROM #SCHEMA#.ALLGTN_T ALG  WHERE ALG.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step2 AS (\n"
      + " SELECT ALG.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.CLIENT_T C  "
      + " JOIN #SCHEMA#.ALLGTN_T ALG ON (C.IDENTIFIER = ALG.FKCLIENT_0 OR C.IDENTIFIER = ALG.FKCLIENT_T) "
      + " WHERE C.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step3 AS (\n"
      + " SELECT RCT.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.REFR_CLT RCT "
      + " WHERE RCT.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step4 AS (\n"
      + " SELECT RFL.IDENTIFIER AS REFERRAL_ID FROM #SCHEMA#.REFERL_T RFL "
      + " WHERE RFL.IBMSNAP_LOGMARKER > ##TIMESTAMP##), step5 AS (\n"
      + " SELECT RPT.FKREFERL_T AS REFERRAL_ID FROM #SCHEMA#.REPTR_T RPT "
      + " WHERE RPT.IBMSNAP_LOGMARKER > ##TIMESTAMP##), hoard AS (\n"
      + " SELECT s1.REFERRAL_ID FROM STEP1 s1 UNION ALL\n"
      + " SELECT s2.REFERRAL_ID FROM STEP2 s2 UNION ALL\n"
      + " SELECT s3.REFERRAL_ID FROM STEP3 s3 UNION ALL\n"
      + " SELECT s4.REFERRAL_ID FROM STEP4 s4 UNION ALL\n"
      + " SELECT s5.REFERRAL_ID FROM STEP5 s5 )\n" + "SELECT DISTINCT g.REFERRAL_ID from hoard g ";

  private static final String SELECT_CLIENT =
      "SELECT FKCLIENT_T, FKREFERL_T, SENSTV_IND FROM #SCHEMA#.GT_REFR_CLT RC";

  private static final String SELECT_ALLEGATION = "SELECT "
      + " RC.FKREFERL_T         AS REFERRAL_ID," + " ALG.IDENTIFIER        AS ALLEGATION_ID,"
      + " ALG.ALG_DSPC          AS ALLEGATION_DISPOSITION,"
      + " ALG.ALG_TPC           AS ALLEGATION_TYPE,"
      + " ALG.LST_UPD_TS        AS ALLEGATION_LAST_UPDATED,"
      + " CLP.IDENTIFIER        AS PERPETRATOR_ID,"
      + " CLP.SENSTV_IND        AS PERPETRATOR_SENSITIVITY_IND,"
      + " TRIM(CLP.COM_FST_NM)  AS PERPETRATOR_FIRST_NM,"
      + " TRIM(CLP.COM_LST_NM)  AS PERPETRATOR_LAST_NM,"
      + " CLP.LST_UPD_TS        AS PERPETRATOR_LAST_UPDATED,"
      + " CLV.IDENTIFIER        AS VICTIM_ID," + " CLV.SENSTV_IND        AS VICTIM_SENSITIVITY_IND,"
      + " TRIM(CLV.COM_FST_NM)  AS VICTIM_FIRST_NM," + " TRIM(CLV.COM_LST_NM)  AS VICTIM_LAST_NM,"
      + " CLV.LST_UPD_TS        AS VICTIM_LAST_UPDATED," + " CURRENT TIMESTAMP AS LAST_CHG "
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM #SCHEMA#.GT_REFR_CLT rc1) RC "
      + "JOIN #SCHEMA#.ALLGTN_T       ALG  ON ALG.FKREFERL_T = RC.FKREFERL_T "
      + "JOIN #SCHEMA#.CLIENT_T       CLV  ON CLV.IDENTIFIER = ALG.FKCLIENT_T "
      + "LEFT JOIN #SCHEMA#.CLIENT_T  CLP  ON CLP.IDENTIFIER = ALG.FKCLIENT_0 "
      + " FOR READ ONLY WITH UR ";

  private static final String SELECT_REFERRAL = "SELECT " + " RFL.IDENTIFIER        AS REFERRAL_ID,"
      + " RFL.REF_RCV_DT        AS START_DATE," + " RFL.REFCLSR_DT        AS END_DATE,"
      + " RFL.RFR_RSPC          AS REFERRAL_RESPONSE_TYPE,"
      + " RFL.LMT_ACSSCD        AS LIMITED_ACCESS_CODE,"
      + " RFL.LMT_ACS_DT        AS LIMITED_ACCESS_DATE,"
      + " TRIM(RFL.LMT_ACSDSC)  AS LIMITED_ACCESS_DESCRIPTION,"
      + " RFL.L_GVR_ENTC        AS LIMITED_ACCESS_GOVERNMENT_ENT,"
      + " RFL.LST_UPD_TS        AS REFERRAL_LAST_UPDATED,"
      + " RPT.FKREFERL_T        AS REPORTER_ID," + " TRIM(RPT.RPTR_FSTNM)  AS REPORTER_FIRST_NM,"
      + " TRIM(RPT.RPTR_LSTNM)  AS REPORTER_LAST_NM,"
      + " RPT.LST_UPD_TS        AS REPORTER_LAST_UPDATED," + " STP.IDENTIFIER        AS WORKER_ID,"
      + " TRIM(STP.FIRST_NM)    AS WORKER_FIRST_NM," + " TRIM(STP.LAST_NM)     AS WORKER_LAST_NM,"
      + " STP.LST_UPD_TS        AS WORKER_LAST_UPDATED,"
      + " RFL.GVR_ENTC          AS REFERRAL_COUNTY," + " CURRENT TIMESTAMP     AS LAST_CHG "
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM #SCHEMA#.GT_REFR_CLT rc1) RC "
      + "JOIN #SCHEMA#.REFERL_T          RFL  ON RFL.IDENTIFIER = RC.FKREFERL_T "
      + "LEFT JOIN #SCHEMA#.REPTR_T      RPT  ON RPT.FKREFERL_T = RFL.IDENTIFIER "
      + "LEFT JOIN #SCHEMA#.STFPERST     STP  ON RFL.FKSTFPERST = STP.IDENTIFIER ";

  private static final int FETCH_SIZE = 5000;

  /**
   * Carrier bean for client referral keys.
   * 
   * @author CWDS API Team
   */
  public static class MinClientReferral implements ApiMarker {
    String clientId;
    String referralId;
    String sensitivity;

    public MinClientReferral(String clientId, String referralId, String sensitivity) {
      this.clientId = clientId;
      this.referralId = referralId;
      this.sensitivity = sensitivity;
    }

    public static MinClientReferral extract(ResultSet rs) throws SQLException {
      return new MinClientReferral(rs.getString("FKCLIENT_T"), rs.getString("FKREFERL_T"),
          rs.getString("SENSTV_IND"));
    }

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getReferralId() {
      return referralId;
    }

    public void setReferralId(String referralId) {
      this.referralId = referralId;
    }

    public String getSensitivity() {
      return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
      this.sensitivity = sensitivity;
    }
  }

  /**
   * Allocate memory once for each thread and reuse per key range.
   * 
   * <p>
   * Note: <strong>use thread local variables sparingly</strong> because they stick to the thread.
   * This Neutron job reuses threads for performance, since thread creation is expensive.
   * </p>
   */
  private final ThreadLocal<List<EsPersonReferral>> allocAllegations = new ThreadLocal<>();

  private final ThreadLocal<Map<String, EsPersonReferral>> allocReferrals = new ThreadLocal<>();

  private final ThreadLocal<List<MinClientReferral>> allocClientReferralKeys = new ThreadLocal<>();

  private final ThreadLocal<List<EsPersonReferral>> allocReadyToNorm = new ThreadLocal<>();

  private AtomicInteger rowsReadReferrals = new AtomicInteger(0);

  private AtomicInteger rowsReadAllegations = new AtomicInteger(0);

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
    EsPersonReferral.setOpts(getOpts());
    return EsPersonReferral.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_REFRL_ONLY";
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
    // Roll your own. Can't wait for DBA turn-around.
    StringBuilder buf = new StringBuilder();
    // buf.append("SELECT vw.* FROM ");
    // buf.append(dbSchemaName);
    // buf.append(".");
    // buf.append(getInitialLoadViewName());
    // buf.append(" vw ");

    buf.append(SELECT_REFERRAL.replaceAll("#SCHEMA#", dbSchemaName));

    if (!getOpts().isLoadSealedAndSensitive()) {
      // buf.append(" WHERE LIMITED_ACCESS_CODE = 'N' ");
      buf.append(" WHERE RFL.LMT_ACSSCD = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    final String ret = buf.toString().replaceAll("\\s+", " ");
    LOGGER.warn("REFERRAL SQL: {}", ret);
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
      allocReferrals.set(new HashMap<>(99881));
      allocClientReferralKeys.set(new ArrayList<>(12000));
    }
  }

  /**
   * Stop the DB2 monitor and report stats.
   * 
   * @param monitor current monitor instance
   * @throws SQLException on JDBC error
   */
  private void monitorStopAndReport(final DB2SystemMonitor monitor) throws SQLException {
    monitor.stop();
    LOGGER.info("Server elapsed time (microseconds)=" + monitor.getServerTimeMicros());
    LOGGER.info("Network I/O elapsed time (microseconds)=" + monitor.getNetworkIOTimeMicros());
    LOGGER.info("Core driver elapsed time (microseconds)=" + monitor.getCoreDriverTimeMicros());
    LOGGER.info("Application elapsed time (milliseconds)=" + monitor.getApplicationTimeMillis());
    LOGGER.info("monitor.moreData: 0: {}", monitor.moreData(0));
    LOGGER.info("monitor.moreData: 1: {}", monitor.moreData(1));
    LOGGER.info("monitor.moreData: 2: {}", monitor.moreData(2));

    // C'mon IBM. Where are the constants for method DB2SystemMonitor.moreData()??
    // LOGGER.info("NETWORK_TRIPS: {}", monitor.moreData(NUMBER_NETWORK_TRIPS));
  }

  private void readClients(final PreparedStatement stmtInsClient,
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
    stmtSelClient.setFetchSize(FETCH_SIZE);

    {
      LOGGER.info("pull client referral keys");
      final ResultSet rs = stmtSelClient.executeQuery(); // NOSONAR
      MinClientReferral mx;
      while (!fatalError && rs.next() && (mx = MinClientReferral.extract(rs)) != null) {
        listClientReferralKeys.add(mx);
      }
    }
  }

  private void readReferrals(final PreparedStatement stmtSelReferral,
      final Map<String, EsPersonReferral> mapReferrals) throws SQLException {
    stmtSelReferral.setMaxRows(0);
    stmtSelReferral.setQueryTimeout(0);
    stmtSelReferral.setFetchSize(FETCH_SIZE);

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull referrals");
    final ResultSet rs = stmtSelReferral.executeQuery(); // NOSONAR
    while (!fatalError && rs.next() && (m = extractReferral(rs)) != null) {
      JobLogUtils.logEvery(++cntr, "read", "bundle referral");
      JobLogUtils.logEvery(LOGGER, 10000, rowsReadReferrals.incrementAndGet(), "Total read",
          "referrals");
      mapReferrals.put(m.getReferralId(), m);
    }
  }

  private void readAllegations(final PreparedStatement stmtSelAllegation,
      final List<EsPersonReferral> listAllegations) throws SQLException {
    stmtSelAllegation.setMaxRows(0);
    stmtSelAllegation.setQueryTimeout(0);
    stmtSelAllegation.setFetchSize(FETCH_SIZE);

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull allegations");
    final ResultSet rs = stmtSelAllegation.executeQuery(); // NOSONAR
    while (!fatalError && rs.next() && (m = extractAllegation(rs)) != null) {
      JobLogUtils.logEvery(++cntr, "read", "bundle allegation");
      JobLogUtils.logEvery(LOGGER, 15000, rowsReadAllegations.incrementAndGet(), "Total read",
          "allegations");
      listAllegations.add(m);
    }
  }

  private DB2SystemMonitor monitorStart(final Connection con)
      throws SQLException, ClassNotFoundException {
    final com.ibm.db2.jcc.t4.b nativeCon =
        (com.ibm.db2.jcc.t4.b) ((com.mchange.v2.c3p0.impl.NewProxyConnection) con)
            .unwrap(Class.forName("com.ibm.db2.jcc.t4.b"));
    final DB2Connection db2Con = nativeCon;
    LOGGER.info("sendDataAsIs_: {}, enableRowsetSupport_: {}", nativeCon.sendDataAsIs_,
        nativeCon.enableRowsetSupport_);

    final DB2SystemMonitor monitor = db2Con.getDB2SystemMonitor();
    monitor.enable(true);
    monitor.start(DB2SystemMonitor.RESET_TIMES);
    return monitor;
  }

  private int normalizeQueryResults(final Map<String, EsPersonReferral> mapReferrals,
      final List<EsPersonReferral> listReadyToNorm,
      final Map<String, List<MinClientReferral>> mapReferralByClient,
      final Map<String, List<EsPersonReferral>> mapAllegationByReferral) {
    LOGGER.info("Normalize all: START");

    // TODO: convert to stream instead of nested for loops.
    // unsorted.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential()
    // listReferrals.stream().sorted().collect(Collectors.groupingBy(EsPersonReferral::getClientId))
    // .entrySet().stream().map(e -> normalizeSingle(e.getValue()))
    // .forEach(this::addToIndexQueue);

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
          // else {
          // LOGGER.trace("sensitive referral? ref id={}, client id={}", referralId, clientId);
          // }
        }

        final ReplicatedPersonReferrals repl = normalizeSingle(listReadyToNorm);
        if (repl != null) {
          ++cntr;
          repl.setClientId(clientId);
          addToIndexQueue(repl);
        }
        // else {
        // LOGGER.trace("null normalized? sensitive? client id={}", clientId);
        // }
      }
      // else {
      // LOGGER.trace("empty client? client id={}", clientId);
      // }
    }
    LOGGER.info("Normalize all: END");
    return cntr;
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
      enableParallelism(con);

      final DB2SystemMonitor monitor = monitorStart(con);
      final String schema = getDBSchemaName();

      try (
          final PreparedStatement stmtInsClient =
              con.prepareStatement(INSERT_CLIENT_FULL.replaceAll("#SCHEMA#", schema));
          final PreparedStatement stmtSelClient =
              con.prepareStatement(SELECT_CLIENT.replaceAll("#SCHEMA#", schema));
          final PreparedStatement stmtSelReferral =
              con.prepareStatement(getInitialLoadQuery(schema));
          final PreparedStatement stmtSelAllegation =
              con.prepareStatement(SELECT_ALLEGATION.replaceAll("#SCHEMA#", schema))) {
        readClients(stmtInsClient, stmtSelClient, listClientReferralKeys, p);
        readReferrals(stmtSelReferral, mapReferrals);
        readAllegations(stmtSelAllegation, listAllegations);
        monitorStopAndReport(monitor);
        con.commit();
      } finally {
        // The statements and result sets close automatically.
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    final Map<String, List<MinClientReferral>> mapReferralByClient = listClientReferralKeys.stream()
        .sorted((e1, e2) -> e1.getClientId().compareTo(e2.getClientId()))
        .collect(Collectors.groupingBy(MinClientReferral::getClientId));
    listClientReferralKeys.clear();

    final Map<String, List<EsPersonReferral>> mapAllegationByReferral = listAllegations.stream()
        .sorted((e1, e2) -> e1.getReferralId().compareTo(e2.getReferralId()))
        .collect(Collectors.groupingBy(EsPersonReferral::getReferralId));
    listAllegations.clear();

    // For each client:
    int cntr = normalizeQueryResults(mapReferrals, listReadyToNorm, mapReferralByClient,
        mapAllegationByReferral);

    // Release heap objects early and often.
    releaseLocalMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);

    // Good time to *request* garbage collection, not *demand* it. GC runs in another thread anyway.
    // SonarQube disagrees.
    // The catch: when many threads run, parallel GC may not get sufficient CPU cycles, until heap
    // memory is exhausted. Yes, this is a good place to drop a hint to GC that it *might* want to
    // clean up memory.
    System.gc(); // NOSONAR
    LOGGER.info("DONE");

    return cntr;
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("read_main");
    LOGGER.info("BEGIN: main read thread");
    EsPersonReferral.setOpts(getOpts());

    try {
      // This job normalizes **without** the transform thread.
      doneTransform = true;

      // Init the task list.
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.warn(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
      final List<ForkJoinTask<?>> tasks = new ArrayList<>(ranges.size());

      // Set thread pool size.
      final int cntReaderThreads =
          getOpts().getThreadCount() != 0L ? (int) getOpts().getThreadCount()
              : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
      LOGGER.warn(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", cntReaderThreads);
      ForkJoinPool forkJoinPool = new ForkJoinPool(cntReaderThreads);

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        tasks.add(forkJoinPool.submit(() -> pullRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.get();
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      LOGGER.info("read {} ES referral rows", this.rowsReadReferrals.get());
      doneExtract = true;
    }

    LOGGER.info("DONE: main read thread");
  }

  @Override
  protected void prepHibernatePull(Session session, Transaction txn, final Date lastRunTime)
      throws SQLException {
    final Work work = new Work() {
      @Override
      public void execute(Connection con) throws SQLException {
        con.setSchema(getDBSchemaName());
        con.setAutoCommit(false);
        enableParallelism(con);

        // The DB2 optimizer on z/OS treats timestamps in a JDBC prepared statements. Roll SQL.
        // To quote our beloved president, "Sad!" :-)
        final StringBuilder buf = new StringBuilder();
        buf.append("TIMESTAMP('")
            .append(new SimpleDateFormat(LEGACY_TIMESTAMP_FORMAT).format(lastRunTime)).append("')");

        final String sql = INSERT_CLIENT_LAST_CHG.replaceAll("#SCHEMA#", getDBSchemaName())
            .replaceAll("##TIMESTAMP##", buf.toString());
        LOGGER.info("Prep SQL: {}", sql);

        try (final Statement stmt =
            con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
          LOGGER.info("Find referrals new/changed since {}", lastRunTime);
          final int cntInsClientReferral = stmt.executeUpdate(sql);
          LOGGER.info("Total referrals new/changed: {}", cntInsClientReferral);
        } finally {
          // The statement closes automatically.
        }

      }
    };
    session.doWork(work);
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

  protected EsPersonReferral extractReferral(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    // IBM strongly recommends retrieving column results by position, not by column name.
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

  protected EsPersonReferral extractAllegation(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    // IBM strongly recommends retrieving column results by position, not by column name.
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
  public EsPersonReferral extract(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    ret.setReferralId(ifNull(rs.getString("REFERRAL_ID")));
    ret.setStartDate(rs.getDate("START_DATE"));
    ret.setEndDate(rs.getDate("END_DATE"));
    ret.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    ret.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    ret.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    ret.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));
    ret.setReferralLastUpdated(rs.getTimestamp("REFERRAL_LAST_UPDATED"));

    ret.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    ret.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    ret.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    ret.setAllegationLastUpdated(rs.getTimestamp("ALLEGATION_LAST_UPDATED"));

    ret.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    ret.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    ret.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    ret.setPerpetratorLastUpdated(rs.getTimestamp("PERPETRATOR_LAST_UPDATED"));
    ret.setPerpetratorSensitivityIndicator(rs.getString("PERPETRATOR_SENSITIVITY_IND"));

    ret.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    ret.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    ret.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    ret.setReporterLastUpdated(rs.getTimestamp("REPORTER_LAST_UPDATED"));

    ret.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    ret.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    ret.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    ret.setVictimLastUpdated(rs.getTimestamp("VICTIM_LAST_UPDATED"));
    ret.setVictimSensitivityIndicator(rs.getString("VICTIM_SENSITIVITY_IND"));

    ret.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    ret.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    ret.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    ret.setWorkerLastUpdated(rs.getTimestamp("WORKER_LAST_UPDATED"));

    ret.setCounty(rs.getInt("REFERRAL_COUNTY"));
    ret.setLastChange(rs.getDate("LAST_CHG"));

    return ret;
  }

  @Override
  protected boolean isRangeSelfManaging() {
    return true;
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
