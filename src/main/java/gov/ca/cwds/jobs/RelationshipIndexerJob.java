package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.ReplicatedRelationships;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtil;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.jobs.util.jdbc.NeutronThreadUtil;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.InitialLoadJdbcRocket;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Job to load family relationships from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class RelationshipIndexerJob
    extends InitialLoadJdbcRocket<ReplicatedRelationships, EsRelationship>
    implements NeutronRowMapper<EsRelationship> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipIndexerJob.class);

  /**
   * SQL to insert last changed client list into global temporary table for use in views,
   * VW_LST_BI_DIR_RELATION and VW_LST_REL_CLN_RELT_CLIENT.
   */
  static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO GT_ID (IDENTIFIER)\nSELECT clnr.IDENTIFIER\nFROM CLN_RELT CLNR\n"
          + "WHERE CLNR.IBMSNAP_LOGMARKER > ?\nUNION ALL\nSELECT clnr.IDENTIFIER\n"
          + "FROM CLN_RELT CLNR\nJOIN CLIENT_T CLNS ON CLNR.FKCLIENT_T = CLNS.IDENTIFIER\n"
          + "WHERE CLNS.IBMSNAP_LOGMARKER > ?\nUNION ALL\nSELECT clnr.IDENTIFIER\n"
          + "FROM CLN_RELT CLNR\nJOIN CLIENT_T CLNP ON CLNR.FKCLIENT_0 = CLNP.IDENTIFIER\n"
          + "WHERE CLNP.IBMSNAP_LOGMARKER > ?";

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Relationship View DAO
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run file
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param flightPlan command line options
   */
  @Inject
  public RelationshipIndexerJob(final ReplicatedRelationshipsDao dao, final ElasticsearchDao esDao,
      @LastRunFile String lastRunFile, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, sessionFactory, flightPlan);
    EsRelationship.SonarQubeMemoryBloatComplaintCache.getInstance().clearCache();
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_BI_DIR_RELATION";
  }

  @Override
  public EsRelationship extract(ResultSet rs) throws SQLException {
    return EsRelationship.mapRow(rs);
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID ";
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsRelationship.class;
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.THIS_LEGACY_ID BETWEEN ':fromId' AND ':toId' ");

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
      buf.append(" AND x.THIS_SENSITIVITY_IND = 'N' AND x.RELATED_SENSITIVITY_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Send all recs for the same group id to the index queue.
   * 
   * @param grpRecs recs for same client id
   */
  protected void normalizeAndQueueIndex(final List<EsRelationship> grpRecs) {
    grpRecs.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential().sorted()
        .collect(Collectors.groupingBy(EsRelationship::getThisLegacyId)).entrySet().stream()
        .map(e -> normalizeSingle(e.getValue())).forEach(this::addToIndexQueue);
  }

  /**
   * Iterate results sets from {@link #pullRange(Pair)}.
   * 
   * @param rs result set
   * @throws SQLException on database error
   */
  @Override
  public void handleRangeResults(final ResultSet rs) throws SQLException {
    int cntr = 0;
    EsRelationship m;
    Object lastId = new Object();
    final List<EsRelationship> grpRecs = new ArrayList<>();

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

  @Override
  public int nextThreadNumber() {
    return nextThreadNum.incrementAndGet();
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers. This job normalizes **without**
   * the transform thread.
   */
  @Override
  protected void threadRetrieveByJdbc() {
    nameThread("relationship_extract");
    LOGGER.info("BEGIN: main extract thread");
    doneTransform(); // No transform thread

    EsRelationship.SonarQubeMemoryBloatComplaintCache.getInstance().clearCache();
    try {
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.info(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
      final List<ForkJoinTask<?>> tasks = new ArrayList<>(ranges.size());
      final ForkJoinPool threadPool =
          new ForkJoinPool(NeutronThreadUtil.calcReaderThreads(getFlightPlan()));

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        tasks.add(threadPool.submit(() -> pullRange(p)));
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
      EsRelationship.SonarQubeMemoryBloatComplaintCache.getInstance().clearCache();
    }

    LOGGER.info("DONE: relationship extract thread");
  }

  @Override
  public boolean useTransformThread() {
    return false;
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
    return "relationships";
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedRelationships p)
      throws NeutronException {
    return prepareUpsertRequest(esp, p, p.getRelations());
  }

  @Override
  public ReplicatedRelationships normalizeSingle(List<EsRelationship> recs) {
    return !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedRelationships> normalize(List<EsRelationship> recs) {
    return EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(recs);
  }

  @Override
  public synchronized void close() throws IOException {
    EsRelationship.SonarQubeMemoryBloatComplaintCache.getInstance().clearCache();
    super.close();
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(RelationshipIndexerJob.class, args);
  }

}
