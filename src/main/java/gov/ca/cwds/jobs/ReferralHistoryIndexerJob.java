package gov.ca.cwds.jobs;

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
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.ibm.db2.jcc.DB2SystemMonitor;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronDB2Util;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.jobs.util.jdbc.NeutronThreadUtil;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.rocket.referral.MinClientReferral;
import gov.ca.cwds.neutron.rocket.referral.ReferralJobRanges;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Rocket to load person referrals from CMS into ElasticSearch.
 * 
 * <p>
 * Turn-around time for database objects is too long. Embed SQL in Java instead.
 * </p>
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob
    extends BasePersonRocket<ReplicatedPersonReferrals, EsPersonReferral>
    implements NeutronRowMapper<EsPersonReferral> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralHistoryIndexerJob.class);

  protected static final String INSERT_CLIENT_FULL =
      "INSERT INTO GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "\nSELECT rc.FKREFERL_T, rc.FKCLIENT_T, c.SENSTV_IND\nFROM REFR_CLT rc\n"
          + "\nJOIN CLIENT_T c on c.IDENTIFIER = rc.FKCLIENT_T\n"
          + "\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  protected static final String INSERT_CLIENT_LAST_CHG = "INSERT INTO GT_ID (IDENTIFIER)\n"
      + "WITH step1 AS (\nSELECT ALG.FKREFERL_T AS REFERRAL_ID\n"
      + " FROM ALLGTN_T ALG  WHERE ALG.IBMSNAP_LOGMARKER > ?), step2 AS (\n"
      + " SELECT ALG.FKREFERL_T AS REFERRAL_ID FROM CLIENT_T C \n"
      + " JOIN ALLGTN_T ALG ON (C.IDENTIFIER = ALG.FKCLIENT_0 OR C.IDENTIFIER = ALG.FKCLIENT_T)\n"
      + " WHERE C.IBMSNAP_LOGMARKER > ?), step3 AS (\n"
      + " SELECT RCT.FKREFERL_T AS REFERRAL_ID FROM REFR_CLT RCT \n"
      + " WHERE RCT.IBMSNAP_LOGMARKER > ?), step4 AS (\n"
      + " SELECT RFL.IDENTIFIER AS REFERRAL_ID FROM REFERL_T RFL \n"
      + " WHERE RFL.IBMSNAP_LOGMARKER > ?), step5 AS (\n"
      + " SELECT RPT.FKREFERL_T AS REFERRAL_ID FROM REPTR_T RPT \n"
      + " WHERE RPT.IBMSNAP_LOGMARKER > ?), hoard AS (\n"
      + " SELECT s1.REFERRAL_ID FROM STEP1 s1 UNION ALL\n"
      + " SELECT s2.REFERRAL_ID FROM STEP2 s2 UNION ALL\n"
      + " SELECT s3.REFERRAL_ID FROM STEP3 s3 UNION ALL\n"
      + " SELECT s4.REFERRAL_ID FROM STEP4 s4 UNION ALL\n"
      + " SELECT s5.REFERRAL_ID FROM STEP5 s5 )\n" + "SELECT DISTINCT g.REFERRAL_ID from hoard g ";

  protected static final String SELECT_CLIENT =
      "SELECT FKCLIENT_T, FKREFERL_T, SENSTV_IND FROM GT_REFR_CLT RC";

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
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM GT_REFR_CLT rc1) RC \n"
      + "JOIN ALLGTN_T       ALG  ON ALG.FKREFERL_T = RC.FKREFERL_T \n"
      + "JOIN CLIENT_T       CLV  ON CLV.IDENTIFIER = ALG.FKCLIENT_T \n"
      + "LEFT JOIN CLIENT_T  CLP  ON CLP.IDENTIFIER = ALG.FKCLIENT_0 \n"
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
      + "FROM (SELECT DISTINCT rc1.FKREFERL_T FROM GT_REFR_CLT rc1) RC \n"
      + "JOIN REFERL_T          RFL  ON RFL.IDENTIFIER = RC.FKREFERL_T \n"
      + "LEFT JOIN REPTR_T      RPT  ON RPT.FKREFERL_T = RFL.IDENTIFIER \n"
      + "LEFT JOIN STFPERST     STP  ON RFL.FKSTFPERST = STP.IDENTIFIER ";

  /**
   * Allocate memory once for each thread and reuse per key range.
   * 
   * <p>
   * Note: <strong>use thread local variables sparingly</strong> because they stick to the thread.
   * This Neutron job reuses threads for performance, since thread creation is expensive.
   * </p>
   */
  private transient ThreadLocal<List<EsPersonReferral>> allocAllegations = new ThreadLocal<>();

  private transient ThreadLocal<Map<String, EsPersonReferral>> allocReferrals = new ThreadLocal<>();

  private transient ThreadLocal<List<MinClientReferral>> allocClientReferralKeys =
      new ThreadLocal<>();

  private transient ThreadLocal<List<EsPersonReferral>> allocReadyToNorm = new ThreadLocal<>();

  private final AtomicInteger rowsReadReferrals = new AtomicInteger(0);

  private final AtomicInteger rowsReadAllegations = new AtomicInteger(0);

  private final AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public ReferralHistoryIndexerJob(ReplicatedPersonReferralsDao dao, ElasticsearchDao esDao,
      @LastRunFile String lastRunFile, ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    EsPersonReferral.setOpts(getFlightPlan()); // WARNING: change for continuous mode
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
   * Roll your own SQL. Turn-around on DB2 objects from other teams takes too long.
   */
  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append(SELECT_REFERRAL);

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
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
   */
  protected synchronized Connection getConnection() throws SQLException {
    return jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection();
  }

  /**
   * Initial mode only. Allocate memory once per thread and reuse it.
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
    stmtSelClient.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

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
    stmtSelReferral.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull referrals");
    final ResultSet rs = stmtSelReferral.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = EsPersonReferral.extractReferral(rs)) != null) {
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
    stmtSelAllegation.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

    int cntr = 0;
    EsPersonReferral m;
    LOGGER.info("pull allegations");
    final ResultSet rs = stmtSelAllegation.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = EsPersonReferral.extractAllegation(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "bundle allegation");
      JobLogs.logEvery(LOGGER, 15000, rowsReadAllegations.incrementAndGet(), "Total read",
          "allegations");
      listAllegations.add(m);
    }
  }

  protected int normalizeClientReferrals(int cntr, MinClientReferral rc1, final String clientId,
      final Map<String, EsPersonReferral> mapReferrals,
      final List<EsPersonReferral> listReadyToNorm,
      final Map<String, List<EsPersonReferral>> mapAllegationByReferral) {
    int ret = cntr;
    final String referralId = rc1.getReferralId();
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

    final ReplicatedPersonReferrals repl = normalizeSingle(listReadyToNorm);
    if (repl != null) {
      ++ret;
      repl.setClientId(clientId);
      addToIndexQueue(repl);
    }

    return ret;
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
          cntr = normalizeClientReferrals(cntr, rc1, clientId, mapReferrals, listReadyToNorm,
              mapAllegationByReferral);
        }
      }
    }

    LOGGER.info("Normalize all: END");
    return cntr;
  }

  protected void cleanUpMemory(final List<EsPersonReferral> listAllegations,
      Map<String, EsPersonReferral> mapReferrals, List<MinClientReferral> listClientReferralKeys,
      List<EsPersonReferral> listReadyToNorm) {
    // Release heap objects early and often.
    releaseLocalMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);

    // Good time to *request* garbage collection, not *demand* it. GC runs in another thread.
    // SonarQube disagrees.
    // The catch: when many threads run, parallel GC may not get sufficient CPU cycles, until heap
    // memory is exhausted. Yes, this is a good place to drop a hint to GC that it *might* want to
    // clean up memory.
    System.gc(); // NOSONAR
  }

  protected int mapReduce(final List<EsPersonReferral> listAllegations,
      final Map<String, EsPersonReferral> mapReferrals,
      final List<MinClientReferral> listClientReferralKeys,
      final List<EsPersonReferral> listReadyToNorm) {
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
      cleanUpMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);
    }
    return cntr;
  }

  /**
   * Read all records from a single partition (key range) in buckets. Then sort results and
   * normalize.
   * 
   * <p>
   * Each call to this method should run in its own thread.
   * </p>
   * 
   * @param p partition (key) range to read
   * @return number of client documents affected
   */
  protected int pullNextRange(final Pair<String, String> p) {
    final String threadName =
        "extract_" + nextThreadNum.incrementAndGet() + "_" + p.getLeft() + "_" + p.getRight();
    nameThread(threadName);
    LOGGER.info("BEGIN");
    getFlightLog().trackRangeStart(p);

    allocateThreadMemory(); // allocate thread local memory, if not done prior.
    final List<EsPersonReferral> listAllegations = allocAllegations.get();
    final Map<String, EsPersonReferral> mapReferrals = allocReferrals.get();
    final List<MinClientReferral> listClientReferralKeys = allocClientReferralKeys.get();
    final List<EsPersonReferral> listReadyToNorm = allocReadyToNorm.get();

    // Clear collections, free memory before starting.
    releaseLocalMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);

    try (final Connection con = getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      NeutronDB2Util.enableParallelism(con);

      final DB2SystemMonitor monitor = NeutronDB2Util.monitorStart(con);
      final String schema = getDBSchemaName();

      try (
          final PreparedStatement stmtInsClient =
              con.prepareStatement(INSERT_CLIENT_FULL.replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelClient =
              con.prepareStatement(SELECT_CLIENT.replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelReferral =
              con.prepareStatement(getInitialLoadQuery(schema).replaceAll("\\s+", " ").trim());
          final PreparedStatement stmtSelAllegation =
              con.prepareStatement(SELECT_ALLEGATION.replaceAll("\\s+", " ").trim())) {

        // Read separate components for this key bundle.
        readClients(stmtInsClient, stmtSelClient, listClientReferralKeys, p);
        readReferrals(stmtSelReferral, mapReferrals);
        readAllegations(stmtSelAllegation, listAllegations);

        // Retrieved all data.
        NeutronDB2Util.monitorStopAndReport(monitor);
        con.commit();
      }
    } catch (Exception e) {
      fail();
      throw JobLogs.runtime(LOGGER, e, "ERROR HANDING RANGE {} - {}: {}", p.getLeft(), p.getRight(),
          e.getMessage());
    }

    int cntr = mapReduce(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);
    getFlightLog().trackRangeComplete(p);
    LOGGER.info("DONE");
    return cntr;
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   * 
   * <p>
   * Note that this job normalizes <strong>without</strong> the transform thread.
   * </p>
   */
  @Override
  protected void threadRetrieveByJdbc() {
    nameThread("read_main");
    LOGGER.info("BEGIN: main read thread");

    // WARNING: static setter is OK in standalone job but NOT permitted in continuous mode.
    EsPersonReferral.setOpts(getFlightPlan());
    doneTransform(); // normalize in place **without** the transform thread

    try {
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.info(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
      final List<ForkJoinTask<?>> tasks = new ArrayList<>(ranges.size());
      final ForkJoinPool threadPool =
          new ForkJoinPool(NeutronThreadUtil.calcReaderThreads(getFlightPlan()));

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        tasks.add(threadPool.submit(() -> pullNextRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.get();
      }

    } catch (Exception e) {
      fail();
      throw JobLogs.runtime(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneRetrieve();
    }

    LOGGER.info("DONE: read {} ES referral rows", this.rowsReadReferrals.get());
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
  public boolean isInitialLoadJdbc() {
    return true;
  }

  /**
   * Referrals is an <strong>enormous</strong> task and fetches partition ranges from a file instead
   * of bloating a Java file.
   * 
   * @see ReferralJobRanges
   */
  @Override
  public List<Pair<String, String>> getPartitionRanges() throws NeutronException {
    return new ReferralJobRanges().getPartitionRanges(this);
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getFlightPlan().isLoadSealedAndSensitive();
  }

  @Override
  public List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  public String getOptionalElementName() {
    return "referrals";
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonReferrals p)
      throws NeutronException {
    return prepareUpdateRequest(esp, p, p.getReferrals(), true);
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
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(ReferralHistoryIndexerJob.class, args);
  }

}
