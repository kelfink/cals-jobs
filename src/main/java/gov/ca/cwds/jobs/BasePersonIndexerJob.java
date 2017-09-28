package gov.ca.cwds.jobs;

import static gov.ca.cwds.data.persistence.cms.CmsPersistentObject.CMS_ID_LEN;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
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
import com.google.inject.Inject;

import gov.ca.cwds.dao.ApiLegacyAware;
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
import gov.ca.cwds.jobs.component.JobFeatureCore;
import gov.ca.cwds.jobs.component.JobFeatureHibernate;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;
import gov.ca.cwds.jobs.util.transform.JobElasticPersonDocPrep;

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
    extends LastSuccessfulRunJob implements AutoCloseable, JobResultSetAware<M>,
    JobElasticPersonDocPrep<T>, JobFeatureHibernate, JobFeatureCore {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(BasePersonIndexerJob.class);

  private static final String SQL_COLUMN_AFTER = "after";

  private static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.NONE};

  private static final List<? extends ApiTypedIdentifier<String>> EMPTY_OPTIONAL_LIST =
      new ArrayList<>();

  /**
   * Obsolete. Doesn't optimize on DB2 z/OS, though on "smaller" tables (single digit millions) it's
   * not too bad (table scan performance).
   * 
   * @deprecated other options now
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
   * Track job progress.
   */
  protected final JobProgressTrack track = new JobProgressTrack();

  /**
   * Queue of raw, de-normalized records waiting to be normalized.
   * <p>
   * NOTE: some jobs normalize on their own, since the step is inexpensive.
   * </p>
   */
  protected volatile LinkedBlockingDeque<M> queueTransform = new LinkedBlockingDeque<>(100000);

  /**
   * Queue of normalized records waiting to publish to Elasticsearch.
   */
  protected volatile LinkedBlockingDeque<T> queueIndex = new LinkedBlockingDeque<>(250000);

  /**
   * Read/write lock for extract threads and sources, such as JDBC, Hibernate, or even flat files.
   */
  protected final ReadWriteLock lock;

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

    ElasticTransformer.setMapper(mapper);
    lock = new ReentrantReadWriteLock(false);
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

  @Override
  public M extract(final ResultSet rs) throws SQLException {
    return null;
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
   * @param obj object to serialize
   * @return JSON for this screening
   * @see ElasticTransformer#jsonify(Object)
   */
  public String jsonify(Object obj) {
    return ElasticTransformer.jsonify(obj);
  }

  /**
   * Just adds an object to the normalized index queue and traps InterruptedException. Suitable for
   * streams and lambda.
   * 
   * @param norm object to add to index queue
   */
  protected void addToIndexQueue(T norm) {
    try {
      JobLogUtils.logEvery(track.getRecsSentToIndexQueue().incrementAndGet(),
          "added to index queue", "recs");
      queueIndex.putLast(norm);
    } catch (InterruptedException e) {
      fatalError = true;
      Thread.currentThread().interrupt();
      JobLogUtils.raiseError(LOGGER, e, "INTERRUPTED! {}", e.getMessage());
    }
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * @return Elasticsearch BulkProcessor
   */
  public BulkProcessor buildBulkProcessor() {
    return BulkProcessor.builder(esDao.getClient(), new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
        track.getRecsBulkBefore().getAndAdd(request.numberOfActions());
        LOGGER.debug("Ready to execute bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        track.getRecsBulkAfter().getAndAdd(request.numberOfActions());
        LOGGER.info("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        track.getRecsBulkError().getAndIncrement();
        LOGGER.error("ERROR EXECUTING BULK", failure);
      }
    }).setBulkActions(ES_BULK_SIZE).setBulkSize(new ByteSizeValue(14, ByteSizeUnit.MB))
        .setConcurrentRequests(1).setName("jobs_bp").build();
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
   * @param recs de-normalized view beans
   * @return normalized entity bean instance
   */
  protected T normalizeSingle(List<M> recs) {
    JobLogUtils.logEvery(track.getRowsNormalized().incrementAndGet(), "Normalize", "single");
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

  // ===================
  // ELASTICSEARCH:
  // ===================

  public void pushToBulkProcessor(BulkProcessor bp, DocWriteRequest<?> t) {
    JobLogUtils.logEvery(track.getRecsSentToBulkProcessor().incrementAndGet(), "add to es bulk",
        "push doc");
    bp.add(t);
  }

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
    Arrays.stream(ElasticTransformer.buildElasticSearchPersons(t))
        .map(p -> prepareUpsertRequestNoChecked(p, t)).forEach(x -> { // NOSONAR
          pushToBulkProcessor(bp, x);
        });
  }

  /**
   * Prepare an "upsert" request without a checked exception. Throws runtime {@link JobsException}
   * on error. This method's signature is easier to use in functional lambda and stream calls than
   * other method signatures.
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
        track.getRecsBulkDeleted().getAndIncrement();
      } else {
        track.getRecsBulkPrepared().getAndIncrement();
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
   *        {@link ElasticTransformer#buildElasticSearchPersonDoc(ApiPersonAware)}
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
    final Pair<String, String> json = ElasticTransformer.prepareUpsertJson(this, esp, t,
        getOptionalElementName(), getOptionalCollection(esp, t), keepCollections());

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
      final Thread threadIndexer = new Thread(this::threadIndex); // Index
      threadIndexer.start();

      Thread threadTransformer = null;
      if (useTransformThread()) {
        threadTransformer = new Thread(this::threadTransform); // Transform
        threadTransformer.start();
      }

      final Thread threadJdbc = new Thread(this::threadExtractJdbc); // Extract
      threadJdbc.start();

      while (!(fatalError || (doneExtract && doneTransform && doneLoad))) {
        LOGGER.debug("runInitialLoad: sleep");
        Thread.sleep(SLEEP_MILLIS);

        try {
          this.jobDao.find("abc1234567"); // dummy call, keep connection pool alive.
        } catch (HibernateException he) { // NOSONAR
          LOGGER.trace("DIRECT JDBC. IGNORE HIBERNATE ERROR: {}", he.getMessage());
        } catch (Exception e) { // NOSONAR
          LOGGER.warn("Hibernate keep-alive error: {}", e.getMessage());
        }
      }

      threadJdbc.join();
      if (useTransformThread() && threadTransformer != null) {
        threadTransformer.join();
      }

      threadIndexer.join();

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

    try (final Connection con = jobDao.getSessionFactory().getSessionFactoryOptions()
        .getServiceRegistry().getService(ConnectionProvider.class).getConnection()) {
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
      JobDB2Utils.enableParallelism(con);

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(15000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(100000);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        int cntr = 0;
        while (!fatalError && rs.next() && (m = extract(rs)) != null) {
          // Hand the baton to the next runner ...
          JobLogUtils.logEvery(++cntr, "Retrieved", "recs");
          queueTransform.putLast(m);
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
   * de-normalized view records to normalized ones and pass to the load queue.
   */
  protected void threadTransform() {
    Thread.currentThread().setName("transform");
    LOGGER.info("BEGIN: Transform thread");

    int cntr = 0;
    Object lastId = new Object();
    M m;
    T t;
    final List<M> grpRecs = new ArrayList<>();

    while (!(fatalError || (doneExtract && queueTransform.isEmpty()))) {
      try {
        while ((m = queueTransform.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
          JobLogUtils.logEvery(++cntr, "Transformed", "recs");

          // NOTE: Assumes that records are sorted by group key.
          // End of group. Normalize these group recs.
          if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1
              && (t = normalizeSingle(grpRecs)) != null) {
            LOGGER.trace("queueIndex.putLast: id: {}", t.getPrimaryKey());
            queueIndex.putLast(t);
            grpRecs.clear(); // Single thread, re-use memory.
            Thread.yield();
          }

          grpRecs.add(m);
          lastId = m.getNormalizationGroupKey();
          Thread.yield();
        }

        // Last bundle.
        if (!grpRecs.isEmpty() && (t = normalizeSingle(grpRecs)) != null) {
          queueIndex.putLast(t);
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

    LOGGER.info("DONE: Transform thread");
  }

  /**
   * Reusable method to poll index queue, track counts, and bulk prepare documents.
   * 
   * @param bp ES bulk processor
   * @param cntr record count
   * @throws IOException on IO error
   * @throws InterruptedException if thread interrupted
   * @return number of documents prepared
   */
  protected int bulkPrepare(final BulkProcessor bp, int cntr)
      throws IOException, InterruptedException {
    int i = cntr;
    T t;

    while ((t = queueIndex.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
      LOGGER.trace("queueIndex.pollFirst: id {}", t.getPrimaryKey());
      JobLogUtils.logEvery(++i, "Indexed", "recs to ES");
      prepareDocument(bp, t);
    }
    return i;
  }

  /**
   * The "load" part of ETL. Read from normalized record queue and push to ES.
   */
  protected void threadIndex() {
    Thread.currentThread().setName("es_indexer");
    final BulkProcessor bp = buildBulkProcessor();
    int cntr = 0;

    LOGGER.info("BEGIN: Indexer thread");
    try {
      while (!(fatalError || (doneExtract && doneTransform && queueIndex.isEmpty()))) {
        LOGGER.trace("Stage #3: Index: just *do* something ...");
        cntr = bulkPrepare(bp, cntr);
      }

      // Just to be sure ...
      cntr = bulkPrepare(bp, cntr);
      LOGGER.info("Flush ES bulk processor ... recs processed: {}", cntr);
      bp.flush();

      Thread.sleep(SLEEP_MILLIS);
      bp.flush();

      LOGGER.info("Waiting to close ES bulk processor ...");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      LOGGER.info("Closed ES bulk processor");

    } catch (InterruptedException e) { // NOSONAR
      LOGGER.warn("Indexer interrupted!");
      fatalError = true;
      Thread.currentThread().interrupt();
      JobLogUtils.raiseError(LOGGER, e, "Indexer: Interrupted! {}", e.getMessage());
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "Indexer: fatal error {}", e.getMessage());
    } finally {
      doneLoad = true;
    }

    LOGGER.info("DONE: Indexer thread");
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
      final Set<String> deletionResults = new HashSet<>();

      final List<T> results =
          this.isViewNormalizer() ? extractLastRunRecsFromView(lastRunDt, deletionResults)
              : extractLastRunRecsFromTable(lastRunDt);

      if (results != null && !results.isEmpty()) {
        LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
        results.stream().forEach(p -> { // NOSONAR
          prepLastRunDoc(bp, p);
        });
      }

      // Delete records identified for deletion...
      if (!deletionResults.isEmpty()) {
        LOGGER.warn(MessageFormat.format("Found {0} people to delete", deletionResults.size())
            + ", IDs: " + deletionResults);
        final String alias = getOpts().getIndexName();
        final String docType = esDao.getConfig().getElasticsearchDocType();

        for (String deletionId : deletionResults) {
          bp.add(new DeleteRequest(alias, docType, deletionId));
        }

        track.getRecsBulkDeleted().getAndAdd(deletionResults.size());
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

  /**
   * @return true if the job provides its own key ranges
   */
  protected boolean isRangeSelfManaging() {
    return false;
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
      // If index name is provided then use it, otherwise use alias from ES config.
      String indexNameOverride = getOpts().getIndexName();
      String effectiveIndexName = StringUtils.isBlank(indexNameOverride)
          ? esDao.getConfig().getElasticsearchAlias() : indexNameOverride;
      getOpts().setIndexName(effectiveIndexName);

      final Date effectiveLastSuccessfulRunTime = calcLastRunDate(lastSuccessfulRunTime);
      final String documentType = esDao.getConfig().getElasticsearchDocType();
      final String peopleSettingsFile = "/elasticsearch/setting/people-index-settings.json";
      final String personMappingFile = "/elasticsearch/mapping/map_person_5x_snake.json";

      LOGGER.info("Effective index name: " + effectiveIndexName);
      LOGGER.info("Last successsful run time: " + effectiveLastSuccessfulRunTime.toString());

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
        // WARNING: don't overwrite command line settings.
        getOpts().setStartBucket(1);
        getOpts().setEndBucket(1);
        getOpts().setTotalBuckets(getJobTotalBuckets());

        if (this.getDenormalizedClass() != null) {
          LOGGER.info("LOAD FROM VIEW USING JDBC!");
          doInitialLoadJdbc();
        } else {
          LOGGER.info("LOAD REPLICATED TABLE QUERY USING HIBERNATE!");
          extractHibernate();
        }

      } else if (this.opts == null || this.opts.isLastRunMode()) {
        LOGGER.info("LAST RUN MODE!");
        doLastRun(effectiveLastSuccessfulRunTime);
      } else {
        LOGGER.info("DIRECT BUCKET MODE!");
        if (isRangeSelfManaging()) {
          doInitialLoadJdbc();
        } else {
          extractHibernate();
        }
      }

      LOGGER.info(
          "STATS: \nRecs To Index:  {}\nRecs To Delete: {}\nrecsBulkBefore: {}\nrecsBulkAfter:  {}\nrecsBulkError:  {}",
          track.getRecsBulkPrepared(), track.getRecsBulkDeleted(), track.getRecsBulkBefore(),
          track.getRecsBulkAfter(), track.getRecsBulkError());

      LOGGER.info("Updating last successful run time to {}",
          new SimpleDateFormat(LAST_RUN_DATE_FORMAT).format(startTime));
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
   * If this job defines a de-normalized view entity, then pull from that. Otherwise, pull from the
   * table entity.
   * </p>
   * 
   * @param lastRunTime last successful run time
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromTable(Date lastRunTime) {
    LOGGER.info("last successful run: {}", lastRunTime);
    final Class<?> entityClass = jobDao.getEntityClass();

    final String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    final Session session = jobDao.getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();

    try {
      final NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setString(SQL_COLUMN_AFTER, JobJdbcUtils.makeSimpleTimestampString(lastRunTime));

      final ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
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
    final String namedQueryName =
        getOpts().isLoadSealedAndSensitive() ? entityClass.getName() + ".findAllUpdatedAfter"
            : entityClass.getName() + ".findAllUpdatedAfterWithUnlimitedAccess";

    final Session session = jobDao.getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();
    Object lastId = new Object();

    try {
      prepHibernateLastChange(session, txn, lastRunTime);

      /**
       * Load records to index
       */
      final NativeQuery<M> q = session.getNamedNativeQuery(namedQueryName);
      q.setString(SQL_COLUMN_AFTER, JobJdbcUtils.makeSimpleTimestampString(lastRunTime));

      final ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<M> recs = q.list();
      LOGGER.warn("FOUND {} RECORDS", recs.size());

      // Convert de-normalized view rows to normalized persistence objects.
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
        final String namedQueryNameForDeletion =
            entityClass.getName() + ".findAllUpdatedAfterWithLimitedAccess";
        final NativeQuery<M> queryForDeletion =
            session.getNamedNativeQuery(namedQueryNameForDeletion);
        queryForDeletion.setString(SQL_COLUMN_AFTER,
            JobJdbcUtils.makeSimpleTimestampString(lastRunTime));

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
    } catch (SQLException h) {
      fatalError = true;
      txn.rollback();
      throw JobLogUtils.buildException(LOGGER, h, "EXTRACT SQL ERROR!: {}", h.getMessage());
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
   * Finish job and close resources.
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
    } catch (InterruptedException e) {
      fatalError = true;
      Thread.currentThread().interrupt();
      throw JobLogUtils.buildException(LOGGER, e, "INTERRUPTED!: {}", e.getMessage());
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
              .replaceAll("THE_ID_COL", getIdColumn()).replaceAll("THE_TOTAL_BUCKETS",
                  String.valueOf(totalBuckets)),
              BatchBucket.class);

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
   * <p>
   * Prefer methods {@link #threadExtractJdbc()} or {@link #extractHibernate()} over this one.
   * </p>
   * 
   * @return list of partition key pairs
   * @see ReplicatedClient
   */
  protected List<Pair<String, String>> getPartitionRanges() {
    LOGGER.info("DETERMINE BUCKET RANGES ...");
    final List<Pair<String, String>> ret = new ArrayList<>();
    final List<BatchBucket> buckets = buildBucketList(getDriverTable());

    for (BatchBucket b : buckets) {
      LOGGER.warn("BUCKET RANGE: {} to {}", b.getMinId(), b.getMaxId());
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

    return ret;
  }

  /**
   * Execute JDBC prior to calling method {@link #pullBucketRange(String, String)}.
   * 
   * <blockquote>
   * 
   * <pre>
   * final Work work = new Work() {
   *   &#64;Override
   *   public void execute(Connection connection) throws SQLException {
   *     // Run JDBC here.
   *   }
   * };
   * session.doWork(work);
   * </pre>
   * 
   * </blockquote>
   * 
   * @param session current Hibernate session
   * @param txn current transaction
   * @param lastRunTime last successful run datetime
   * @throws SQLException on disconnect, invalid parameters, etc.
   */
  protected void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime) throws SQLException {
    if (StringUtils.isNotBlank(getPrepLastChangeSQL())) {
      JobJdbcUtils.prepHibernateLastChange(session, txn, lastRunTime, getPrepLastChangeSQL());
    }
  }

  /**
   * Divide work into buckets: pull a unique range of identifiers so that no bucket results overlap.
   * 
   * <p>
   * Where possible, prefer use {@link #threadExtractJdbc()} or {@link #extractHibernate()} instead.
   * 
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @return collection of entity results
   */
  @SuppressWarnings("unchecked")
  protected List<T> pullBucketRange(String minId, String maxId) {
    LOGGER.info("PULL BUCKET RANGE {} to {}", minId, maxId);
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findBucketRange";
    final Session session = jobDao.getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();
    try {
      final NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setString("min_id", minId).setString("max_id", maxId).setCacheable(false)
          .setFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheMode(CacheMode.IGNORE)
          .setFetchSize(DEFAULT_FETCH_SIZE);

      // No reduction/normalization. Iterate, process, flush.
      final ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
      final ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        Object[] row = results.get();
        ret.add((T) row[0]);

        if (((++cnt) % DEFAULT_FETCH_SIZE) == 0) {
          LOGGER.info("recs read: {}", cnt);
          session.flush();
        }
      }

      session.flush();
      results.close();
      txn.commit();
      return ret.build();
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
   * Thread safety: ElasticsearchDao is thread-safe, but BulkProcessor is <strong>NOT</strong>.
   * Construct one BulkProcessor per thread.
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
      }
    }

    return track.getRecsBulkPrepared().get();
  }

  // ===========================
  // ACCESSORS:
  // ===========================

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
  public static void runStandalone(final Class<? extends BasePersonIndexerJob> klass,
      String... args) {
    LOGGER.info("Run job {}", klass.getName());
    try {
      JobRunner.runStandalone(klass, args);
    } catch (Exception e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

  /**
   * @return default CMS schema name
   */
  public static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  /**
   * @see JobDB2Utils#isDB2OnZOS(BaseDaoImpl)
   * @return true if DB2 on mainframe
   */
  public boolean isDB2OnZOS() {
    return JobDB2Utils.isDB2OnZOS(jobDao);
  }

}
