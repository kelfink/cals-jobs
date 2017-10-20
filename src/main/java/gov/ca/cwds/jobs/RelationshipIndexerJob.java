package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
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
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.schedule.JobRunner;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load family relationships from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class RelationshipIndexerJob
    extends BasePersonIndexerJob<ReplicatedRelationships, EsRelationship>
    implements JobResultSetAware<EsRelationship> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipIndexerJob.class);

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
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public RelationshipIndexerJob(final ReplicatedRelationshipsDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public EsRelationship extract(ResultSet rs) throws SQLException {
    return EsRelationship.mapRow(rs);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsRelationship.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_BI_DIR_RELATION";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.THIS_LEGACY_ID BETWEEN ':fromId' AND ':toId' ");

    if (!getOpts().isLoadSealedAndSensitive()) {
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
   * Read recs from a single partition.
   * 
   * @param p partition range to read
   */
  protected void pullRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.info("BEGIN: extract thread {}", threadName);
    getTrack().trackRangeStart(p);

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);

      final String query = getInitialLoadQuery(getDBSchemaName()).replaceAll(":fromId", p.getLeft())
          .replaceAll(":toId", p.getRight());
      LOGGER.info("query: {}", query);
      JobDB2Utils.enableParallelism(con);

      int cntr = 0;
      EsRelationship m;
      Object lastId = new Object();
      final List<EsRelationship> grpRecs = new ArrayList<>();

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(0);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        // NOTE: Assumes that records are sorted by group key.
        while (!isFailed() && rs.next() && (m = extract(rs)) != null) {
          JobLogs.logEvery(++cntr, "Retrieved", "recs");
          if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1) {
            normalizeAndQueueIndex(grpRecs);
            grpRecs.clear(); // Single thread, re-use memory.
          }

          grpRecs.add(m);
          lastId = m.getNormalizationGroupKey();
        }

        con.commit();
      }

    } catch (Exception e) {
      fail();
      JobLogs.raiseError(LOGGER, e, "FAILED TO PULL RANGE! {}-{} : {}", p.getLeft(), p.getRight(),
          e.getMessage());
    }

    getTrack().trackRangeComplete(p);
    LOGGER.info("DONE: Extract thread {}", i);
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers. This job normalizes **without**
   * the transform thread.
   */
  @Override
  protected void threadRetrieveByJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");
    doneTransform();

    try {
      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.info(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);
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

    } catch (Exception e) {
      fail();
      JobLogs.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneRetrieve();
    }

    LOGGER.info("DONE: main extract thread");
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
  protected List<Pair<String, String>> getPartitionRanges() {
    return JobJdbcUtils.getCommonPartitionRanges64(this);
  }

  @Override
  public String getOptionalElementName() {
    return "relationships";
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedRelationships p)
      throws IOException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"relationships\":[");

    if (!p.getRelations().isEmpty()) {
      try {
        buf.append(p.getRelations().stream().map(ElasticTransformer::jsonify)
            .sorted(String::compareTo).collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogs.raiseError(LOGGER, e, "ERROR SERIALIZING RELATIONSHIPS! {}", e.getMessage());
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  public ReplicatedRelationships normalizeSingle(List<EsRelationship> recs) {
    return !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedRelationships> normalize(List<EsRelationship> recs) {
    return EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(RelationshipIndexerJob.class, args);
  }

}
