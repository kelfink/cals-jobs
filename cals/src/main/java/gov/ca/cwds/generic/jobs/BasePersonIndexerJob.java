package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.generic.dao.cms.BatchBucket;
import gov.ca.cwds.generic.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.generic.jobs.component.AtomHibernate;
import gov.ca.cwds.generic.jobs.component.AtomInitialLoad;
import gov.ca.cwds.generic.jobs.component.AtomPersonDocPrep;
import gov.ca.cwds.generic.jobs.component.AtomSecurity;
import gov.ca.cwds.generic.jobs.component.AtomTransformer;
import gov.ca.cwds.generic.jobs.component.JobBulkProcessorBuilder;
import gov.ca.cwds.generic.jobs.component.JobProgressTrack;
import gov.ca.cwds.generic.jobs.component.NeutronDateTimeFormat;
import gov.ca.cwds.generic.jobs.component.NeutronIntegerDefaults;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.generic.jobs.util.JobLogs;
import gov.ca.cwds.generic.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.generic.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.generic.jobs.util.transform.ElasticTransformer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.update.UpdateRequest;
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
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    extends LastSuccessfulRunJob implements AutoCloseable, AtomPersonDocPrep<T>,
    AtomHibernate<T, M>, AtomTransformer<T, M>, AtomInitialLoad<T>, AtomSecurity {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      BasePersonIndexerJob.class);

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

  private final JobBulkProcessorBuilder jobBulkProcessorBuilder;

  /**
   * Main DAO for the supported persistence class.
   */
  protected transient BaseDaoImpl<T> jobDao;

  /**
   * Elasticsearch client DAO.
   */
  protected transient ElasticsearchDao esDao;

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
   * <strong>NOTE</strong>: some jobs normalize on their own, since the normalize/transform step is
   * inexpensive.
   * </p>
   */
  protected LinkedBlockingDeque<M> queueNormalize = new LinkedBlockingDeque<>(100000);

  /**
   * Queue of normalized records waiting to publish to Elasticsearch.
   */
  protected LinkedBlockingDeque<T> queueIndex = new LinkedBlockingDeque<>(250000);

  /**
   * Read/write lock for extract threads and sources, such as JDBC, Hibernate, or even flat files.
   */
  protected transient ReadWriteLock lock;

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
    this.jobBulkProcessorBuilder = new JobBulkProcessorBuilder(esDao, track);
    this.lock = new ReentrantReadWriteLock(false);
  }

  /**
   * Build a delete request to remove the document from the index.
   * 
   * @param id primary key
   * @return bulk delete request
   */
  public DeleteRequest bulkDelete(final String id) {
    return new DeleteRequest(getOpts().getIndexName(), esDao.getConfig().getElasticsearchDocType(),
        id);
  }

  /**
   * Adds an object to the index queue and trap InterruptedException. Suitable for streams and
   * lambda.
   * 
   * @param norm normalized object to add to index queue
   */
  protected void addToIndexQueue(T norm) {
    try {
      JobLogs.logEvery(track.trackQueuedToIndex(), "add to index queue", "recs");
      queueIndex.putLast(norm);
    } catch (InterruptedException e) {
      markFailed();
      Thread.currentThread().interrupt();
      JobLogs.raiseError(LOGGER, e, "INTERRUPTED! {}", e.getMessage());
    }
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * @return an ES bulk processor
   */
  public BulkProcessor buildBulkProcessor() {
    return this.jobBulkProcessorBuilder.buildBulkProcessor();
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
   * @throws IOException if unable to prepare request
   * @see #prepareUpsertRequest(ElasticSearchPerson, PersistentObject)
   */
  protected void prepareDocument(final BulkProcessor bp, T t) throws IOException {
    Arrays.stream(ElasticTransformer.buildElasticSearchPersons(t))
        .map(p -> prepareUpsertRequestNoChecked(p, t)).forEach(x -> { // NOSONAR
          ElasticTransformer.pushToBulkProcessor(track, bp, x);
        });
  }

  /**
   * Prepare an "upsert" request without a checked exception. Throws runtime {@link JobsException}
   * on error. This method's signature is easier to use in functional lambda and stream calls than
   * method signatures with checked exceptions.
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
        ret = bulkDelete((String) t.getPrimaryKey()); // NOTE: cannot assume String PK.
        getTrack().trackBulkDeleted();
      } else {
        ret = prepareUpsertRequest(esp, t);
        getTrack().trackBulkPrepared();
      }
    } catch (Exception e) {
      throw JobLogs.buildException(LOGGER, e, "ERROR BUILDING UPSERT!: PK: {}", t.getPrimaryKey()); // NOSONAR
    }

    return ret;
  }

  /**
   * Prepare sections of a document for update.
   * 
   * @param esp ES document, already prepared by
   *        {@link ElasticTransformer#buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @return left = insert JSON, right = update JSON throws JsonProcessingException on JSON parse
   *         error
   * @throws IOException on Elasticsearch disconnect
   * @see ElasticTransformer#prepareUpsertRequest(AtomPersonDocPrep, String, String,
   *      ElasticSearchPerson, PersistentObject)
   */
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, T t) throws IOException {
    if (StringUtils.isNotBlank(getLegacySourceTable())) {
      esp.setLegacySourceTable(getLegacySourceTable());
    }

    return ElasticTransformer.<T>prepareUpsertRequest(this, getOpts().getIndexName(),
        esDao.getConfig().getElasticsearchDocType(), esp, t);
  }

  /**
   * ENTRY POINT FOR INITIAL LOAD.
   * 
   * <p>
   * Run threads to extract, transform, and index.
   * </p>
   */
  protected void doInitialLoadJdbc() {
    Thread.currentThread().setName("main");

    try {
      final Thread threadIndexer = new Thread(this::threadIndex); // Index
      threadIndexer.start();

      Thread threadTransformer = null;
      if (useTransformThread()) {
        threadTransformer = new Thread(this::threadNormalize); // Transform
        threadTransformer.start();
      }

      final Thread threadJdbc = new Thread(this::threadRetrieveByJdbc); // Extract
      threadJdbc.start();

      threadJdbc.join();
      if (threadTransformer != null) {
        threadTransformer.join();
      }

      threadIndexer.join();
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue());

      // SLF4J does not yet support conditional invocation.
      LOGGER.info("PROGRESS TRACK: {}", this.getTrack().toString()); // NOSONAR
    } catch (Exception e) {
      markFailed();
      Thread.currentThread().interrupt();
      JobLogs.raiseError(LOGGER, e, "GENERAL EXCEPTION: {}", e);
    } finally {
      markJobDone();
      this.finish(); // OK for initial load.
    }

    LOGGER.info("DONE: JDBC initial load");
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers.
   */
  protected void threadRetrieveByJdbc() {
    Thread.currentThread().setName("jdbc");
    LOGGER.info("BEGIN: jdbc thread");

    try (final Connection con = jobDao.getSessionFactory().getSessionFactoryOptions()
        .getServiceRegistry().getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      // con.setReadOnly(true); // WARNING: fails with Postgres.

      // Linux MQT lacks ORDER BY clause. Must sort manually.
      // Either detect platform or force ORDER BY clause.
      final String query = getInitialLoadQuery(getDBSchemaName());

      // Enable parallelism for underlying database.
      JobDB2Utils.enableParallelism(con);

      M m;
      try (final Statement stmt = con.createStatement()) {
        stmt.setFetchSize(15000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(100000);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        int cntr = 0;
        while (isRunning() && rs.next() && (m = extract(rs)) != null) {
          JobLogs.logEvery(++cntr, "Retrieved", "recs");
          queueNormalize.putLast(m);
        }

        con.commit();
      }
    } catch (Exception e) {
      markFailed();
      throw JobLogs.buildException(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      markRetrieveDone();
    }

    LOGGER.info("DONE: jdbc thread");
  }

  protected int normalizeLoop(final List<M> grpRecs, Object theLastId, int inCntr)
      throws InterruptedException {
    M m;
    T t;
    Object lastId = theLastId;
    int cntr = inCntr;
    ++cntr;

    while ((m = queueNormalize.pollFirst(NeutronIntegerDefaults.POLL_MILLIS.getValue(),
        TimeUnit.MILLISECONDS)) != null) {
      JobLogs.logEvery(++cntr, "Transformed", "recs");

      // NOTE: Assumes that records are sorted by group key.
      // End of group. Normalize these group recs.
      if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1
          && (t = normalizeSingle(grpRecs)) != null) {
        LOGGER.trace("queueIndex.putLast: id: {}", t.getPrimaryKey());
        queueIndex.putLast(t);
        grpRecs.clear(); // Single thread, re-use memory.
      }

      grpRecs.add(m);
      lastId = m.getNormalizationGroupKey();
    }

    // Last bundle.
    if (!grpRecs.isEmpty() && (t = normalizeSingle(grpRecs)) != null) {
      queueIndex.putLast(t);
      grpRecs.clear(); // Single thread, re-use memory.
    }

    return cntr;
  }

  /**
   * The "transform" part of ETL. Single-thread consumer, second stage of initial load. Convert
   * de-normalized view records to normalized ones and pass to the index queue.
   */
  protected void threadNormalize() {
    Thread.currentThread().setName("normalize");
    LOGGER.info("BEGIN: normalize thread");

    int cntr = 0;
    Object lastId = new Object();
    final List<M> grpRecs = new ArrayList<>();

    try {
      while (isRunning() && !(isRetrieveDone() && queueNormalize.isEmpty())) {
        cntr = normalizeLoop(grpRecs, lastId, cntr);
      }
    } catch (Exception e) {
      markFailed();
      Thread.currentThread().interrupt();
      JobLogs.raiseError(LOGGER, e, "Transformer: FATAL ERROR: {}", e.getMessage());
    } finally {
      markTransformDone();
    }

    LOGGER.info("DONE: normalize thread");
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

    while ((t = queueIndex.pollFirst(NeutronIntegerDefaults.POLL_MILLIS.getValue(),
        TimeUnit.MILLISECONDS)) != null) {
      JobLogs.logEvery(++i, "Indexed", "recs to ES");
      prepareDocument(bp, t);
    }
    return i;
  }

  /**
   * The "load" part of ETL. Read from normalized record queue and push to ES.
   */
  protected void threadIndex() {
    LOGGER.info("BEGIN: indexer thread");
    Thread.currentThread().setName("es_indexer");
    final BulkProcessor bp = buildBulkProcessor();
    int cntr = 0;

    try {
      while (!(isFailed() || (isRetrieveDone() && isTransformDone() && queueIndex.isEmpty()))) {
        cntr = bulkPrepare(bp, cntr);
      }

      // Just to be sure ...
      cntr = bulkPrepare(bp, cntr);
      LOGGER.info("Flush ES bulk processor ... recs processed: {}", cntr);
      bp.flush();
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue());
      bp.flush();

      LOGGER.info("Waiting to close ES bulk processor ...");
      bp.awaitClose(NeutronIntegerDefaults.DEFAULT_BATCH_WAIT.getValue(), TimeUnit.SECONDS);
      LOGGER.info("Closed ES bulk processor");
    } catch (Exception e) {
      markFailed();
      Thread.currentThread().interrupt();
      JobLogs.raiseError(LOGGER, e, "Indexer: fatal error {}", e.getMessage());
    } finally {
      markJobDone();
    }

    LOGGER.info("DONE: indexer thread");
  }

  /**
   * Prepare a document and trap IOException.
   * 
   * @param bp bulk processor
   * @param p ApiPersonAware object
   */
  protected void prepareDocumentTrapIO(BulkProcessor bp, T p) {
    try {
      prepareDocument(bp, p);
    } catch (IOException e) {
      markFailed();
      JobLogs.raiseError(LOGGER, e, "IO EXCEPTION: {}", e.getMessage());
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
   * @see LastSuccessfulRunJob#_run(Date)
   */
  protected Date doLastRun(Date lastRunDt) {
    LOGGER.info("LAST RUN MODE!");
    try {
      final BulkProcessor bp = buildBulkProcessor();
      final Set<String> deletionResults = new HashSet<>();
      final List<T> results =
          this.isViewNormalizer() ? extractLastRunRecsFromView(lastRunDt, deletionResults)
              : extractLastRunRecsFromTable(lastRunDt);

      if (results != null && !results.isEmpty()) {
        LOGGER.info("Found {} people to index", results.size());
        results.stream().forEach(p -> { // NOSONAR
          prepareDocumentTrapIO(bp, p);
        });
      }

      // Delete records identified for deletion...
      if (!deletionResults.isEmpty()) {
        LOGGER.warn("Found {} people to delete, IDs: {}", deletionResults.size(), deletionResults);

        for (String deletionId : deletionResults) {
          bp.add(new DeleteRequest(getOpts().getIndexName(),
              esDao.getConfig().getElasticsearchDocType(), deletionId));
        }

        track.getRecsBulkDeleted().getAndAdd(deletionResults.size());
      }

      awaitBulkProcessorClose(bp);
      return new Date(this.startTime);
    } catch (Exception e) {
      markFailed();
      throw JobLogs.buildException(LOGGER, e, "General Exception: {}", e.getMessage());
    } finally {
      markJobDone();
    }
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
   * @see LastSuccessfulRunJob#_run(Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      // If index name is provided then use it, otherwise use alias from ES config.
      final String indexNameOverride = getOpts().getIndexName();
      final String effectiveIndexName = StringUtils.isBlank(indexNameOverride)
          ? esDao.getConfig().getElasticsearchAlias() : indexNameOverride;
      getOpts().setIndexName(effectiveIndexName); // WARNING: probably a bad idea.

      final Date lastRun = calcLastRunDate(lastSuccessfulRunTime);
      LOGGER.info("Last successsful run time: {}", lastRun.toString()); // NOSONAR

      // If the index is missing, create it.
      LOGGER.debug("Create index if missing, effectiveIndexName: {}", effectiveIndexName);
      final String documentType = esDao.getConfig().getElasticsearchDocType();
      esDao.createIndexIfNeeded(effectiveIndexName, documentType, ES_PEOPLE_INDEX_SETTINGS,
          ES_PERSON_MAPPING);

      // Smart/auto mode:
      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -25);
      final boolean autoMode = this.opts.isLastRunMode() && lastRun.before(cal.getTime());

      if (autoMode) {
        LOGGER.warn("AUTO MODE!");
        // WARNING: don't overwrite command line settings.
        getOpts().setStartBucket(1);
        getOpts().setEndBucket(1);
        getOpts().setTotalBuckets(getJobTotalBuckets());

        if (this.getDenormalizedClass() != null) {
          LOGGER.info("LOAD FROM VIEW WITH JDBC!");
          doInitialLoadJdbc();
        } else {
          LOGGER.info("LOAD REPLICATED QUERY WITH HIBERNATE!");
          extractHibernate();
        }
      } else if (this.opts == null || this.opts.isLastRunMode()) {
        doLastRun(lastRun);
      } else {
        LOGGER.info("DIRECT BUCKET MODE!");
        if (providesInitialKeyRanges()) {
          doInitialLoadJdbc();
        } else {
          extractHibernate();
        }
      }

      // SLF4J does not yet support conditional invocation.
      LOGGER.info(track.toString()); // NOSONAR
      LOGGER.info("Updating last successful run time to {}",
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
              .format(startTime)); // NOSONAR
      return new Date(this.startTime);
    } catch (Exception e) {
      markFailed();
      throw JobLogs.buildException(LOGGER, e, "GENERAL EXCEPTION: {}", e.getMessage());
    } finally {
      try {
        markJobDone();
        this.close();
      } catch (IOException io) {
        LOGGER.error("IOException on close! {}", io.getMessage(), io);
      }
    }
  }

  /**
   * Pull records changed since the last successful run.
   * 
   * <p>
   * If this job defines a de-normalized view entity, then pull from that. Otherwise, pull from the
   * table entity.
   * </p>
   * 
   * @param lastRunTime last successful run date/time
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromTable(final Date lastRunTime) {
    LOGGER.info("last successful run: {}", lastRunTime);
    final Class<?> entityClass = jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    final Session session = jobDao.getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();

    try {
      final NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setParameter(SQL_COLUMN_AFTER, JobJdbcUtils.makeSimpleTimestampString(lastRunTime),
          StringType.INSTANCE);

      final ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<T> recs = q.list();

      LOGGER.info("FOUND {} RECORDS", recs.size());
      results.addAll(recs);
      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      markFailed();
      LOGGER.error("EXTRACT ERROR! {}", h.getMessage(), h);
      txn.rollback();
      throw new DaoException(h);
    } finally {
      markRetrieveDone();
    }
  }

  protected void loadRecsForDeletion(final Class<?> entityClass, final Session session,
      final Date lastRunTime, Set<String> deletionResults) {
    final String namedQueryNameForDeletion =
        entityClass.getName() + ".findAllUpdatedAfterWithLimitedAccess";
    final NativeQuery<M> q = session.getNamedNativeQuery(namedQueryNameForDeletion);
    q.setParameter(SQL_COLUMN_AFTER, JobJdbcUtils.makeSimpleTimestampString(lastRunTime),
        StringType.INSTANCE);

    final List<M> deletionRecs = q.list();

    if (deletionRecs != null && !deletionRecs.isEmpty()) {
      for (M rec : deletionRecs) {
        // Assuming group key represents ID of client to delete. This is true for client,
        // referral history, case history jobs.
        Object groupKey = rec.getNormalizationGroupKey();
        if (groupKey != null) {
          deletionResults.add(groupKey.toString());
        }
      }
    }

    LOGGER.warn("FOUND {} RECORDS FOR DELETION", deletionResults.size());
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
      final NativeQuery<M> q = session.getNamedNativeQuery(namedQueryName);
      q.setParameter(SQL_COLUMN_AFTER, JobJdbcUtils.makeSimpleTimestampString(lastRunTime),
          StringType.INSTANCE);

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
          // Could be a data error (invalid data in db).
          LOGGER.warn("NULL Normalization Group Key: {}", m);
          lastId = new Object();
        }
      }

      if (!groupRecs.isEmpty()) {
        results.add(normalizeSingle(groupRecs));
      }

      if (mustDeleteLimitedAccessRecords()) {
        loadRecsForDeletion(entityClass, session, lastRunTime, deletionResults);
      }

      session.clear();
      txn.commit();
      return results.build();
    } catch (SQLException h) {
      markFailed();
      txn.rollback();
      throw JobLogs.buildException(LOGGER, h, "EXTRACT SQL ERROR!: {}", h.getMessage());
    } catch (HibernateException h) {
      markFailed();
      txn.rollback();
      throw JobLogs.buildException(LOGGER, h, "EXTRACT ERROR!: {}", h.getMessage());
    } finally {
      markRetrieveDone();
    }
  }

  @Override
  public synchronized void close() throws IOException {
    if (isRunning()) {
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

  @Override
  protected synchronized void finish() {
    LOGGER.warn("FINISH JOB AND SHUTDOWN!");
    try {
      markJobDone();
      close();
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue()); // NOSONAR
    } catch (Exception e) {
      markFailed();
      Thread.currentThread().interrupt();
      throw JobLogs.buildException(LOGGER, e, "ERROR FINISHING JOB: {}", e.getMessage());
    }
  }

  /**
   * Build runtime key pairs for initial load.
   * 
   * @param table the driver table
   * @return batch buckets
   * @deprecated use {@link #threadRetrieveByJdbc()} or {@link #extractHibernate()} instead
   */
  @Deprecated
  @SuppressWarnings("unchecked")
  protected List<BatchBucket> buildBucketList(final String table) {
    List<BatchBucket> ret;
    Transaction txn = null;

    try {
      LOGGER.info("FETCH DYNAMIC BUCKETS FOR {}", table);
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
      markFailed();
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(e);
    } finally {
      markIndexDone();
    }

    return ret;
  }

  /**
   * Return partition keys for initial load. Supports native named query, "findPartitionedBuckets".
   * 
   * <p>
   * Prefer methods {@link #threadRetrieveByJdbc()} or {@link #extractHibernate()} over this one.
   * </p>
   * 
   * @return list of partition key pairs
   */
  protected List<Pair<String, String>> getPartitionRanges() {
    final List<Pair<String, String>> ret = new ArrayList<>();
    final List<BatchBucket> buckets = buildBucketList(getDriverTable());

    for (BatchBucket b : buckets) {
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

    return ret;
  }

  /**
   * Divide work into buckets: pull a unique range of identifiers so that no bucket results overlap.
   * <p>
   * Where possible, prefer use {@link #threadRetrieveByJdbc()} or {@link #extractHibernate()}
   * instead.
   * </p>
   * 
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @return collection of entity results
   */
  @SuppressWarnings("unchecked")
  protected List<T> pullBucketRange(String minId, String maxId) {
    LOGGER.info("PULL BUCKET RANGE {} to {}", minId, maxId);
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : getJobDao().getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findBucketRange";
    final Session session = jobDao.getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();
    try {
      final NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setParameter("min_id", minId, StringType.INSTANCE)
          .setParameter("max_id", maxId, StringType.INSTANCE).setCacheable(false)
          .setFlushMode(FlushMode.MANUAL).setCacheMode(CacheMode.IGNORE)
          .setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());

      // No reduction/normalization. Iterate, process, flush.
      final ScrollableResults results = q.scroll(ScrollMode.FORWARD_ONLY);
      final ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        final Object[] row = results.get();
        for (Object obj : row) {
          ret.add((T) obj);
        }

        if (((++cnt) % NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue()) == 0) {
          LOGGER.info("recs read: {}", cnt);
          session.flush();
        }
      }

      session.flush();
      results.close();
      txn.commit();
      return ret.build();
    } catch (HibernateException e) {
      markFailed();
      LOGGER.error("ERROR PULLING BUCKET RANGE! {}-{}: {}", minId, maxId, e.getMessage(), e);
      txn.rollback();
      throw new DaoException(e);
    }
  }

  protected void awaitBulkProcessorClose(final BulkProcessor bp) {
    try {
      bp.awaitClose(NeutronIntegerDefaults.DEFAULT_BATCH_WAIT.getValue(), TimeUnit.SECONDS);
    } catch (Exception e2) {
      markFailed();
      throw new JobsException("ERROR AWAITING BULK PROCESSOR CLOSE!", e2);
    } finally {
      markRetrieveDone();
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
      final List<T> results = pullBucketRange(b.getLeft(), b.getRight());

      if (results != null && !results.isEmpty()) {
        final BulkProcessor bp = buildBulkProcessor();
        results.stream().forEach(p -> { // NOSONAR
          prepareDocumentTrapIO(bp, p);
        });

        awaitBulkProcessorClose(bp);
      }
    }

    return getTrack().getRecsBulkPrepared().get();
  }

  @Override
  public BaseDaoImpl<T> getJobDao() {
    return jobDao;
  }

  @Override
  public JobProgressTrack getTrack() {
    return track;
  }

  @Override
  public ElasticsearchDao getEsDao() {
    return esDao;
  }

}
