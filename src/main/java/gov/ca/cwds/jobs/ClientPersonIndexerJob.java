package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsClientPerson;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.atom.AtomValidateDocument;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.InitialLoadJdbcRocket;
import gov.ca.cwds.neutron.util.jdbc.NeutronJdbcUtils;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Rocket to load Client person data from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientPersonIndexerJob extends InitialLoadJdbcRocket<ReplicatedClient, EsClientPerson>
    implements NeutronRowMapper<EsClientPerson>, AtomValidateDocument {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientPersonIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO GT_ID (IDENTIFIER)\n" + "SELECT CLT.IDENTIFIER \nFROM CLIENT_T clt\n"
          + "WHERE CLT.IBMSNAP_LOGMARKER > ?\nUNION\n" + "SELECT CLT.IDENTIFIER "
          + "FROM CLIENT_T clt\n" + "JOIN CL_ADDRT cla ON clt.IDENTIFIER = cla.FKCLIENT_T \n"
          + "WHERE CLA.IBMSNAP_LOGMARKER > ?\nUNION\n" + "SELECT CLT.IDENTIFIER "
          + "FROM CLIENT_T clt\n" + "JOIN CL_ADDRT cla ON clt.IDENTIFIER = cla.FKCLIENT_T\n"
          + "JOIN ADDRS_T  adr ON cla.FKADDRS_T  = adr.IDENTIFIER\n"
          + "WHERE ADR.IBMSNAP_LOGMARKER > ?";

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public ClientPersonIndexerJob(final ReplicatedClientDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastRunFile, final ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
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
  public EsClientPerson extract(ResultSet rs) throws SQLException {
    return EsClientPerson.extract(rs);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsClientPerson.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "MQT_CLIENT_ADDRESS";
  }

  @Override
  public String getMQTName() {
    return getInitialLoadViewName();
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.clt_identifier ";
  }

  /**
   * Send all recs for same client id to the index queue.
   * 
   * @param grpRecs recs for same client id
   */
  protected void normalizeAndQueueIndex(final List<EsClientPerson> grpRecs) {
    grpRecs.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential().sorted()
        .collect(Collectors.groupingBy(EsClientPerson::getNormalizationGroupKey)).entrySet()
        .stream().map(e -> normalizeSingle(e.getValue())).forEach(this::addToIndexQueue);
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();

    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.clt_identifier BETWEEN ':fromId' AND ':toId' ");

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
      buf.append(" AND x.CLT_SENSTV_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialLoadProcessRangeResults(final ResultSet rs) throws SQLException {
    int cntr = 0;
    EsClientPerson m;
    Object lastId = new Object();
    final List<EsClientPerson> grpRecs = new ArrayList<>();

    // NOTE: Assumes that records are sorted by group key.
    while (!isFailed() && rs.next() && (m = extract(rs)) != null) {
      JobLogs.logEvery(LOGGER, ++cntr, "Retrieved", "recs");
      if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1) {
        normalizeAndQueueIndex(grpRecs);
        grpRecs.clear(); // Single thread, re-use memory.
      }

      grpRecs.add(m);
      lastId = m.getNormalizationGroupKey();
    }
  }

  /**
   * Validate that addresses are found in ES and vice versa.
   * 
   * @param client client address to check
   * @param person person document
   * @return true if addresses pass validation
   */
  public boolean validateAddresses(final ReplicatedClient client,
      final ElasticSearchPerson person) {
    final String clientId = person.getId();
    final Map<String, ReplicatedAddress> repAddresses =
        client.getClientAddresses().stream().flatMap(ca -> ca.getAddresses().stream())
            .collect(Collectors.toMap(ReplicatedAddress::getId, a -> a));

    final Map<String, ElasticSearchPersonAddress> docAddresses = person.getAddresses().stream()
        .collect(Collectors.toMap(ElasticSearchPersonAddress::getId, a -> a));

    for (ElasticSearchPersonAddress docAddr : docAddresses.values()) {
      if (!repAddresses.containsKey(docAddr.getAddressId())) {
        LOGGER.warn("DOC ADDRESS ID {} NOT FOUND IN DATABASE {}", docAddr.getAddressId(), clientId);
        return false;
      }
    }

    for (ReplicatedAddress repAddr : repAddresses.values()) {
      if (!docAddresses.containsKey(repAddr.getAddressId())) {
        LOGGER.warn("ADDRESS ID {} NOT FOUND IN DOCUMENT {}", repAddr.getAddressId(), clientId);
        return false;
      }
    }

    LOGGER.warn("set size: docAddresses: {}, repAddresses: {}, client addrs: {}, doc addrs: {}",
        docAddresses.size(), repAddresses.size(), client.getClientAddresses().size(),
        person.getAddresses().size());

    return true;
  }

  @Override
  public boolean validateDocument(final ElasticSearchPerson person) throws NeutronException {
    final String clientId = person.getId();
    LOGGER.info("Validate client: {}", clientId);

    // HACK: Initialize transaction. Fix DAO impl instead.
    getOrCreateTransaction();
    final ReplicatedClient client = getJobDao().find(clientId);

    return client.getCommonFirstName().equals(person.getFirstName())
        && client.getCommonLastName().equals(person.getLastName())
        && client.getCommonMiddleName().equals(person.getMiddleName())
        && validateAddresses(client, person);
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers. This job normalizes **without**
   * the transform thread.
   */
  @Override
  protected void threadRetrieveByJdbc() {
    pullMultiThreadJdbc();
  }

  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  public List<Pair<String, String>> getPartitionRanges() throws NeutronException {
    return NeutronJdbcUtils.getCommonPartitionRanges64(this);
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
  public List<ReplicatedClient> normalize(List<EsClientPerson> recs) {
    return EntityNormalizer.<ReplicatedClient, EsClientPerson>normalizeList(recs);
  }

  @Override
  public int nextThreadNumber() {
    return nextThreadNum.incrementAndGet();
  }

  @Override
  public ESOptionalCollection[] keepCollections() {
    return new ESOptionalCollection[] {ESOptionalCollection.AKA, ESOptionalCollection.SAFETY_ALERT};
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   * @throws Exception unhandled launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(ClientPersonIndexerJob.class, args);
  }

}
