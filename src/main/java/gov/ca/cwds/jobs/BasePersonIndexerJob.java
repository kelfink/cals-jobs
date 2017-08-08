package gov.ca.cwds.jobs;

import static gov.ca.cwds.data.persistence.cms.CmsPersistentObject.CMS_ID_LEN;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;
import gov.ca.cwds.jobs.util.transform.JobTransformUtils;

/**
 * Base person batch job to load clients from CMS into ElasticSearch.
 * 
 * <p>
 * This class implements {@link AutoCloseable} and automatically closes common resources, such as
 * {@link ElasticsearchDao} and Hibernate {@link SessionFactory}.
 * </p>
 * 
 * <p>
 * <strong>Auto mode ("smart" mode)</strong> takes the same parameters as last run and determines
 * whether the job has never been run. If the last run date is older than 50 years, then then assume
 * that the job is populating ElasticSearch for the first time and run all initial batch loads.
 * </p>
 * 
 * <h3>Command Line:</h3>
 * 
 * <pre>
 * {@code java gov.ca.cwds.jobs.ClientIndexerJob -c config/local.yaml -l /Users/CWS-NS3/client_indexer_time.txt}
 * </pre>
 * 
 * @author CWDS API Team
 * @param <T> ES storable, replicated Person persistence class
 * @param <M> MQT entity class, if any, or T
 * @see JobOptions
 */
public abstract class BasePersonIndexerJob<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends LastSuccessfulRunJob implements AutoCloseable, JobResultSetAware<M> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasePersonIndexerJob.class);

  private static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.NONE};

  private static final List<? extends ApiTypedIdentifier<String>> EMPTY_OPTIONAL_LIST =
      new ArrayList<>();

  private static final int DEFAULT_BATCH_WAIT = 25;
  private static final int DEFAULT_BUCKETS = 1;

  private static final int ES_BULK_SIZE = 2000;

  private static final int SLEEP_MILLIS = 2500;
  private static final int POLL_MILLIS = 3000;

  /**
   * Obsolete. Doesn't optimize on DB2 z/OS, though on "smaller" tables (single digit millions) it
   * runs just fine.
   * 
   * @see #doInitialLoadJdbc()
   */
  @Deprecated
  private static final String QUERY_BUCKET_LIST =
      "SELECT z.bucket, MIN(z.THE_ID_COL) AS minId, MAX(z.THE_ID_COL) AS maxId, COUNT(*) AS bucketCount "
          + "FROM (SELECT (y.rn / (total_cnt/THE_TOTAL_BUCKETS)) + 1 AS bucket, y.rn, y.THE_ID_COL FROM ( "
          + "SELECT c.THE_ID_COL, ROW_NUMBER() OVER (ORDER BY 1) AS rn, COUNT(*) OVER (ORDER BY 1) AS total_cnt "
          + "FROM {h-schema}THE_TABLE c ORDER BY c.THE_ID_COL) y ORDER BY y.rn "
          + ") z GROUP BY z.bucket FOR READ ONLY WITH UR ";

  /**
   * Guice Injector used for all Job instances during the life of this batch JVM.
   */
  protected static Injector injector;

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private static boolean testMode = false;

  /**
   * Jackson ObjectMapper.
   */
  protected final ObjectMapper mapper;

  /**
   * Main DAO for the supported persistence class.
   */
  protected final BaseDaoImpl<T> jobDao;

  /**
   * Elasticsearch client DAO.
   */
  protected final ElasticsearchDao esDao;

  /**
   * Hibernate session factory.
   */
  protected final SessionFactory sessionFactory;

  /**
   * Command line options for this job.
   */
  protected JobOptions opts;

  /**
   * Running count of records prepared for bulk indexing.
   */
  protected AtomicInteger recsPrepared = new AtomicInteger(0);

  /**
   * Running count of records prepared for bulk deletion.
   */
  protected AtomicInteger recsDeleted = new AtomicInteger(0);

  /**
   * Running count of records before bulk indexing.
   */
  protected AtomicInteger recsBulkBefore = new AtomicInteger(0);

  /**
   * Running count of records after bulk indexing.
   */
  protected AtomicInteger recsBulkAfter = new AtomicInteger(0);

  /**
   * Running count of errors during bulk indexing.
   */
  protected AtomicInteger recsBulkError = new AtomicInteger(0);

  /**
   * Official start time.
   */
  protected final long startTime = System.currentTimeMillis();

  /**
   * Queue of raw, denormalized records waiting to be normalized.
   */
  protected LinkedBlockingDeque<M> queueTransform = new LinkedBlockingDeque<>(100000);

  /**
   * Queue of normalized records waiting to publish to Elasticsearch.
   */
  protected LinkedBlockingDeque<T> queueLoad = new LinkedBlockingDeque<>(50000);

  /**
   * Completion flag for <strong>Extract</strong> method {@link #threadExtractJdbc()}.
   * <p>
   * Volatile guarantees that changes to this flag become visible other threads (i.e., don't cache a
   * copy of the flag in thread memory).
   * </p>
   */
  protected volatile boolean doneExtract = false;

  /**
   * Completion flag for <strong>Transform</strong> method {@link #threadTransform()}.
   */
  protected volatile boolean doneTransform = false;

  /**
   * Completion flag for <strong>Load</strong> method {@link #threadLoad()}.
   */
  protected volatile boolean doneLoad = false;

  private final Lock lock;
  private final Condition condition;

  // ======================
  // CONSTRUCTOR:
  // ======================

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param jobDao Person DAO, such as {@link ReplicatedClientDao}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public BasePersonIndexerJob(final BaseDaoImpl<T> jobDao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      SessionFactory sessionFactory) {
    super(lastJobRunTimeFilename);
    this.jobDao = jobDao;
    this.esDao = esDao;
    this.mapper = mapper;
    this.sessionFactory = sessionFactory;

    lock = new ReentrantLock();
    condition = lock.newCondition();

    java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.INFO);
  }

  /**
   * Get the view or materialized query table name, if used. Any child classes relying on a
   * denormalized view must define the name.
   * 
   * @return name of view or materialized query table or null if none
   */
  public String getInitialLoadViewName() {
    return null;
  }

  /**
   * Get initial load SQL query.
   * 
   * @param dbSchemaName The DB schema name
   * @return Inital load query
   */
  public String getInitialLoadQuery(String dbSchemaName) {
    return null;
  }

  /**
   * Optional method to customize JDBC ORDER BY clause on initial load.
   * 
   * @return custom ORDER BY clause for JDBC query
   */
  public String getJdbcOrderBy() {
    return null;
  }

  /**
   * Determine if limited access records must be deleted from ES.
   * 
   * @return True if limited access records must be deleted from ES, false otherwise.
   */
  public boolean mustDeleteLimitedAccessRecords() {
    return false;
  }

  /**
   * Mark a record for deletion. Intended for replicated records with deleted flag.
   * 
   * @param t bean to check
   * @return true if marked for deletion
   */
  protected boolean isDelete(T t) {
    return t instanceof CmsReplicatedEntity ? CmsReplicatedEntity.isDelete((CmsReplicatedEntity) t)
        : false;
  }

  /**
   * Build a delete request to remove the document from the index.
   * 
   * @param id primary key
   * @return bulk delete request
   * @throws JsonProcessingException unable to parse
   */
  public DeleteRequest bulkDelete(String id) throws JsonProcessingException {
    final String alias = getOpts().getIndexName();
    final String docType = esDao.getConfig().getElasticsearchDocType();
    return new DeleteRequest(alias, docType, id);
  }

  /**
   * Serialize object to JSON.
   * 
   * @param obj object to serialize
   * @return JSON for this screening
   */
  protected String jsonify(Object obj) {
    String ret = "";
    try {
      ret = mapper.writeValueAsString(obj);
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR SERIALIZING OBJECT {} TO JSON", obj);
    }
    return ret;
  }

  @Override
  public M extract(ResultSet rs) throws SQLException {
    return null;
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * @return Elasticsearch BulkProcessor
   */
  protected BulkProcessor buildBulkProcessor() {
    return BulkProcessor.builder(esDao.getClient(), new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
        recsBulkBefore.getAndAdd(request.numberOfActions());
        LOGGER.warn("Ready to execute bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        recsBulkAfter.getAndAdd(request.numberOfActions());
        LOGGER.warn("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        recsBulkError.getAndIncrement();
        LOGGER.error("ERROR EXECUTING BULK", failure);
      }
    }).setBulkActions(ES_BULK_SIZE).setConcurrentRequests(0).setName("jobs_bp").build();
  }

  // ======================
  // STATIC, INJECTION:
  // ======================

  /**
   * Build the Guice Injector once, which is used for all Job instances during the life of this
   * batch JVM.
   * 
   * @param opts command line options
   * @return Guice Injector
   * @throws JobsException if unable to construct dependencies
   */
  protected static synchronized Injector buildInjector(final JobOptions opts) throws JobsException {
    if (injector == null) {
      try {
        injector = Guice.createInjector(
            new JobsGuiceInjector(new File(opts.getEsConfigLoc()), opts.getLastRunLoc()));

        /**
         * Initialize system code cache
         */
        injector.getInstance(gov.ca.cwds.rest.api.domain.cms.SystemCodeCache.class);

      } catch (CreationException e) {
        throw JobLogUtils.buildException(LOGGER, e, "FAILED TO BUILD INJECTOR!: {}",
            e.getMessage());
      }
    }

    return injector;
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   * @param <T> Person persistence type
   * @throws JobsException if unable to parse command line or load dependencies
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newJob(final Class<T> klass,
      String... args) throws JobsException {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final T ret = buildInjector(opts).getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      throw JobLogUtils.buildException(LOGGER, e, "FAILED TO CREATE JOB!: {}", e.getMessage());
    }
  }

  /**
   * Batch job entry point.
   * 
   * <p>
   * This method automatically closes the Hibernate session factory and ElasticSearch DAO and EXITs
   * the JVM.
   * </p>
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   * @throws JobsException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void runJob(final Class<T> klass,
      String... args) throws JobsException {
    int exitCode = 0;
    try (final T job = newJob(klass, args)) { // Close resources automatically.
      job.run();
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      exitCode = 1;
      throw JobLogUtils.buildException(LOGGER, e, "JOB FAILED!: {}", e.getMessage());
    } finally {
      // WARNING: kills the JVM in testing but may be needed to shutdown resources.
      if (!isTestMode()) {
        // Shutdown all remaining resources, even those not attached to this job.
        Runtime.getRuntime().exit(exitCode); // NOSONAR
      }
    }
  }

  // ===================
  // BUILD ES PERSON:
  // ===================

  /**
   * Handle both {@link ApiMultiplePersonAware} and {@link ApiPersonAware} implementations of type
   * T.
   * 
   * @param p instance of type T
   * @return array of person documents
   * @throws JsonProcessingException on parse error
   * @see #buildElasticSearchPersonDoc(ApiPersonAware)
   * @see #buildElasticSearchPerson(PersistentObject)
   */
  protected ElasticSearchPerson[] buildElasticSearchPersons(T p) throws JsonProcessingException {
    ElasticSearchPerson[] ret;
    if (p instanceof ApiMultiplePersonAware) {
      final ApiPersonAware[] persons = ((ApiMultiplePersonAware) p).getPersons();
      ret = new ElasticSearchPerson[persons.length];
      int i = 0;
      for (ApiPersonAware px : persons) {
        ret[i++] = buildElasticSearchPersonDoc(px);
      }
    } else {
      ret = new ElasticSearchPerson[] {buildElasticSearchPerson(p)};
    }
    return ret;
  }

  /**
   * Produce an ElasticSearchPerson suitable as an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  protected ElasticSearchPerson buildElasticSearchPerson(T p) throws JsonProcessingException {
    return buildElasticSearchPersonDoc((ApiPersonAware) p);
  }

  /**
   * Produce an ElasticSearchPerson objects suitable for an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  protected ElasticSearchPerson buildElasticSearchPersonDoc(ApiPersonAware p)
      throws JsonProcessingException {
    return ElasticTransformer.buildElasticSearchPersonDoc(mapper, p);
  }

  /**
   * Identifier column for this table. Defaults to "IDENTIFIER", the most common key name in legacy
   * DB2.
   * 
   * @return Identifier column
   */
  protected String getIdColumn() {
    return "IDENTIFIER";
  }

  /**
   * Trim a String.
   * 
   * @param value String to trim
   * @return trimmed String or null
   */
  public String ifNull(String value) {
    return JobTransformUtils.ifNull(value);
  }

  /**
   * Getter for the entity class of this job's view or materialized query table, if any, or null if
   * none.
   * 
   * @return entity class of view or materialized query table
   */
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return null;
  }

  /**
   * Default normalize method just returns the input. Child classes may customize this method to
   * normalize de-normalized result sets (view records) to normalized entities (parent/child)
   * records.
   * 
   * @param recs entity records
   * @return unmodified entity records
   * @see EntityNormalizer
   */
  @SuppressWarnings("unchecked")
  protected List<T> normalize(List<M> recs) {
    return (List<T>) recs;
  }

  /**
   * Normalize view records for a single grouping (such as all the same client) into a normalized
   * entity bean, consisting of a parent object and its child objects.
   * 
   * @param recs denormalized view beans
   * @return normalized entity bean instance
   */
  protected T normalizeSingle(List<M> recs) {
    final List<T> list = normalize(recs);
    return list != null && !list.isEmpty() ? list.get(0) : null;
  }

  /**
   * Override to customize the default number of buckets by job.
   * 
   * @return default total buckets
   */
  protected int getJobTotalBuckets() {
    return DEFAULT_BUCKETS;
  }

  /**
   * True if the Job class reduces denormalized results to normalized ones.
   * 
   * @return true if class overrides {@link #normalize(List)}
   */
  protected final boolean isViewNormalizer() {
    return getDenormalizedClass() != null;
  }

  // ===================
  // COMMON JSON:
  // ===================

  /**
   * Publish a Person record to Elasticsearch with a bulk processor.
   * 
   * <p>
   * Child implementations may customize this method and generate different JSON for create/insert
   * and update to prevent overwriting data from other jobs.
   * </p>
   * 
   * @param bp {@link #buildBulkProcessor()} for this thread
   * @param t Person record to write
   * @throws JsonProcessingException if unable to serialize JSON
   * @throws IOException if unable to prepare request
   * @see #prepareUpsertRequest(ElasticSearchPerson, PersistentObject)
   */
  protected void prepareDocument(BulkProcessor bp, T t) throws IOException {
    Arrays.stream(buildElasticSearchPersons(t)).map(p -> prepareUpsertRequestNoChecked(p, t))
        .forEach(bp::add);
  }

  /**
   * Set optional ES person collections before serializing JSON for insert. Child classes which
   * handle optional collections should override this method.
   *
   * <p>
   * <strong>Example:</strong>
   * </p>
   * 
   * <pre>
   * {@code esp.setScreenings((List<ElasticSearchPerson.ElasticSearchPersonScreening>) col);}
   * </pre>
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param list list of ES child objects
   */
  protected void setInsertCollections(ElasticSearchPerson esp, T t,
      List<? extends ApiTypedIdentifier<String>> list) {
    // Default, no-op.
  }

  /**
   * Set optional ES person collections to null so that they are not overwritten by accident. Child
   * classes do not normally override this method.
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @throws JsonProcessingException on JSON processing error
   */
  protected void prepareInsertCollections(ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
          throws JsonProcessingException {

    // Null out optional collections for updates.
    esp.clearOptionalCollections(keep);

    // Child classes: Set optional collections before serializing the insert JSON.
    setInsertCollections(esp, t, list);
  }

  /**
   * Prepare upsert JSON (update and insert). Child classes do not normally override this method.
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @return Pair of JSON, left = update, right = insert
   * @throws JsonProcessingException on JSON processing error
   */
  protected Pair<String, String> prepareUpsertJson(ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
          throws JsonProcessingException {

    // Child classes: Set optional collections before serializing the insert JSON.
    prepareInsertCollections(esp, t, elementName, list, keep);
    final String insertJson = mapper.writeValueAsString(esp);

    String updateJson;
    if (StringUtils.isNotBlank(elementName)) {
      StringBuilder buf = new StringBuilder();
      buf.append("{\"").append(elementName).append("\":[");

      if (list != null && !list.isEmpty()) {
        buf.append(list.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      }

      buf.append("]}");
      updateJson = buf.toString();
    } else {
      updateJson = insertJson;
    }

    return Pair.of(updateJson, insertJson);
  }

  /**
   * Prepare an "upsert" request without a checked exception. Throws runtime {@link JobsException}
   * on error. This method's signature is easier to use in functional lambda and stream calls than
   * older styles.
   * 
   * @param esp person document object
   * @param t normalized entity
   * @return prepared upsert request
   */
  @SuppressWarnings("rawtypes")
  protected DocWriteRequest prepareUpsertRequestNoChecked(ElasticSearchPerson esp, T t) {
    DocWriteRequest<?> ret;
    try {
      if (isDelete(t)) {
        ret = bulkDelete((String) t.getPrimaryKey());
        recsDeleted.getAndIncrement();
      } else {
        recsPrepared.getAndIncrement();
        ret = prepareUpsertRequest(esp, t);
      }
    } catch (Exception e) {
      throw JobLogUtils.buildException(LOGGER, e, "ERROR BUILDING UPSERT!: PK: {}",
          t.getPrimaryKey());
    }

    return ret;
  }

  /**
   * Prepare sections of a document for update. Elasticsearch automatically updates the provided
   * sections. Some jobs should only write sub-documents, such as screenings or allegations, from a
   * new data source, like Intake PostgreSQL, but should NOT overwrite document details from legacy.
   * 
   * <p>
   * Default handler just serializes the whole ElasticSearchPerson instance to JSON and returns the
   * same JSON for both insert and update. Child classes should override this method and null out
   * any fields that should not be updated.
   * </p>
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @return left = insert JSON, right = update JSON throws JsonProcessingException on JSON parse
   *         error
   * @throws JsonProcessingException on JSON parse error
   * @throws IOException on Elasticsearch disconnect
   */
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, T t) throws IOException {
    String id = esp.getId();

    // Set id and legacy id.
    if (t instanceof ApiLegacyAware) {
      ApiLegacyAware l = (ApiLegacyAware) t;
      final boolean hasLegacyId =
          StringUtils.isNotBlank(l.getLegacyId()) && l.getLegacyId().trim().length() == CMS_ID_LEN;

      if (hasLegacyId) {
        id = l.getLegacyId();
        esp.setLegacyId(id);
      } else {
        id = esp.getId();
      }
    } else if (t instanceof CmsReplicatedEntity) {
      esp.setLegacyId(t.getPrimaryKey().toString());
    }

    // Set the legacy source table, if appropriate for this job.
    if (StringUtils.isNotBlank(getLegacySourceTable())) {
      esp.setLegacySourceTable(getLegacySourceTable());
    }

    // Child classes may override these methods as needed.
    // left = update, right = insert.
    final Pair<String, String> json = prepareUpsertJson(esp, t, getOptionalElementName(),
        getOptionalCollection(esp, t), keepCollections());

    final String alias = getOpts().getIndexName();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    // "Upsert": update if doc exists, insert if it does not.
    return new UpdateRequest(alias, docType, id).doc(json.getLeft())
        .upsert(new IndexRequest(alias, docType, id).source(json.getRight()));
  }

  // ===================
  // OPTIONAL ELEMENTS:
  // ===================

  /**
   * Which optional ES collections to retain for insert JSON. Child classes that populate optional
   * collections should override this method.
   * 
   * @return array of optional collections to keep in insert JSON
   */
  protected ESOptionalCollection[] keepCollections() {
    return KEEP_COLLECTIONS;
  }

  /**
   * Get the optional element name populated by this job or null if none.
   * 
   * @return optional element name
   */
  protected String getOptionalElementName() {
    return null;
  }

  /**
   * Return the optional collection used to build the update JSON, if any. Child classes that
   * populate optional collections should override this method.
   * 
   * @param esp ES person document object
   * @param t normalized type
   * @return List of ES person elements
   */
  protected List<? extends ApiTypedIdentifier<String>> getOptionalCollection(
      ElasticSearchPerson esp, T t) {
    return EMPTY_OPTIONAL_LIST;
  }

  // =================
  // RUN THREADS:
  // =================

  /**
   * ENTRY POINT FOR INITIAL LOAD.
   * 
   * <p>
   * Continue processing until
   * </p>
   * 
   * @throws IOException on JDBC error or Elasticsearch disconnect
   */
  protected void doInitialLoadJdbc() throws IOException {
    Thread.currentThread().setName("main");
    try {
      new Thread(this::threadExtractJdbc).start(); // Extract
      new Thread(this::threadTransform).start(); // Transform
      new Thread(this::threadLoad).start(); // Load

      while (!(fatalError || (doneExtract && doneTransform && doneLoad))) {
        LOGGER.debug("runInitialLoad: sleep");
        Thread.sleep(SLEEP_MILLIS);

        try {
          this.jobDao.find("abc123"); // dummy call, keep connection pool alive.
        } catch (HibernateException he) { // NOSONAR
          LOGGER.trace("DIRECT JDBC. IGNORE HIBERNATE ERROR: {}", he.getMessage());
        } catch (Exception e) { // NOSONAR
          LOGGER.warn("Hibernate keep-alive error: {}", e.getMessage());
        }
      }

      Thread.sleep(SLEEP_MILLIS);
      this.close();
      final long endTime = System.currentTimeMillis();
      LOGGER.warn("TOTAL ELAPSED TIME: " + ((endTime - startTime) / 1000) + " SECONDS");
      LOGGER.warn("DONE: doInitialLoadViaJdbc");

    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.warn("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "GENERAL EXCEPTION: {}", e);
    } finally {
      doneExtract = true;
    }
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers.
   */
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.info("BEGIN: Stage #1: extract");

    try {
      Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      con.setReadOnly(true); // WARNING: fails with Postgres.

      // Linux MQT lacks ORDER BY clause. Must sort manually.
      // Either detect platform or force ORDER BY clause.
      final String query = getInitialLoadQuery(getDBSchemaName());
      M m;

      /**
       * Enable parallelism for underlying database
       */
      enableParallelism(con);

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(100000);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        int cntr = 0;
        while (!fatalError && rs.next()) {
          // Hand the baton to the next runner ...
          JobLogUtils.logEvery(++cntr, "Retrieved", "recs");
          if ((m = extract(rs)) != null) {
            queueTransform.putLast(m);
          }
        }

        con.commit();
      } finally {
        // The statement closes automatically.
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: Stage #1: Extract");
  }

  /**
   * The "transform" part of ETL. Single thread consumer, second stage of initial load. Convert
   * denormalized view records to normalized ones and pass to the load queue.
   */
  protected void threadTransform() {
    Thread.currentThread().setName("transform");
    LOGGER.info("BEGIN: Stage #2: Transform");

    int cntr = 0;
    Object lastId = new Object();
    M m;
    T t;
    List<M> grpRecs = new ArrayList<>();

    while (!(fatalError || (doneExtract && queueTransform.isEmpty()))) {
      try {
        while ((m = queueTransform.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
          JobLogUtils.logEvery(++cntr, "Transformed", "recs");

          // NOTE: Assumes that records are sorted by group key.
          if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1) {
            // End of group. Normalize these group recs.
            if ((t = normalizeSingle(grpRecs)) != null) {
              queueLoad.putLast(t);
              grpRecs.clear(); // Single thread, re-use memory.
            }
          }

          grpRecs.add(m);
          lastId = m.getNormalizationGroupKey();
        }

        // Last bundle.
        if (!grpRecs.isEmpty() && (t = normalizeSingle(grpRecs)) != null) {
          queueLoad.putLast(t);
          grpRecs.clear(); // Single thread, re-use memory.
        }

      } catch (InterruptedException e) { // NOSONAR
        LOGGER.error("Transformer interrupted!");
        fatalError = true;
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        fatalError = true;
        JobLogUtils.raiseError(LOGGER, e, "Transformer: fatal error {}", e.getMessage());
      } finally {
        doneTransform = true;
      }
    }

    LOGGER.info("DONE: Stage #2: Transform");
  }

  /**
   * The "load" part of ETL. Read from normalized record queue and push to ES.
   */
  protected void threadLoad() {
    Thread.currentThread().setName("load");
    final BulkProcessor bp = buildBulkProcessor();
    int cntr = 0;
    T t;

    LOGGER.warn("BEGIN: Stage #3: Load");
    try {
      while (!(fatalError || (doneExtract && doneTransform && queueLoad.isEmpty()))) {
        while ((t = queueLoad.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
          JobLogUtils.logEvery(++cntr, "Published", "recs to ES");
          prepareDocument(bp, t);
        }
      }

      // Just to be sure ...
      while ((t = queueLoad.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
        JobLogUtils.logEvery(++cntr, "Published", "recs to ES");
        prepareDocument(bp, t);
      }

      LOGGER.info("Flush ES bulk processor ...");
      bp.flush();

      Thread.sleep(SLEEP_MILLIS);

      LOGGER.info("Flush ES bulk processor again ...");
      bp.flush();

      LOGGER.info("Waiting to close ES bulk processor ...");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      LOGGER.info("Closed ES bulk processor");

    } catch (InterruptedException e) { // NOSONAR
      LOGGER.warn("Publisher interrupted!");
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (JsonProcessingException e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "Publisher: JsonProcessingException {}", e.getMessage());
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "Publisher: fatal error {}", e.getMessage());
    } finally {
      doneLoad = true;
    }

    LOGGER.warn("DONE: Stage #3: ES loader");
  }

  // =================
  // LAST RUN MODE:
  // =================

  /**
   * Prepare a document.
   * 
   * @param bp bulk processor
   * @param p document object
   */
  protected void prepLastRunDoc(BulkProcessor bp, T p) {
    try {
      // Write persistence object to Elasticsearch Person document.
      prepareDocument(bp, p);
    } catch (JsonProcessingException e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "ERROR WRITING JSON: {}", e.getMessage());
    } catch (IOException e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "IO EXCEPTION: {}", e.getMessage());
    } finally {
      doneLoad = true;
    }
  }

  /**
   * <strong>ENTRY POINT FOR LAST RUN.</strong>
   *
   * <p>
   * Fetch all records for the next batch run, either by bucket or last successful run date. Pulls
   * either from an MQT via {@link #extractLastRunRecsFromView(Date, Set)}, if
   * {@link #isViewNormalizer()} is overridden, else from the base table directly via
   * {@link #extractLastRunRecsFromTable(Date)}.
   * </p>
   * 
   * @param lastRunDt last time the batch ran successfully.
   * @return List of results to process
   * @see gov.ca.cwds.jobs.LastSuccessfulRunJob#_run(java.util.Date)
   */
  protected Date doLastRun(Date lastRunDt) {
    try {
      // One bulk processor for "last run" operations. BulkProcessor itself is thread-safe.
      final BulkProcessor bp = buildBulkProcessor();
      Set<String> deletionResults = new HashSet<>();

      final List<T> results =
          this.isViewNormalizer() ? extractLastRunRecsFromView(lastRunDt, deletionResults)
              : extractLastRunRecsFromTable(lastRunDt);

      if (results != null && !results.isEmpty()) {
        LOGGER.warn(MessageFormat.format("Found {0} people to index", results.size()));

        results.stream().forEach(p -> {
          prepLastRunDoc(bp, p);
        });
      }

      /**
       * Delete records identified for deletion...
       */
      if (!deletionResults.isEmpty()) {
        LOGGER.warn(MessageFormat.format("Found {0} people to delete", deletionResults.size())
            + ", IDs: " + deletionResults);
        final String alias = getOpts().getIndexName();
        final String docType = esDao.getConfig().getElasticsearchDocType();

        for (String deletionId : deletionResults) {
          DeleteRequest deleteRequest = new DeleteRequest(alias, docType, deletionId);
          bp.add(deleteRequest);
        }

        // Track counts.
        recsDeleted.getAndAdd(deletionResults.size());
      }

      // Give it time to finish the last batch.
      LOGGER.info("Waiting on ElasticSearch to finish last batch ...");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      return new Date(this.startTime);

    } catch (Exception e) {
      fatalError = true;
      throw JobLogUtils.buildException(LOGGER, e, "General Exception: {}", e.getMessage());
    } finally {
      doneLoad = true;
    }
  }

  // ====================
  // RUN MODE DECISION:
  // ====================

  /**
   * If last run time is provide in options then use it, otherwise use provided
   * lastSuccessfulRunTime.
   * 
   * @param lastSuccessfulRunTime last successful run
   * @return appropriate date to detect changes
   */
  protected Date calcLastRunDate(final Date lastSuccessfulRunTime) {
    Date ret;
    final Date lastSuccessfulRunTimeOverride = getOpts().getLastRunTime();

    if (lastSuccessfulRunTimeOverride != null) {
      ret = lastSuccessfulRunTimeOverride;
    } else {
      final Calendar cal = Calendar.getInstance();
      cal.setTime(lastSuccessfulRunTime);
      cal.add(Calendar.MINUTE, -25); // 25 minute window
      ret = cal.getTime();
    }

    return ret;
  }

  /**
   * Lambda runs a number of threads up to max processor cores. Queued jobs wait until a worker
   * thread is available.
   * 
   * <p>
   * Auto mode ("smart" mode) takes the same parameters as last run and determines whether the job
   * has never been run. If the last run date is older than 50 years, then then assume that the job
   * is populating ElasticSearch for the first time and run all initial batch loads.
   * </p>
   * 
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.jobs.LastSuccessfulRunJob#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      final int maxThreads = Math.min(Runtime.getRuntime().availableProcessors(), 4);
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
          String.valueOf(maxThreads));
      LOGGER.info("Processors={}", maxThreads);

      /**
       * If index name is provided then use it, otherwise use alias from ES config.
       */
      String indexNameOverride = getOpts().getIndexName();
      String effectiveIndexName = StringUtils.isBlank(indexNameOverride)
          ? esDao.getConfig().getElasticsearchAlias() : indexNameOverride;
      getOpts().setIndexName(effectiveIndexName);

      final Date effectiveLastSuccessfulRunTime = calcLastRunDate(lastSuccessfulRunTime);
      String documentType = esDao.getConfig().getElasticsearchDocType();
      String peopleSettingsFile = "/elasticsearch/setting/people-index-settings.json";
      String personMappingFile = "/elasticsearch/mapping/map_person_5x_snake.json";

      LOGGER.info("Effective index name: " + effectiveIndexName);
      LOGGER.info(
          "Effective last successsful run time: " + effectiveLastSuccessfulRunTime.toString());

      // If the index is missing, create it.
      LOGGER.debug("Create index if missing, effectiveIndexName: " + effectiveIndexName);
      esDao.createIndexIfNeeded(effectiveIndexName, documentType, peopleSettingsFile,
          personMappingFile);

      // Smart/auto mode:
      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -50);
      final boolean autoMode =
          this.opts.isLastRunMode() && effectiveLastSuccessfulRunTime.before(cal.getTime());

      if (autoMode) {
        LOGGER.warn("AUTO MODE!");
        getOpts().setStartBucket(1);
        getOpts().setEndBucket(1);
        getOpts().setTotalBuckets(getJobTotalBuckets());

        if (this.getDenormalizedClass() != null) {
          LOGGER.warn("LOAD FROM VIEW VIA JDBC!");
          doInitialLoadJdbc();
        } else {
          LOGGER.warn("LOAD REPLICATED TABLE QUERY VIA HIBERNATE!");
          extractHibernate();
        }

      } else if (this.opts == null || this.opts.isLastRunMode()) {
        LOGGER.warn("LAST RUN MODE!");
        doLastRun(effectiveLastSuccessfulRunTime);
      } else {
        LOGGER.warn("DIRECT BUCKET MODE!");
        extractHibernate();
      }

      // Result stats:
      LOGGER.info(
          "STATS: \nRecs To Index: {}\nRecs To Delete: {}\nrecsBulkBefore: {}\nrecsBulkAfter: {}\nrecsBulkError: {}",
          recsPrepared, recsDeleted, recsBulkBefore, recsBulkAfter, recsBulkError);

      LOGGER.info("Updating last successful run time to {}", jobDateFormat.format(startTime));
      return new Date(this.startTime);

    } catch (Exception e) {
      fatalError = true;
      throw JobLogUtils.buildException(LOGGER, e, "GENERAL EXCEPTION: {}", e.getMessage());
    } finally {

      // Set ETL completion flags to done.
      doneExtract = true;
      doneTransform = true;
      doneLoad = true;

      try {
        this.close();
      } catch (IOException io) {
        LOGGER.warn("IOException on close! {}", io.getMessage(), io);
      }
    }
  }

  // ===================
  // PULL RECORDS:
  // ===================

  /**
   * Pull records changed since the last successful run.
   * 
   * <p>
   * If this job defines a denormalized view entity, then pull from that. Otherwise, pull from the
   * table entity.
   * </p>
   * 
   * @param lastRunTime last successful run time
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromTable(Date lastRunTime) {
    LOGGER.info("last successful run: {}", lastRunTime);
    final Class<?> entityClass = jobDao.getEntityClass();

    String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    Session session = jobDao.getSessionFactory().getCurrentSession();
    Transaction txn = session.beginTransaction();

    try {
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setTimestamp("after", new Timestamp(lastRunTime.getTime()));

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<T> recs = q.list();

      LOGGER.warn("FOUND {} RECORDS", recs.size());
      results.addAll(recs);

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      fatalError = true;
      LOGGER.error("EXTRACT ERROR! {}", h.getMessage(), h);
      txn.rollback();
      throw new DaoException(h);
    } finally {
      doneExtract = true;
    }
  }

  /**
   * Pull from view for last run mode.
   * 
   * @param lastRunTime last successful run time
   * @param deletionResults records to remove
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromView(Date lastRunTime, Set<String> deletionResults) {
    LOGGER.info("PULL VIEW: last successful run: {}", lastRunTime);
    final Class<?> entityClass = getDenormalizedClass(); // view entity class
    String namedQueryName;

    if (getOpts().isLoadSealedAndSensitive()) {
      /**
       * Load everything
       */
      namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    } else {
      /**
       * Load only unlimited access data
       */
      namedQueryName = entityClass.getName() + ".findAllUpdatedAfterWithUnlimitedAccess";
    }

    Session session = jobDao.getSessionFactory().getCurrentSession();

    Object lastId = new Object();
    Transaction txn = session.beginTransaction();

    try {
      /**
       * Load records to index
       */
      NativeQuery<M> q = session.getNamedNativeQuery(namedQueryName);
      q.setTimestamp("after", new Timestamp(lastRunTime.getTime()));

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      List<M> recs = q.list();
      LOGGER.warn("FOUND {} RECORDS", recs.size());

      // Convert denormalized view rows to normalized persistence objects.
      final List<M> groupRecs = new ArrayList<>();
      for (M m : recs) {
        if (!lastId.equals(m.getNormalizationGroupKey()) && !groupRecs.isEmpty()) {
          results.add(normalizeSingle(groupRecs));
          groupRecs.clear();
        }

        groupRecs.add(m);
        lastId = m.getNormalizationGroupKey();
        if (lastId == null) {
          // This could be due to data error (invalid data in db)
          LOGGER.warn("NULL Normalization Group Key: " + m);
          lastId = new Object();
        }
      }

      if (!groupRecs.isEmpty()) {
        results.add(normalizeSingle(groupRecs));
      }

      /**
       * Load records to delete.
       */
      if (mustDeleteLimitedAccessRecords()) {
        String namedQueryNameForDeletion =
            entityClass.getName() + ".findAllUpdatedAfterWithLimitedAccess";
        NativeQuery<M> queryForDeletion = session.getNamedNativeQuery(namedQueryNameForDeletion);
        queryForDeletion.setTimestamp("after", new Timestamp(lastRunTime.getTime()));

        final List<M> deletionRecs = queryForDeletion.list();

        if (deletionRecs != null && !deletionRecs.isEmpty()) {
          for (M rec : deletionRecs) {
            /**
             * Assuming group key represents ID of client to delete. This is true for client,
             * referral history, case history jobs.
             */
            Object groupKey = rec.getNormalizationGroupKey();
            if (groupKey != null) {
              deletionResults.add(groupKey.toString());
            }
          }
        }
        LOGGER.warn("FOUND {} RECORDS FOR DELETION", deletionResults.size());
      }

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      fatalError = true;
      txn.rollback();
      throw JobLogUtils.buildException(LOGGER, h, "EXTRACT ERROR!: {}", h.getMessage());
    } finally {
      doneExtract = true;
    }
  }

  /**
   * Get the table or view used to allocate bucket ranges. Called on full load only.
   * 
   * @return the table or view used to allocate bucket ranges
   */
  protected String getDriverTable() {
    String ret = null;
    final Table tbl = this.jobDao.getEntityClass().getDeclaredAnnotation(Table.class);
    if (tbl != null) {
      ret = tbl.name();
    }

    return ret;
  }

  // ===================
  // MANAGE RESOURCES:
  // ===================

  @Override
  public synchronized void close() throws IOException {
    if (fatalError || (doneExtract && doneTransform && doneLoad)) {
      LOGGER.warn("CLOSING CONNECTIONS!!");

      if (this.esDao != null) {
        LOGGER.warn("CLOSING ES DAO");
        this.esDao.close();
      }

      if (this.sessionFactory != null) {
        LOGGER.warn("CLOSING SESSION FACTORY");
        this.sessionFactory.close();
      }

    } else {
      LOGGER.warn("CLOSE: FALSE ALARM");
    }
  }

  /**
   * Finish job and close resources. Default implementation exits the JVM.
   */
  @Override
  protected synchronized void finish() {
    LOGGER.warn("FINISH JOB AND SHUTDOWN!");
    try {
      this.doneExtract = true;
      this.doneLoad = true;
      this.doneTransform = true;

      close();
      Thread.sleep(SLEEP_MILLIS); // NOSONAR

      // Shutdown all remaining resources, even those not attached to this job.
      // Runtime.getRuntime().exit(0); // NOSONAR

    } catch (InterruptedException e) {
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (IOException ioe) {
      fatalError = true;
      throw JobLogUtils.buildException(LOGGER, ioe, "ERROR FINISHING JOB: {}", ioe.getMessage());
    }
  }

  // ===========================
  // DEPRECATED:
  // ===========================

  /**
   * Build the bucket list at runtime.
   * 
   * @param table the driver table
   * @return batch buckets
   * @deprecated use {@link #threadExtractJdbc()} or {@link #extractHibernate()} instead
   */
  @Deprecated
  @SuppressWarnings("unchecked")
  protected List<BatchBucket> buildBucketList(String table) {
    List<BatchBucket> ret = new ArrayList<>();

    Transaction txn = null;
    try {
      LOGGER.info("FETCH DYNAMIC BUCKET LIST FOR TABLE {}", table);
      final Session session = jobDao.getSessionFactory().getCurrentSession();
      txn = session.beginTransaction();
      final long totalBuckets = opts.getTotalBuckets() < getJobTotalBuckets() ? getJobTotalBuckets()
          : opts.getTotalBuckets();
      final javax.persistence.Query q = jobDao.getSessionFactory().createEntityManager()
          .createNativeQuery(QUERY_BUCKET_LIST.replaceAll("THE_TABLE", table)
              .replaceAll("THE_ID_COL", getIdColumn())
              .replaceAll("THE_TOTAL_BUCKETS", String.valueOf(totalBuckets)), BatchBucket.class);

      ret = q.getResultList();
      session.clear();
      txn.commit();
    } catch (HibernateException e) {
      LOGGER.error("BATCH ERROR! ", e);
      fatalError = true;
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(e);
    } finally {
      doneLoad = true;
    }

    return ret;
  }

  /**
   * Return a list of partition keys to optimize batch SELECT statements. See ReplicatedClient
   * native named query, "findPartitionedBuckets".
   * 
   * @return list of partition key pairs
   * @see ReplicatedClient
   * @deprecated use {@link #threadExtractJdbc()} or {@link #extractHibernate()} instead
   */
  @Deprecated
  protected List<Pair<String, String>> getPartitionRanges() {
    LOGGER.info("DETERMINE BUCKET RANGES ...");
    List<Pair<String, String>> ret = new ArrayList<>();
    List<BatchBucket> buckets = buildBucketList(getDriverTable());

    for (BatchBucket b : buckets) {
      LOGGER.warn("BUCKET RANGE: {} to {}", b.getMinId(), b.getMaxId());
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

    return ret;
  }

  /**
   * Divide work into buckets: pull a unique range of identifiers so that no bucket results overlap.
   * 
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @deprecated use {@link #threadExtractJdbc()} or {@link #extractHibernate()} instead
   * @return collection of entity results
   */
  @Deprecated
  protected List<T> pullBucketRange(String minId, String maxId) {
    LOGGER.info("PULL BUCKET RANGE {} to {}", minId, maxId);
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findBucketRange";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = session.beginTransaction();
    try {
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setString("min_id", minId).setString("max_id", maxId);

      // No reduction/normalization.
      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      results.addAll(q.list());

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException e) {
      fatalError = true;
      LOGGER.error("BATCH ERROR! {}", e.getMessage(), e);
      txn.rollback();
      throw new DaoException(e);
    }
  }

  /**
   * Pull replicated records from named query "findBucketRange".
   * 
   * <p>
   * Thread safety: both BulkProcessor are ElasticsearchDao are thread-safe.
   * </p>
   * 
   * @return number of records processed
   * @see #pullBucketRange(String, String)
   */
  protected int extractHibernate() {
    final List<Pair<String, String>> buckets = getPartitionRanges();

    for (Pair<String, String> b : buckets) {
      final String minId = b.getLeft();
      final String maxId = b.getRight();
      final List<T> results = pullBucketRange(minId, maxId);

      if (results != null && !results.isEmpty()) {
        final BulkProcessor bp = buildBulkProcessor();
        results.stream().forEach(p -> {
          try {
            prepareDocument(bp, p);
          } catch (JsonProcessingException e) {
            throw JobLogUtils.buildException(LOGGER, e, "JSON ERROR: id: {}, {}", p.getPrimaryKey(),
                e.getMessage());
          } catch (IOException e) {
            throw JobLogUtils.buildException(LOGGER, e, "IO ERROR: id: {}, {}", p.getPrimaryKey(),
                e.getMessage());
          }
        });

        try {
          bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
        } catch (Exception e2) {
          fatalError = true;
          throw new JobsException("ERROR EXTRACTING VIA HIBERNATE!", e2);
        } finally {
          doneExtract = true;
        }

        // Track counts.
        recsPrepared.getAndAdd(results.size());
      }
    }

    return recsPrepared.get();
  }

  // ===========================
  // ACCESSORS:
  // ===========================

  /**
   * Getter for this job's options.
   * 
   * @return this job's options
   */
  public JobOptions getOpts() {
    return opts;
  }

  /**
   * Setter for this job's options.
   * 
   * @param opts this job's options
   */
  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

  /**
   * Get the legacy source table for this job, if any.
   * 
   * @return legacy source table
   * @deprecated Logic moved to ApiLegacyAware implementation classes
   */
  @Deprecated
  protected String getLegacySourceTable() {
    return null;
  }

  /**
   * Batch job entry point.
   * 
   * @param klass batch job class
   * @param args command line arguments
   */
  @SuppressWarnings("rawtypes")
  public static void runMain(final Class<? extends BasePersonIndexerJob> klass, String... args) {
    LOGGER.info("Run job {}", klass.getName());
    try {
      runJob(klass, args);
    } catch (Exception e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @return whether in test mode
   */
  public static boolean isTestMode() {
    return testMode;
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @param testMode whether in test mode
   */
  public static void setTestMode(boolean testMode) {
    BasePersonIndexerJob.testMode = testMode;
  }

  /**
   * @return default CMS schema name
   */
  protected static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  private static void enableParallelism(Connection con) throws SQLException {
    String dbProductName = con.getMetaData().getDatabaseProductName();
    if (StringUtils.containsIgnoreCase(dbProductName, "db2")) {
      con.nativeSQL("SET CURRENT DEGREE = 'ANY'");
    }
  }
}
