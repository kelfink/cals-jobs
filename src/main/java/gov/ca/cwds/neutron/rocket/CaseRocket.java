package gov.ca.cwds.neutron.rocket;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.dao.cms.StaffPersonDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.data.persistence.cms.StaffPerson;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.ReferralHistoryIndexerJob;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronDB2Util;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.jobs.util.jdbc.NeutronThreadUtil;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.referral.MinClientReferral;
import gov.ca.cwds.neutron.util.jdbc.NeutronJdbcUtil;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Rocket to index person cases from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public abstract class CaseRocket extends InitialLoadJdbcRocket<ReplicatedPersonCases, EsPersonCase>
    implements NeutronRowMapper<EsPersonCase> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CaseRocket.class);

  //@formatter:off
  protected static final String INSERT_CLIENT_FULL =
      "INSERT INTO GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)"
      + "WITH step1 as (\n"
          + " SELECT DISTINCT CAS.FKCHLD_CLT AS CLIENT_ID, CAS.IDENTIFIER AS CASE_ID, CLC.SENSTV_IND\n"
          + " FROM CASE_T CAS\n"
          + " JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT\n"
          + " JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T\n"
          + " WHERE CAS.IBMSNAP_OPERATION IN ('I','U')\n"
          + " AND CLC.IBMSNAP_OPERATION IN ('I','U')\n"
          + " AND CLC.IDENTIFIER BETWEEN ? AND ?\n"
     + "), step2 as (\n"
          + " SELECT DISTINCT d.CLIENT_ID, d.CASE_ID, d.SENSTV_IND FROM step1 d\n"
          + " UNION\n"
          + " SELECT DISTINCT CLR.FKCLIENT_0 AS CLIENT_ID, d.CASE_ID, cli.SENSTV_IND\n"
          + " FROM step1 d\n"
          + " JOIN CLN_RELT CLR ON CLR.FKCLIENT_T = d.CLIENT_ID\n"
          + " JOIN CLIENT_T CLI ON CLI.IDENTIFIER = CLR.FKCLIENT_0\n"
          + " WHERE CLI.IBMSNAP_OPERATION IN ('I','U')\n"
          + " UNION\n"
          + " SELECT DISTINCT CLR.FKCLIENT_T AS CLIENT_ID, d.CASE_ID, cli.SENSTV_IND\n"
          + " FROM step1 d\n"
          + " JOIN CLN_RELT CLR ON CLR.FKCLIENT_0 = d.CLIENT_ID\n"
          + " JOIN CLIENT_T CLI ON CLI.IDENTIFIER = CLR.FKCLIENT_T\n"
          + " WHERE CLI.IBMSNAP_OPERATION IN ('I','U')\n"
     + ")\n"
     + "SELECT x.CLIENT_ID, x.CASE_ID, x.SENSTV_IND\n"
     + "FROM step2 x\n";
//@formatter:on

  /**
   * Filter <strong>deleted</strong> Clients and Cases.
   */
//@formatter:off
  protected static final String INSERT_CLIENT_LAST_CHG =  
      "INSERT INTO GT_ID (IDENTIFIER)"
      + "\nSELECT DISTINCT X.IDENTIFIER FROM ( "
          + "\nSELECT CAS1.IDENTIFIER"
           + "\n FROM CASE_T CAS1 "
           + "\nWHERE CAS1.IBMSNAP_LOGMARKER > ? "
       + "\nUNION\n"
           + "SELECT CAS2.IDENTIFIER "
           + "\nFROM CASE_T CAS2"
           + "\nLEFT JOIN CHLD_CLT CCL1 ON CCL1.FKCLIENT_T = CAS2.FKCHLD_CLT  "
           + "\nLEFT JOIN CLIENT_T CLC1 ON CLC1.IDENTIFIER = CCL1.FKCLIENT_T "
           + "\nWHERE CCL1.IBMSNAP_LOGMARKER > ? "
       + "\nUNION"
           + "\n SELECT CAS3.IDENTIFIER "
           + "\n FROM CASE_T CAS3 "
           + "\nLEFT JOIN CHLD_CLT CCL2 ON CCL2.FKCLIENT_T = CAS3.FKCHLD_CLT  "
           + "\nLEFT JOIN CLIENT_T CLC2 ON CLC2.IDENTIFIER = CCL2.FKCLIENT_T "
           + "\nWHERE CLC2.IBMSNAP_LOGMARKER > ? "
       + "\nUNION "
           + "\nSELECT CAS3.IDENTIFIER "
           + "\nFROM CASE_T CAS3 "
           + "\nLEFT JOIN CHLD_CLT CCL3 ON CCL3.FKCLIENT_T = CAS3.FKCHLD_CLT  "
           + "\nLEFT JOIN CLIENT_T CLC3 ON CLC3.IDENTIFIER = CCL3.FKCLIENT_T "
           + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL3.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
           + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
           + "\nWHERE CLR.IBMSNAP_LOGMARKER > ? "
       + "\nUNION "
           + "\nSELECT CAS.IDENTIFIER "
           + "\nFROM CASE_T CAS "
           + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
           + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
           + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
           + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
           + "\nJOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0 "
           + "\nWHERE CLP.IBMSNAP_LOGMARKER > ? "
      + "\n) x";
//@formatter:on

//@formatter:off
  protected static final String SELECT_CLIENT =
        "SELECT rc.FKCLIENT_T, rc.FKREFERL_T, rc.SENSTV_IND, c.IBMSNAP_OPERATION AS CLT_IBMSNAP_OPERATION \n" 
      + "FROM GT_REFR_CLT RC \n"
      + "JOIN CLIENT_T C ON C.IDENTIFIER = RC.FKCLIENT_T";
//@formatter:on

//@formatter:off
  protected static final String SELECT_CASE = "SELECT "
      + " RFL.IDENTIFIER        AS REFERRAL_ID,\n"
      + " RFL.REF_RCV_DT        AS START_DATE,\n" 
      + " RFL.REFCLSR_DT        AS END_DATE,\n"
      + " RFL.RFR_RSPC          AS REFERRAL_RESPONSE_TYPE,\n"
      + " RFL.LMT_ACSSCD        AS LIMITED_ACCESS_CODE,\n"
      + " RFL.LMT_ACS_DT        AS LIMITED_ACCESS_DATE,\n"
      + " TRIM(RFL.LMT_ACSDSC)  AS LIMITED_ACCESS_DESCRIPTION,\n"
      + " RFL.L_GVR_ENTC        AS LIMITED_ACCESS_GOVERNMENT_ENT,\n"
      + " RFL.LST_UPD_TS        AS REFERRAL_LAST_UPDATED,\n"
      + " TRIM(RPT.FKCASE_T)  AS REPORTER_ID,\n"
      + " TRIM(RPT.RPTR_FSTNM)  AS REPORTER_FIRST_NM,\n"
      + " TRIM(RPT.RPTR_LSTNM)  AS REPORTER_LAST_NM,\n"
      + " RPT.LST_UPD_TS        AS REPORTER_LAST_UPDATED,\n"
      + " STP.IDENTIFIER        AS WORKER_ID,\n" 
      + " TRIM(STP.FIRST_NM)    AS WORKER_FIRST_NM,\n"
      + " TRIM(STP.LAST_NM)     AS WORKER_LAST_NM,\n"
      + " STP.LST_UPD_TS        AS WORKER_LAST_UPDATED,\n"
      + " RFL.GVR_ENTC          AS REFERRAL_COUNTY,\n" 
      + " CURRENT TIMESTAMP     AS LAST_CHG, \n"
      + " RFL.IBMSNAP_OPERATION AS RFL_IBMSNAP_OPERATION, \n"
      + " RPT.IBMSNAP_OPERATION AS RPT_IBMSNAP_OPERATION \n"
      + "FROM (SELECT DISTINCT rc1.FKCASE_T FROM GT_REFR_CLT rc1) RC \n"
      + "JOIN REFERL_T          RFL  ON RFL.IDENTIFIER = RC.FKCASE_T \n"
      + "LEFT JOIN REPTR_T      RPT  ON RPT.FKCASE_T = RFL.IDENTIFIER \n"
      + "LEFT JOIN STFPERST     STP  ON RFL.FKSTFPERST = STP.IDENTIFIER ";
//@formatter:on

  private List<StaffPerson> caseWorkers = new ArrayList<>(88000);

  /**
   * Allocate memory once for each thread and reuse per key range.
   * 
   * <p>
   * Use thread local variables <strong>sparingly</strong> because they stick to the thread. This
   * Neutron rocket reuses threads for performance, since thread creation is expensive.
   * </p>
   */
  protected transient ThreadLocal<List<EsPersonCase>> allocCases = new ThreadLocal<>();

  protected transient ThreadLocal<Map<String, EsPersonCase>> allocMapCases = new ThreadLocal<>();

  protected transient ThreadLocal<List<MinClientReferral>> allocClientCaseKeys =
      new ThreadLocal<>();

  protected transient ThreadLocal<List<EsPersonCase>> allocReadyToNorm = new ThreadLocal<>();

  protected final AtomicInteger rowsReadCases = new AtomicInteger(0);

  protected final AtomicInteger nextThreadNum = new AtomicInteger(0);

  private StaffPersonDao staffPersonDao;

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao DAO for {@link ReplicatedPersonCases}
   * @param esDao ElasticSearch DAO
   * @param staffPersonDao staff worker DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public CaseRocket(ReplicatedPersonCasesDao dao, ElasticsearchDao esDao,
      StaffPersonDao staffPersonDao, @LastRunFile String lastRunFile, ObjectMapper mapper,
      FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
  }

  // =====================
  // FIXED SPECS:
  // =====================

  /**
   * This rocket normalizes <strong>without</strong> the transform thread.
   */
  @Override
  public boolean useTransformThread() {
    return false;
  }

  protected String getClientSeedQuery() {
    return INSERT_CLIENT_FULL;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_REFRL_ONLY";
  }

  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  public List<Pair<String, String>> getPartitionRanges() throws NeutronException {
    return NeutronJdbcUtil.getCommonPartitionRanges64(this);
  }

  @Override
  public String getOptionalElementName() {
    return "cases";
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
  public String getJdbcOrderBy() {
    return ""; // sort manually since DB2 might not optimize the sort.
  }

  /**
   * Roll your own SQL.
   * <p>
   * This approach requires sorted results. Either sort on the database side or here in the
   * application.
   */
  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append(SELECT_CASE);

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
      buf.append(" WHERE RFL.LMT_ACSSCD = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    final String ret = buf.toString().replaceAll("\\s+", " ").trim();
    LOGGER.info("CASE SQL: {}", ret);
    return ret;
  }

  // =====================
  // NORMALIZATION:
  // =====================

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonCases p)
      throws NeutronException {
    return prepareUpdateRequest(esp, p, p.getCases(), true);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsPersonCase.class;
  }

  protected void readClients(final PreparedStatement stmtInsClient,
      final PreparedStatement stmtSelClient, final List<MinClientReferral> listClientReferralKeys,
      final Pair<String, String> p) throws SQLException {
    // Prepare client list.
    stmtInsClient.setMaxRows(0);
    stmtInsClient.setQueryTimeout(0);
    stmtInsClient.setString(1, p.getLeft());
    stmtInsClient.setString(2, p.getRight());

    final int cntInsClientCases = stmtInsClient.executeUpdate();
    LOGGER.info("bundle client/cases: {}", cntInsClientCases);

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

  protected abstract EsPersonCase mapRows(ResultSet rs) throws SQLException;

  protected void readReferrals(final PreparedStatement stmtSelCase,
      final Map<String, EsPersonCase> mapReferrals) throws SQLException {
    stmtSelCase.setMaxRows(0);
    stmtSelCase.setQueryTimeout(0);
    stmtSelCase.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

    int cntr = 0;
    EsPersonCase m;
    LOGGER.info("pull cases");
    final ResultSet rs = stmtSelCase.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = mapRows(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "bundle referral");
      JobLogs.logEvery(LOGGER, 10000, rowsReadCases.incrementAndGet(), "Total read", "cases");
      // if (m.getReferralClientReplicationOperation() != CmsReplicationOperation.D) {
      // mapReferrals.put(m.getReferralId(), m);
      // }
    }
  }

  @Override
  public List<ReplicatedPersonCases> normalize(List<EsPersonCase> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsPersonCase>normalizeList(recs);
  }

  protected int normalizeClientReferrals(int cntr, MinClientReferral rc1, final String clientId,
      final Map<String, EsPersonCase> mapReferrals, final List<EsPersonCase> listReadyToNorm,
      final Map<String, List<EsPersonCase>> mapAllegationByReferral) {
    int ret = cntr;
    final String referralId = rc1.getReferralId();
    final EsPersonCase denormReferral = mapReferrals.get(referralId);
    final boolean goodToGo = denormReferral != null
    // && denormReferral.getReferralReplicationOperation() != CmsReplicationOperation.D
    ;

    // Sealed and sensitive may be excluded.
    if (goodToGo) {
      // Loop allegations for this referral:
      if (mapAllegationByReferral.containsKey(referralId)) {
        for (EsPersonCase alg : mapAllegationByReferral.get(referralId)) {
          // alg.mergeClientReferralInfo(clientId, denormReferral);
          listReadyToNorm.add(alg);
        }
      } else {
        listReadyToNorm.add(denormReferral);
      }
    }

    // #152932457: Overwrite deleted cases.
    final ReplicatedPersonCases repl =
        goodToGo ? normalizeSingle(listReadyToNorm) : new ReplicatedPersonCases(clientId);
    ++ret;
    // repl.setClientId(clientId);
    addToIndexQueue(repl);

    return ret;
  }

  protected int normalizeQueryResults(final Map<String, EsPersonCase> mapReferrals,
      final List<EsPersonCase> listReadyToNorm,
      final Map<String, List<MinClientReferral>> mapCaseByClient,
      final Map<String, List<EsPersonCase>> mapAllegationByReferral) {
    LOGGER.debug("Normalize all: START");
    int countNormalized = 0;

    for (Map.Entry<String, List<MinClientReferral>> rc : mapCaseByClient.entrySet()) {
      final String clientId = rc.getKey();
      // Loop cases for this client only.
      if (StringUtils.isNotBlank(clientId)) {
        listReadyToNorm.clear(); // next client id
        for (MinClientReferral rc1 : rc.getValue()) {
          countNormalized = normalizeClientReferrals(countNormalized, rc1, clientId, mapReferrals,
              listReadyToNorm, mapAllegationByReferral);
        }
      }
    }

    LOGGER.debug("Normalize all: END");
    return countNormalized;
  }

  /**
   * Justification: See method cleanUpMemory in rocket {@link ReferralHistoryIndexerJob}.
   * 
   * @param listAllegations EsPersonCase
   * @param mapReferrals k=id, v=EsPersonCase
   * @param listClientReferralKeys client/referral id pairs
   * @param listReadyToNorm EsPersonCase
   */
  protected void cleanUpMemory(final List<EsPersonCase> listAllegations,
      Map<String, EsPersonCase> mapReferrals, List<MinClientReferral> listClientReferralKeys,
      List<EsPersonCase> listReadyToNorm) {
    releaseLocalMemory(mapReferrals, listClientReferralKeys, listReadyToNorm);
    System.gc(); // NOSONAR
  }

  /**
   * Pour cases, and client/case keys into the caldron and brew into a cases array element per
   * client.
   * 
   * @param listAllegations bundle allegations
   * @param mapReferrals k=referral id, v=EsPersonCase
   * @param listClientReferralKeys client/referral key pairs
   * @param listReadyToNorm denormalized records
   * @return normalized record count
   */
  protected int mapReduce(final List<EsPersonCase> listAllegations,
      final Map<String, EsPersonCase> mapReferrals,
      final List<MinClientReferral> listClientReferralKeys,
      final List<EsPersonCase> listReadyToNorm) {
    int countNormalized = 0;
    try {
      final Map<String, List<MinClientReferral>> mapReferralByClient = listClientReferralKeys
          .stream().sorted((e1, e2) -> e1.getClientId().compareTo(e2.getClientId()))
          .collect(Collectors.groupingBy(MinClientReferral::getClientId));
      listClientReferralKeys.clear(); // release objects for gc

      final Map<String, List<EsPersonCase>> mapAllegationByReferral =
          listAllegations.stream().sorted((e1, e2) -> e1.getCaseId().compareTo(e2.getCaseId()))
              .collect(Collectors.groupingBy(EsPersonCase::getCaseId));
      listAllegations.clear(); // release objects for gc

      // For each client group:
      countNormalized = normalizeQueryResults(mapReferrals, listReadyToNorm, mapReferralByClient,
          mapAllegationByReferral);
    } finally {
      cleanUpMemory(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);
    }
    return countNormalized;
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
    getFlightLog().markRangeStart(p);

    allocateThreadMemory(); // allocate thread local memory, if not done prior.
    final List<EsPersonCase> listAllegations = allocCases.get();
    final Map<String, EsPersonCase> mapReferrals = allocMapCases.get();
    final List<MinClientReferral> listClientReferralKeys = allocClientCaseKeys.get();
    final List<EsPersonCase> listReadyToNorm = allocReadyToNorm.get();

    // Clear collections, free memory before starting.
    releaseLocalMemory(mapReferrals, listClientReferralKeys, listReadyToNorm);

    try (final Connection con = getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      NeutronDB2Util.enableParallelism(con);

      final String schema = getDBSchemaName();

      try (final PreparedStatement stmtInsClient = con.prepareStatement(getClientSeedQuery());
          final PreparedStatement stmtSelClient = con.prepareStatement(SELECT_CLIENT);
          final PreparedStatement stmtSelCase = con.prepareStatement(getInitialLoadQuery(schema))) {
        // Read separate components for this key bundle.
        readClients(stmtInsClient, stmtSelClient, listClientReferralKeys, p);
        readReferrals(stmtSelCase, mapReferrals);

        // All data retrieved.
        con.commit();
      }
    } catch (Exception e) {
      fail();
      throw JobLogs.runtime(LOGGER, e, "ERROR HANDING RANGE {} - {}: {}", p.getLeft(), p.getRight(),
          e.getMessage());
    }

    int cntr = mapReduce(listAllegations, mapReferrals, listClientReferralKeys, listReadyToNorm);
    getFlightLog().markRangeComplete(p);
    LOGGER.info("DONE");
    return cntr;
  }

  /**
   * Initial load only. The "extract" part of ETL. Processes key ranges in separate threads.
   * 
   * <p>
   * Note that this rocket normalizes <strong>without</strong> the transform thread.
   * </p>
   */
  @Override
  protected void threadRetrieveByJdbc() {
    nameThread("case_main");
    LOGGER.info("BEGIN: main read thread");
    doneTransform(); // normalize in place **WITHOUT** the transform thread

    try {
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.info(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
      final List<ForkJoinTask<?>> tasks = new ArrayList<>(ranges.size());
      final ForkJoinPool threadPool =
          new ForkJoinPool(NeutronThreadUtil.calcReaderThreads(getFlightPlan()));

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        // Pull each range independently on the next available thread.
        tasks.add(threadPool.submit(() -> pullNextRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.get();
      }
    } catch (Exception e) {
      fail();
      throw JobLogs.runtime(LOGGER, e, "ERROR IN THREADED RETRIEVAL! {}", e.getMessage());
    } finally {
      doneRetrieve();
    }

    LOGGER.info("DONE: read {} ES case rows", this.rowsReadCases.get());
  }

//@formatter:off
  @Override
  public String getPrepLastChangeSQL() {
    return 
     "INSERT INTO GT_ID (IDENTIFIER)"
     + "\nSELECT DISTINCT X.IDENTIFIER FROM ( "
         + "\nSELECT CAS.IDENTIFIER"
          + "\n FROM CASE_T CAS "
          + "\nWHERE CAS.IBMSNAP_LOGMARKER > ? "
      + "\nUNION\n"
          + "SELECT CAS.IDENTIFIER "
          + "\nFROM CASE_T CAS"
          + "\nLEFT JOIN CHLD_CLT CCL1 ON CCL1.FKCLIENT_T = CAS.FKCHLD_CLT  "
          + "\nLEFT JOIN CLIENT_T CLC1 ON CLC1.IDENTIFIER = CCL1.FKCLIENT_T "
          + "\nWHERE CCL1.IBMSNAP_LOGMARKER > ? "
      + "\nUNION"
          + "\n SELECT CAS.IDENTIFIER "
          + "\n FROM CASE_T CAS "
          + "\nLEFT JOIN CHLD_CLT CCL2 ON CCL2.FKCLIENT_T = CAS.FKCHLD_CLT  "
          + "\nLEFT JOIN CLIENT_T CLC2 ON CLC2.IDENTIFIER = CCL2.FKCLIENT_T "
          + "\nWHERE CLC2.IBMSNAP_LOGMARKER > ? "
      + "\nUNION "
          + "\nSELECT CAS.IDENTIFIER "
          + "\nFROM CASE_T CAS "
          + "\nLEFT JOIN CHLD_CLT CCL3 ON CCL3.FKCLIENT_T = CAS.FKCHLD_CLT  "
          + "\nLEFT JOIN CLIENT_T CLC3 ON CLC3.IDENTIFIER = CCL3.FKCLIENT_T "
          + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL3.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
          + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
          + "\nWHERE CLR.IBMSNAP_LOGMARKER > ? "
      + "\nUNION "
          + "\nSELECT CAS.IDENTIFIER "
          + "\nFROM CASE_T CAS "
          + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
          + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
          + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
          + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
          + "\nJOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0 "
          + "\nWHERE CLP.IBMSNAP_LOGMARKER > ? "
     + "\n) x";
  }
//@formatter:on

  /**
   * @return complete list of potential case workers
   * @throws NeutronException on database error
   */
  protected List<StaffPerson> readStaffWorkers() throws NeutronException {
    try {
      return staffPersonDao.findAll();
    } catch (Exception e) {
      fail();
      throw new NeutronException("ERROR READING CASE WORKERS", e);
    }
  }

  @Override
  public abstract EsPersonCase extract(final ResultSet rs) throws SQLException;

  protected void releaseLocalMemory(final Map<String, EsPersonCase> mapCases,
      final List<MinClientReferral> listClientCaseKeys, final List<EsPersonCase> listReadyToNorm) {
    listClientCaseKeys.clear();
    listReadyToNorm.clear();
    mapCases.clear();
  }

  /**
   * Initial mode only. Allocate memory once per thread and reuse it.
   * 
   * <p>
   * NEXT: calculate container sizes by bundle size.
   * </p>
   */
  protected void allocateThreadMemory() {
    if (allocCases.get() == null) {
      allocCases.set(new ArrayList<>(150000));
      allocReadyToNorm.set(new ArrayList<>(150000));
      allocMapCases.set(new HashMap<>(99881)); // Prime
      allocClientCaseKeys.set(new ArrayList<>(12000));
    }
  }

  /**
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(CaseRocket.class, args);
  }

  public List<StaffPerson> getCaseWorkers() {
    return caseWorkers;
  }

}
