package gov.ca.cwds.neutron.rocket;

import static gov.ca.cwds.neutron.util.transform.JobTransformUtils.ifNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.dao.cms.StaffPersonDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.CaseSQLResource;
import gov.ca.cwds.data.persistence.cms.EsCaseRelatedPerson;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.data.persistence.cms.StaffPerson;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableStaffWorker;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
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
import gov.ca.cwds.neutron.rocket.cases.CaseClientRelative;
import gov.ca.cwds.neutron.util.jdbc.NeutronJdbcUtil;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Rocket to index person cases from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class CaseRocket extends InitialLoadJdbcRocket<ReplicatedPersonCases, EsCaseRelatedPerson>
    implements NeutronRowMapper<EsCaseRelatedPerson> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CaseRocket.class);

  /**
   * Allocate memory once for each thread and reuse per key range.
   * 
   * <p>
   * Use thread local variables <strong>sparingly</strong> because they stick to the thread. This
   * Neutron rocket reuses threads for performance, since thread creation is expensive.
   * </p>
   */
  protected transient ThreadLocal<List<EsCaseRelatedPerson>> allocCases = new ThreadLocal<>();

  protected transient ThreadLocal<List<CaseClientRelative>> allocCaseClientRelative =
      new ThreadLocal<>();

  /**
   * k=case id, v=case
   */
  protected transient ThreadLocal<Map<String, EsCaseRelatedPerson>> allocMapCases =
      new ThreadLocal<>();

  /**
   * k=client id, v=cases
   */
  protected transient ThreadLocal<Map<String, Set<String>>> allocMapClientCases =
      new ThreadLocal<>();

  /**
   * k=client id, v=client
   */
  protected transient ThreadLocal<Map<String, ReplicatedClient>> allocMapClients =
      new ThreadLocal<>();

  private final AtomicInteger rowsReadCases = new AtomicInteger(0);

  private final AtomicInteger nextThreadNum = new AtomicInteger(0);

  private transient StaffPersonDao staffPersonDao;

  private transient ReplicatedClientDao clientDao;

  private Map<String, StaffPerson> staffWorkers = new HashMap<>();

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao DAO for {@link ReplicatedPersonCases}
   * @param esDao ElasticSearch DAO
   * @param clientDao client DAO
   * @param staffPersonDao staff worker DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public CaseRocket(ReplicatedPersonCasesDao dao, ElasticsearchDao esDao,
      ReplicatedClientDao clientDao, StaffPersonDao staffPersonDao, @LastRunFile String lastRunFile,
      ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);

    this.clientDao = clientDao;
    this.staffPersonDao = staffPersonDao;
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

  @Override
  public String getPrepLastChangeSQL() {
    return CaseSQLResource.INSERT_CLIENT_LAST_CHG;
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
    buf.append(CaseSQLResource.SELECT_CASES_FULL_EVERYTHING);

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
      buf.append(" WHERE CAS.LMT_ACSSCD = 'N' ");
    }

    final String ret = buf.toString().trim();
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

  @Override
  public List<ReplicatedPersonCases> normalize(List<EsCaseRelatedPerson> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsCaseRelatedPerson>normalizeList(recs);
  }

  // =====================
  // TRANSLATE SYSCODES:
  // =====================

  protected void translateParentRelationships(final EsCaseRelatedPerson ret, Short code1,
      Short code2) {
    translateParentalRelationship(ret, code1);
    translateParentalRelationship(ret, code2);
  }

  protected boolean isParentalRelation(short code) {
    return (code >= 187 && code <= 214) || (code >= 245 && code <= 254)
        || (code >= 282 && code <= 294) || code == 272 || code == 273 || code == 5620
        || code == 6360 || code == 6361;
  }

  protected void translateParentalRelationship(final EsCaseRelatedPerson ret, Short codeId) {
    if (ret.getParentRelationship() != null && codeId != null
        && isParentalRelation(codeId.shortValue())) {
      final SystemCode systemCode = SystemCodeCache.global().getSystemCode(codeId.shortValue());
      if (systemCode != null) {
        ret.setParentRelationship(systemCode.getSystemId().intValue());
      }
    }
  }

  // =====================
  // JDBC:
  // =====================

  protected void prepClientBundle(final PreparedStatement stmtInsClient,
      final Pair<String, String> p) throws SQLException {
    stmtInsClient.setMaxRows(0);
    stmtInsClient.setQueryTimeout(0);
    stmtInsClient.setString(1, p.getLeft());
    stmtInsClient.setString(2, p.getRight());

    final int countInsClientCases = stmtInsClient.executeUpdate();
    LOGGER.info("bundle client/cases: {}", countInsClientCases);
  }

  protected void readClientCaseRelationship(final PreparedStatement stmtSelClientCaseRelation,
      final List<CaseClientRelative> listCaseClientRelation) throws SQLException {
    stmtSelClientCaseRelation.setMaxRows(0);
    stmtSelClientCaseRelation.setQueryTimeout(0);
    stmtSelClientCaseRelation.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

    int cntr = 0;
    CaseClientRelative m;
    LOGGER.info("pull cases");
    final ResultSet rs = stmtSelClientCaseRelation.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = CaseClientRelative.extract(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "case bundle");
      JobLogs.logEvery(LOGGER, 10000, rowsReadCases.incrementAndGet(), "Total read",
          "case/client/rel");
      listCaseClientRelation.add(m);
    }
  }

  protected void readCases(final PreparedStatement stmtSelCase,
      final Map<String, EsCaseRelatedPerson> mapCases) throws SQLException {
    stmtSelCase.setMaxRows(0);
    stmtSelCase.setQueryTimeout(0);
    stmtSelCase.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

    int cntr = 0;
    EsCaseRelatedPerson m;
    LOGGER.info("pull cases");
    final ResultSet rs = stmtSelCase.executeQuery(); // NOSONAR
    while (!isFailed() && rs.next() && (m = extractCase(rs)) != null) {
      JobLogs.logEvery(++cntr, "read", "case bundle");
      JobLogs.logEvery(LOGGER, 10000, rowsReadCases.incrementAndGet(), "Total read", "cases");
      mapCases.put(m.getCaseId(), m);
    }
  }

  /**
   * @return complete list of potential case workers
   * @throws NeutronException on database error
   */
  protected Map<String, StaffPerson> readStaffWorkers() throws NeutronException {
    try {
      return staffPersonDao.findAll().stream()
          .collect(Collectors.toMap(StaffPerson::getId, w -> w));
    } catch (Exception e) {
      fail();
      throw new NeutronException("ERROR READING STAFF WORKERS", e);
    }
  }

  // protected CaseClientRelative extractClientCaseRelationship(final ResultSet rs)
  // throws SQLException {
  // final String caseId = rs.getString("CASE_ID");
  // String focusChildId = rs.getString("FOCUS_CHILD_ID");
  //
  // if (focusChildId == null) {
  // LOGGER.warn("FOCUS_CHILD_ID is null for CASE_ID: {}", caseId); // NOSONAR
  // return null;
  // }
  //
  // final EsCaseRelatedPerson ret = new EsCaseRelatedPerson();
  //
  // //
  // // Case:
  // //
  // ret.setCaseId(caseId);
  // final Map<String, ReplicatedClient> mapClients = this.allocMapClients.get();
  //
  // //
  // // Child (client):
  // //
  // final ReplicatedClient focusChild = mapClients.get(focusChildId);
  // ret.setFocusChildId(focusChildId);
  // ret.setFocusChildFirstName(ifNull(focusChild.getFirstName()));
  // ret.setFocusChildLastName(ifNull(focusChild.getLastName()));
  // ret.setFocusChildLastUpdated(focusChild.getLastUpdatedTime());
  // ret.setFocusChildSensitivityIndicator(focusChild.getSensitivityIndicator());
  //
  // //
  // // Relative (client):
  // //
  // final String focusInd = rs.getString("STANZA");
  // if (StringUtils.isBlank(focusInd) || !"1".equalsIgnoreCase(focusInd)) {
  // final ReplicatedClient client = mapClients.get(rs.getString("THIS_CLIENT_ID"));
  // ret.setParentId(client.getId());
  // ret.setParentSourceTable("CLIENT");
  // ret.setParentFirstName(client.getFirstName());
  // ret.setParentLastName(client.getLastName());
  // ret.setParentSensitivityIndicator(client.getSensitivityIndicator());
  // ret.setParentLastUpdated(client.getLastUpdatedTime());
  // translateParentRelationships(ret, rs.getShort("REL_FOCUS_TO_OTHER"),
  // rs.getShort("REL_OTHER_TO_FOCUS"));
  // }
  //
  // return ret;
  // }

  /**
   * Pulls <strong>Client/Case/Relationship</strong>.
   * 
   * @param rs result set
   */
  @Override
  public EsCaseRelatedPerson extract(final ResultSet rs) throws SQLException {
    return null;
  }

  protected ReplicatedClient extractClient(ResultSet rs) throws SQLException {
    ReplicatedClient ret = new ReplicatedClient();

    ret.setId(rs.getString("CLIENT_ID"));
    ret.setCommonFirstName(rs.getString("CLIENT_FIRST_NM"));
    ret.setCommonLastName(rs.getString("CLIENT_LAST_NM"));
    ret.setSensitivityIndicator(rs.getString("CLIENT_SENSITIVITY_IND"));
    ret.setLastUpdatedTime(rs.getTimestamp("CLIENT_LAST_UPDATED"));
    ret.setReplicationOperation(CmsReplicationOperation.valueOf(rs.getString("CLIENT_OPERATION")));
    ret.setReplicationDate(rs.getTimestamp("CLIENT_LOGMARKER"));

    return ret;
  }

  protected EsCaseRelatedPerson extractCase(ResultSet rs) throws SQLException {
    EsCaseRelatedPerson ret = new EsCaseRelatedPerson();

    ret.setCaseId(rs.getString("CASE_ID"));
    ret.setStartDate(rs.getDate("START_DATE"));
    ret.setEndDate(rs.getDate("END_DATE"));
    ret.setCaseLastUpdated(rs.getTimestamp("CASE_LAST_UPDATED"));
    ret.setCounty(rs.getInt("COUNTY"));
    ret.setServiceComponent(rs.getInt("SERVICE_COMP"));

    //
    // Worker (staff):
    //
    final String workerId = ifNull(rs.getString("WORKER_ID"));
    if (StringUtils.isNotBlank(workerId) && staffWorkers.containsKey(workerId)) {
      final StaffPerson staffPerson = staffWorkers.get(workerId);
      final EmbeddableStaffWorker worker = ret.getWorker();
      worker.setWorkerId(workerId);
      worker.setWorkerFirstName(staffPerson.getFirstName());
      worker.setWorkerLastName(staffPerson.getLastName());
      worker.setWorkerLastUpdated(staffPerson.getLastUpdatedTime());
    }

    //
    // Access Limitation:
    //
    ret.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    ret.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    ret.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return ret;
  }

  protected Map<String, ReplicatedClient> readClients(final PreparedStatement stmtSelClient,
      final Map<String, ReplicatedClient> mapClients) throws NeutronException {
    try {
      // Hibernate starts a *new* transaction *after* INSERT into temp table. :-(
      // return clientDao.findByTemp().stream()
      // .collect(Collectors.toMap(ReplicatedClient::getId, c -> c));

      stmtSelClient.setMaxRows(0);
      stmtSelClient.setQueryTimeout(0);
      stmtSelClient.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());

      LOGGER.info("pull client/case keys");
      final ResultSet rs = stmtSelClient.executeQuery(); // NOSONAR

      ReplicatedClient rc;
      while (!isFailed() && rs.next()) {
        rc = extractClient(rs);
        mapClients.put(rc.getId(), rc);
      }

    } catch (Exception e) {
      fail();
      throw new NeutronException("ERROR READING CLIENTS", e);
    }

    return mapClients;
  }

  // =====================
  // ASSEMBLY:
  // =====================

  private void collectCaseClients(final Map<String, Set<String>> mapCaseClients,
      final CaseClientRelative ccr) {
    // case => clients
    final String caseId = ccr.getCaseId();
    Set<String> clientCases = mapCaseClients.get(caseId);

    if (clientCases == null) {
      clientCases = new HashSet<>();
      mapCaseClients.put(caseId, clientCases);
    }

    clientCases.add(ccr.getFocusClientId());
    final String otherClient = ccr.getRelatedClientId();
    if (StringUtils.isNotBlank(otherClient)) {
      clientCases.add(otherClient);
    }

  }

  private void collectThisClientCase(final Map<String, Set<String>> mapClientCases, String caseId,
      String clientId) {
    Set<String> clientCases = mapClientCases.get(clientId);
    if (clientCases == null) {
      clientCases = new HashSet<>();
      mapClientCases.put(clientId, clientCases);
    }

    clientCases.add(caseId);
  }

  private void collectClientCases(final Map<String, Set<String>> mapClientCases,
      final CaseClientRelative ccr) {
    // client => cases
    final String caseId = ccr.getCaseId();
    String clientId = ccr.getFocusClientId();
    collectThisClientCase(mapClientCases, caseId, clientId);

    clientId = ccr.getRelatedClientId();
    if (StringUtils.isNotBlank(clientId)) {
      collectThisClientCase(mapClientCases, caseId, clientId);
    }
  }

  protected int assemblePieces(final List<CaseClientRelative> listCaseClientRelation,
      final Map<String, EsCaseRelatedPerson> mapCases,
      final Map<String, ReplicatedClient> mapClients,
      final Map<String, Set<String>> mapClientCases) {
    int countNormalized = 0;

    try {
      // CCR = Case/Client/Relation
      final List<CaseClientRelative> ccrs = listCaseClientRelation.stream()
          .sorted((e1, e2) -> e1.getCaseId().compareTo(e2.getCaseId()))
          .collect(Collectors.toList());

      // MAPS:
      // cases => clients
      // client => cases
      // client => parents

      // NEXT: case: focus child, parents.

      final Map<String, Set<String>> mapCaseClients = new HashMap<>(99881);
      final Map<String, Set<String>> mapClientParents = new HashMap<>(99881);

      for (CaseClientRelative ccr : ccrs) {
        collectCaseClients(mapCaseClients, ccr);
        collectClientCases(mapClientCases, ccr);
      }

      LOGGER.info("listCaseClientRelation.size(): {}", listCaseClientRelation.size());
      LOGGER.info("mapCases.size(): {}", mapCases.size());
      LOGGER.info("mapClients.size(): {}", mapClients.size());
      LOGGER.info("mapCaseClients.size(): {}", mapCaseClients.size());
      LOGGER.info("mapClientCases.size(): {}", mapClientCases.size());

      final Set<String> amber = mapClientCases.get("TMZGOO205B");
      LOGGER.info("Amber: {}", amber);
      amber.forEach(x -> LOGGER.info("x: {}", mapCases.get(x)));

      final Set<String> nina = mapClientCases.get("TBCF40g0D8");
      LOGGER.info("Nina: {}", nina);

    } finally {
      clearThreadContainers();
    }

    return countNormalized;
  }

  /**
   * Read all records from a single partition (key range), sort results, and normalize.
   * 
   * <p>
   * Each call to this method should run in its own thread.
   * </p>
   * 
   * @param p partition (key) range to read
   * @return number of client documents affected
   * @throws NeutronException on general error
   */
  protected int pullNextRange(final Pair<String, String> p) throws NeutronException {
    final String threadName =
        "extract_" + nextThreadNum.incrementAndGet() + "_" + p.getLeft() + "_" + p.getRight();
    nameThread(threadName);
    LOGGER.info("BEGIN");
    getFlightLog().markRangeStart(p);

    allocateThreadMemory(); // allocate thread local memory, if not done prior.
    final List<CaseClientRelative> listCaseClientRelative = allocCaseClientRelative.get();
    final Map<String, ReplicatedClient> mapClients = allocMapClients.get();
    final Map<String, EsCaseRelatedPerson> mapCasesById = allocMapCases.get();

    try (final Connection con = getConnection()) {
      final String schema = getDBSchemaName();
      con.setSchema(schema);
      con.setAutoCommit(false);
      NeutronDB2Util.enableParallelism(con);

      try (
          final PreparedStatement stmtInsClient =
              con.prepareStatement(CaseSQLResource.INSERT_CLIENT_FULL);
          final PreparedStatement stmtSelClient =
              con.prepareStatement(CaseSQLResource.SELECT_CLIENT_FULL);
          final PreparedStatement stmtSelCase =
              con.prepareStatement(CaseSQLResource.SELECT_CASE_DETAIL);
          final PreparedStatement stmtSelCaseClientRelationship =
              con.prepareStatement(CaseSQLResource.SELECT_CLIENT_CASE_RELATIONSHIP);
          final PreparedStatement stmtSelAlles =
              con.prepareStatement(getInitialLoadQuery(schema))) {
        prepClientBundle(stmtInsClient, p);
        readClients(stmtSelClient, mapClients);
        readCases(stmtSelCase, mapCasesById);
        readClientCaseRelationship(stmtSelCaseClientRelationship, listCaseClientRelative);
      } finally {
        con.commit();
      }

    } catch (Exception e) {
      fail();
      throw JobLogs.checked(LOGGER, e, "ERROR HANDLING RANGE {} - {}: {}", p.getLeft(),
          p.getRight(), e.getMessage());
    }

    int cntr =
        assemblePieces(listCaseClientRelative, mapCasesById, mapClients, allocMapClientCases.get());
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
      staffWorkers = readStaffWorkers();
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
      throw JobLogs.runtime(LOGGER, e, "ERROR! {}", e.getMessage());
    } finally {
      doneRetrieve();
      deallocateThreadMemory();
    }

    LOGGER.info("DONE: read {} ES case rows", this.rowsReadCases.get());
  }

  // =====================
  // THREAD MEMORY:
  // =====================

  protected void clearThreadContainers() {
    final List<EsCaseRelatedPerson> cases = allocCases.get();
    if (cases != null) {
      cases.clear();
      this.allocCaseClientRelative.get().clear();
      this.allocMapClientCases.get().clear();
      this.allocMapClients.get().clear();
      this.allocMapCases.get().clear();
      System.gc(); // NOSONAR
    }
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
      allocCases.set(new ArrayList<>(205000));
      allocCaseClientRelative.set(new ArrayList<>(205000));
      allocMapClientCases.set(new HashMap<>(99881)); // Prime
      allocMapCases.set(new HashMap<>(69029)); // Prime
      allocMapClients.set(new HashMap<>(69029)); // Prime
      clearThreadContainers();
    }
  }

  protected void deallocateThreadMemory() {
    if (allocCases.get() != null) {
      allocCases.set(null);
      allocMapClientCases.set(null);
      allocMapCases.set(null);
      allocMapClients.set(null);
      allocCaseClientRelative.set(null);
    }
  }

  public ReplicatedClientDao getClientDao() {
    return clientDao;
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

}
