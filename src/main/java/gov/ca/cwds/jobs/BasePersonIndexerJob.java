package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.cms.ClientDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeCache;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.data.std.ApiReduce;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.SystemCodeCache;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.es.AutoCompletePerson;
import gov.ca.cwds.rest.api.domain.es.Person;

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
 * @param <T> storable Person persistence type
 * @see JobOptions
 */
public abstract class BasePersonIndexerJob<T extends PersistentObject>
    extends JobBasedOnLastSuccessfulRunTime implements AutoCloseable {

  private static final Logger LOGGER = LogManager.getLogger(BasePersonIndexerJob.class);

  private static final String INDEX_PERSON = ElasticsearchDao.DEFAULT_PERSON_IDX_NM;
  private static final String DOCUMENT_TYPE_PERSON = ElasticsearchDao.DEFAULT_PERSON_DOC_TYPE;
  private static final int DEFAULT_BATCH_WAIT = 45;
  private static final int DEFAULT_BUCKETS = 4;
  private static final int DEFAULT_THREADS = 4;

  private static final String QUERY_BUCKET_LIST =
      "select z.bucket, min(z.identifier) as minId, max(z.identifier) as maxId, count(*) as bucketCount "
          + "from ( select (y.rn / (total_cnt/THE_TOTAL_BUCKETS)) + 1 as bucket, y.rn, y.identifier from ( "
          + "  select c.identifier, row_number() over (order by 1) as rn, count(*) over (order by 1) as total_cnt "
          + "  from {h-schema}THE_TABLE c order by c.IDENTIFIER ) y order by y.rn "
          + ") z group by z.bucket for read only ";

  private static ApiSystemCodeCache systemCodes;

  /**
   * Guice Injector used for all Job instances during the life of this batch JVM.
   */
  protected static Injector injector;

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
   * Thread-safe count across all worker threads.
   */
  protected AtomicInteger recsProcessed = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param jobDao Person DAO, such as {@link ClientDao}
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public BasePersonIndexerJob(final BaseDaoImpl<T> jobDao, final ElasticsearchDao elasticsearchDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(lastJobRunTimeFilename);
    this.jobDao = jobDao;
    this.esDao = elasticsearchDao;
    this.mapper = mapper;
    this.sessionFactory = sessionFactory;
  }

  /**
   * Get the table or view used to allocate bucket ranges. Called on full load only.
   * 
   * @return the table or view used to allocate bucket ranges
   */
  protected String getBucketDriverTable() {
    String ret = null;
    final Table tbl = this.jobDao.getEntityClass().getDeclaredAnnotation(Table.class);
    if (tbl != null) {
      ret = tbl.name();
    }

    return ret;
  }

  /**
   * Return a list of partition keys to optimize batch SELECT statements. See ReplicatedClient
   * native named query, "findPartitionedBuckets".
   * 
   * @return list of partition key pairs
   * @see ReplicatedClient
   */
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();
    List<BatchBucket> buckets = buildBucketList(getBucketDriverTable());

    for (BatchBucket b : buckets) {
      LOGGER.warn("BUCKET RANGE: {} to {}", b.getMinId(), b.getMaxId());
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

    return ret;
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
        LOGGER.debug("Ready to execute bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        LOGGER.debug("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        LOGGER.error("Error executing bulk", failure);
      }
    }).setBulkActions(1500).build();
  }

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
        injector = Guice
            .createInjector(new JobsGuiceInjector(new File(opts.esConfigLoc), opts.lastRunLoc));
      } catch (CreationException e) {
        LOGGER.error("Unable to create dependencies: {}", e.getMessage(), e);
        throw new JobsException("Unable to create dependencies: " + e.getMessage(), e);
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
  public static <T extends BasePersonIndexerJob<?>> T newJob(final Class<T> klass, String... args)
      throws JobsException {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final T ret = buildInjector(opts).getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      LOGGER.error("Unable to create dependencies: {}", e.getMessage(), e);
      throw new JobsException("Unable to create dependencies: " + e.getMessage(), e);
    }
  }

  /**
   * Batch job entry point.
   * 
   * <p>
   * This method closes Hibernate session factory and ElasticSearch DAO automatically.
   * </p>
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   * @throws JobsException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?>> void runJob(final Class<T> klass,
      String... args) throws JobsException {
    try (final T job = newJob(klass, args)) { // Close resources automatically.
      job.run();
    } catch (IOException e) {
      LOGGER.error("Unable to close resource: {}", e.getMessage(), e);
      throw new JobsException("Unable to close resource: " + e.getMessage(), e);
    } catch (JobsException e) {
      LOGGER.error("Unable to complete job: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Produce an ElasticSearchPerson suitable as an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  protected ElasticSearchPerson buildESPerson(T p) throws JsonProcessingException {
    ApiPersonAware pa = (ApiPersonAware) p;
    List<String> languages = null;
    List<ElasticSearchPerson.ElasticSearchPersonPhone> phones = null;
    List<ElasticSearchPersonAddress> addresses = null;

    if (p instanceof ApiMultipleLanguagesAware) {
      ApiMultipleLanguagesAware mlx = (ApiMultipleLanguagesAware) p;
      languages = new ArrayList<>();
      for (ApiLanguageAware lx : mlx.getLanguages()) {
        final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
            ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
        if (lang != null) {
          languages.add(lang.getDescription());
        }
      }
    } else if (p instanceof ApiLanguageAware) {
      languages = new ArrayList<>();
      ApiLanguageAware lx = (ApiLanguageAware) p;
      final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
          ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
      if (lang != null) {
        languages.add(lang.getDescription());
      }
    }

    if (p instanceof ApiMultiplePhonesAware) {
      phones = new ArrayList<>();
      ApiMultiplePhonesAware mphx = (ApiMultiplePhonesAware) p;
      for (ApiPhoneAware phx : mphx.getPhones()) {
        phones.add(new ElasticSearchPersonPhone(phx));
      }
    } else if (p instanceof ApiPhoneAware) {
      phones = new ArrayList<>();
      ApiPhoneAware phx = (ApiPhoneAware) p;
      phones.add(new ElasticSearchPersonPhone(phx));
    }

    if (p instanceof ApiMultipleAddressesAware) {
      addresses = new ArrayList<>();
      ApiMultipleAddressesAware madrx = (ApiMultipleAddressesAware) p;
      for (ApiAddressAware adrx : madrx.getAddresses()) {
        addresses.add(new AutoCompletePerson.AutoCompletePersonAddress(adrx).toESPersonAddress());
      }
    } else if (p instanceof ApiAddressAware) {
      addresses = new ArrayList<>();
      addresses.add(new AutoCompletePerson.AutoCompletePersonAddress((ApiAddressAware) p)
          .toESPersonAddress());
    }

    // Write persistence object to Elasticsearch Person document.
    return new ElasticSearchPerson(p.getPrimaryKey().toString(), // id
        pa.getFirstName(), // first name
        pa.getLastName(), // last name
        pa.getMiddleName(), // middle name
        pa.getNameSuffix(), // name suffix
        pa.getGender(), // gender
        DomainChef.cookDate(pa.getBirthDate()), // birth date
        pa.getSsn(), // SSN
        pa.getClass().getName(), // type
        this.mapper.writeValueAsString(p), // source
        null, // highlights
        addresses, // address
        phones, languages);
  }

  /**
   * Pull records changed since the last successful run.
   * 
   * <p>
   * If this job defines a denormalized (MQT) entity, then pull from that. Otherwise, pull from the
   * regular entity.
   * </p>
   * 
   * @param jobDao DAO
   * @param lastSuccessfulRunTime last successful run time
   * @return List of normalized entities
   */
  protected List<T> pullLastRunResults(BaseDaoImpl<T> jobDao, Date lastSuccessfulRunTime) {
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = null;
    try {
      txn = session.beginTransaction();
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setTimestamp("after", new Timestamp(lastSuccessfulRunTime.getTime()));

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<T> recs = q.list();

      if (isReducer()) {
        results.addAll(reduce(recs));
      } else {
        results.addAll(recs);
      }

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      LOGGER.error("BATCH ERROR! {}", h.getMessage(), h);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(h);
    }
  }

  /**
   * Fetch all records for the next batch run, either by bucket or last successful run date.
   * 
   * @param lastSuccessfulRunTime last time the batch ran successfully.
   * @return List of results to process
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  protected Date processLastRun(Date lastSuccessfulRunTime) {
    try {
      final Date startTime = new Date();

      // One bulk processor "last run" operations. BulkProcessor is thread-safe.
      final BulkProcessor bp = buildBulkProcessor();
      final List<T> results = pullLastRunResults(jobDao, lastSuccessfulRunTime);

      if (results != null && !results.isEmpty()) {
        LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));

        // Spawn a reasonable number of threads to process all results.
        results.parallelStream().forEach(p -> {
          try {
            // Write persistence object to Elasticsearch Person document.
            final ElasticSearchPerson esp = buildESPerson(p);

            // Bulk indexing! MUCH faster than indexing one doc at a time.
            bp.add(this.esDao.bulkAdd(this.mapper, esp.getId(), esp));
          } catch (JsonProcessingException e) {
            throw new JobsException("JSON error", e);
          }
        });

        // Track counts.
        recsProcessed.getAndAdd(results.size());
      }

      // Give it time to finish the last batch.
      LOGGER.warn("Waiting on ElasticSearch to finish last batch");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);

      return startTime;
    } catch (JobsException e) {
      LOGGER.error("JobsException: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("General Exception: {}", e.getMessage(), e);
      throw new JobsException("General Exception: " + e.getMessage(), e);
    }
  }

  /**
   * Build the bucket list at runtime.
   * 
   * @param table the driver table
   * @return batch buckets
   */
  @SuppressWarnings("unchecked")
  protected List<BatchBucket> buildBucketList(String table) {
    List<BatchBucket> ret = new ArrayList<>();

    Session session = jobDao.getSessionFactory().getCurrentSession();
    Transaction txn = null;
    try {
      LOGGER.info("FETCH DYNAMIC BUCKET LIST FOR TABLE {}", table);
      txn = session.beginTransaction();
      final long totalBuckets =
          opts.getTotalBuckets() != 0L ? opts.getTotalBuckets() : getJobTotalBuckets();
      final javax.persistence.Query q = jobDao.getSessionFactory().createEntityManager()
          .createNativeQuery(QUERY_BUCKET_LIST.replaceAll("THE_TABLE", table)
              .replaceAll("THE_TOTAL_BUCKETS", String.valueOf(totalBuckets)), BatchBucket.class);

      ret = q.getResultList();
      session.clear();
      txn.commit();
    } catch (HibernateException h) {
      LOGGER.error("BATCH ERROR! ", h);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(h);
    }

    return ret;
  }

  /**
   * Working approach tells Hibernate to collect results from native query into parent/child
   * objects.
   * 
   * <p>
   * Down side: complicated syntax. Must tell Hibernate how to join aliased tables.
   * </p>
   * 
   * @param jobDao DAO for T
   * @param rootEntity root parent entity class, typically type T
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @return List of T entity
   * @deprecated use method {@link #processBucketRange(BaseDaoImpl, String, String)}
   */
  @Deprecated
  protected List<T> partitionedBucketEntities(BaseDaoImpl<T> jobDao, Class<?> rootEntity,
      String minId, String maxId) {
    final String namedQueryName = jobDao.getEntityClass().getName() + ".findPartitionedBuckets";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = null;
    try {
      txn = session.beginTransaction();
      final Query q = session.getNamedQuery(namedQueryName);
      SQLQuery query = session.createSQLQuery(q.getQueryString());
      query.setString("min_id", minId).setString("max_id", maxId);

      // NamedQuery comments describe join conditions.
      boolean hibernateJoins =
          StringUtils.isNotBlank(q.getComment()) && !q.getComment().equals(namedQueryName);

      if (hibernateJoins) {
        query.addEntity("a", rootEntity);
        final String[] blocks = q.getComment().trim().split(";");
        for (String block : blocks) {
          final String[] terms = block.trim().split(",");
          query.addJoin(terms[0], terms[1]);
        }
      } else {
        query.addEntity("z", rootEntity);
      }

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<Object[]> raw = query.list();
      List<T> answers = new ArrayList<>(raw.size());

      if (hibernateJoins) {
        for (Object[] arr : raw) {
          answers.add((T) arr[0]);
        }
      } else {
        for (Object obj : raw) {
          answers.add((T) obj);
        }
      }

      results.addAll(answers);
      session.clear();
      txn.commit();
      return results.build();

    } catch (HibernateException h) {
      LOGGER.error("BATCH ERROR! ", h);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(h);
    }
  }

  /**
   * Getter for the job's MQT entity class, if any, or null if none.
   * 
   * @return MQT entity class
   */
  protected Class<? extends ApiReduce<? extends PersistentObject>> getDenormalizedClass() {
    return null;
  }

  /**
   * Default reduce method just returns the input. Child classes may customize this method to reduce
   * denormalized result sets to normalized entities.
   * 
   * @param recs entity records
   * @return unmodified entity records
   */
  @SuppressWarnings("unchecked")
  protected List<T> reduce(List<? extends PersistentObject> recs) {
    return (List<T>) recs;
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
   * True if the Job class intends to reduce denormalized results to normalized ones.
   * 
   * @return true if class overrides {@link #reduce(List)}
   */
  protected boolean isReducer() {
    return false;
  }

  /**
   * Simple and clean approach to divide work into buckets: pull a unique range of identifiers so
   * that no bucket results overlap.
   * 
   * @param jobDao DAO for entity type T
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @return collection of entity results
   */
  protected List<T> processBucketRange(BaseDaoImpl<T> jobDao, String minId, String maxId) {
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findBucketRange";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = null;
    try {
      txn = session.beginTransaction();
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setString("min_id", minId).setString("max_id", maxId);

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<T> recs = q.list();

      if (isReducer()) {
        results.addAll(reduce(recs));
      } else {
        results.addAll(recs);
      }

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      LOGGER.error("BATCH ERROR! {}", h.getMessage(), h);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(h);
    }
  }

  /**
   * Process a single bucket in a batch of buckets. This method runs on thread, and therefore, all
   * shared resources (DAO's, mappers, etc.) must be thread-safe or else you must construct or clone
   * instances as needed.
   * 
   * <p>
   * Thread safety: both BulkProcessor are ElasticsearchDao are thread-safe.
   * </p>
   * 
   * @param bucket the bucket number to process
   * @return number of records processed in this bucket
   */
  protected int processBucket(long bucket) {
    final long totalBuckets = this.opts.getTotalBuckets();
    LOGGER.warn("pull bucket #{} of #{}", bucket, totalBuckets);
    final String minId =
        StringUtils.isBlank(this.getOpts().getMinId()) ? " " : this.getOpts().getMinId();
    final String maxId = this.getOpts().getMaxId();

    // ORIGINAL:
    // final List<T> results = StringUtils.isBlank(maxId) ? jobDao.bucketList(bucket, totalBuckets)
    // : jobDao.partitionedBucketList(bucket, totalBuckets, minId, maxId);

    // Too complex, DB2 optimization challenges.
    // final List<T> results = StringUtils.isBlank(maxId) ? jobDao.bucketList(bucket, totalBuckets)
    // : partitionedBucketEntities(jobDao, jobDao.getEntityClass(), bucket, totalBuckets, minId,
    // maxId);

    // Simple and clean approach: process a unique range of identifiers.
    final List<T> results = processBucketRange(jobDao, minId, maxId);

    if (results != null && !results.isEmpty()) {
      LOGGER.warn("bucket #{} found {} people to index", bucket, results.size());

      // Track counts.
      recsProcessed.getAndAdd(results.size());

      // One bulk processor per bucket/thread.
      final BulkProcessor bp = buildBulkProcessor();

      // One thread per bucket up to max cores.
      // Each bucket runs on one thread only. No parallel streams here.
      results.stream().forEach(p -> {
        try {
          final ElasticSearchPerson esp = buildESPerson(p);

          // Bulk indexing! MUCH faster than indexing one doc at a time.
          bp.add(esDao.bulkAdd(mapper, esp.getId(), esp));
        } catch (JsonProcessingException e) {
          throw new JobsException("JSON error", e);
        }
      });

      try {
        bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      } catch (Exception e2) {
        throw new JobsException("ES bulk processor interrupted!", e2);
      }

    }

    return recsProcessed.get();
  }

  /**
   * Process all buckets using default parallelism without partitions.
   */
  protected void processBuckets() {
    LongStream.rangeClosed(this.opts.getStartBucket(), this.opts.getEndBucket()).sorted().parallel()
        .forEach(this::processBucket);
  }

  /**
   * Process all partitions by buckets using default parallelism.
   */
  protected void processBucketPartitions() {
    LOGGER.warn("PROCESS EACH PARTITION");
    for (Pair<String, String> pair : this.getPartitionRanges()) {
      getOpts().setMinId(pair.getLeft());
      getOpts().setMaxId(pair.getRight());
      LOGGER.warn("PROCESS PARTITION RANGE \"{}\" to \"{}\"", getOpts().getMinId(),
          getOpts().getMaxId());
      processBuckets();
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
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    Date ret;
    try {
      final Date startTime = new Date();

      // If the people index is missing, create it.
      LOGGER.debug("Create people index if missing");
      esDao.createIndexIfNeeded(ElasticsearchDao.DEFAULT_PERSON_IDX_NM);
      LOGGER.debug("availableProcessors={}", Runtime.getRuntime().availableProcessors());

      // Smart/auto mode:
      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -50);
      final boolean autoMode = this.opts.lastRunMode && lastSuccessfulRunTime.before(cal.getTime());

      if (autoMode) {
        LOGGER.warn("AUTO MODE!");
        getOpts().setStartBucket(1);
        getOpts().setEndBucket(DEFAULT_BUCKETS);
        getOpts().setTotalBuckets(DEFAULT_THREADS);

        if (!this.getPartitionRanges().isEmpty()) {
          processBucketPartitions();
        } else {
          getOpts().setMaxId(null);
          getOpts().setMinId(null);
          processBuckets();
        }

        ret = startTime;
      } else if (this.opts == null || this.opts.lastRunMode) {
        LOGGER.warn("LAST RUN MODE!");
        ret = processLastRun(lastSuccessfulRunTime);
      } else {
        LOGGER.warn("DIRECT BUCKET MODE!");
        processBuckets();
        ret = startTime;
      }

      // Result stats:
      LOGGER.info("Indexed {} people", recsProcessed);
      LOGGER.info("Updating last successful run time to {}", jobDateFormat.format(startTime));
      return ret;
    } catch (JobsException e) {
      LOGGER.error("JobsException: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("General Exception: {}", e.getMessage(), e);
      throw new JobsException("General Exception: " + e.getMessage(), e);
    }
  }

  /**
   * Indexes a <strong>SINGLE</strong> document. Prefer batch mode.
   * 
   * <p>
   * <strong>TODO:</strong> Add document mapping after creating index.
   * </p>
   * 
   * @param person {@link Person} document to index
   * @throws JsonProcessingException if JSON cannot be read
   */
  protected void indexDocument(T person) throws JsonProcessingException {
    esDao.index(INDEX_PERSON, DOCUMENT_TYPE_PERSON, mapper.writeValueAsString(person),
        person.getPrimaryKey().toString());
  }

  @Override
  public void close() throws IOException {
    if (this.esDao != null) {
      this.esDao.close();
    }

    if (this.sessionFactory != null) {
      this.sessionFactory.close();
    }
  }

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
   * Getter for CMS system code cache.
   * 
   * @return reference to CMS system code cache
   */
  public static ApiSystemCodeCache getSystemCodes() {
    return systemCodes;
  }

  /**
   * Store a reference to the singleton CMS system code cache for quick convenient access.
   * 
   * @param sysCodeCache CMS system code cache
   */
  @Inject
  public static void setSystemCodes(@SystemCodeCache ApiSystemCodeCache sysCodeCache) {
    systemCodes = sysCodeCache;
  }

}

