package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.cms.ClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.Client;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.es.Person;

/**
 * Base person batch job to load clients from CMS into ElasticSearch.
 * 
 * <p>
 * This class implements {@link AutoCloseable} and automatically closes common resources, such as
 * {@link ElasticsearchDao} and Hibernate {@link SessionFactory}.
 * </p>
 * 
 * @author CWDS API Team
 * @param <T> Person persistence type
 */
public abstract class BasePersonIndexerJob<T extends PersistentObject>
    extends JobBasedOnLastSuccessfulRunTime implements AutoCloseable {

  private static final Logger LOGGER = LogManager.getLogger(BasePersonIndexerJob.class);

  private static final String INDEX_PERSON = ElasticsearchDao.DEFAULT_PERSON_IDX_NM;
  private static final String DOCUMENT_TYPE_PERSON = ElasticsearchDao.DEFAULT_PERSON_DOC_TYPE;
  private static final int DEFAULT_BATCH_WAIT = 45;

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
   * Return a list of partition keys to optimize batch SELECT statements. See Client native named
   * query "findPartitionedBuckets".
   * 
   * @return list of partition key pairs
   * @see Client
   */
  protected List<Pair<String, String>> getPartitionRanges() {
    return new ArrayList<>(0);
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor for this batch run.
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
        LOGGER.info("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        LOGGER.error("Error executing bulk", failure);
      }
    }).setBulkActions(1000).build();
  }

  /**
   * Build the Guice Injector once, which is used for all Job instances during the life of this
   * batch JVM.
   * 
   * @param opts command line options
   * @param args command line options
   * @return Guice Injector
   * @throws JobsException if unable to construct dependencies
   */
  protected static synchronized Injector buildInjector(final JobOptions opts, final String... args)
      throws JobsException {
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
      final T ret = buildInjector(opts, args).getInstance(klass);
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

    // Close resources automatically.
    try (final T job = newJob(klass, args)) {
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
      final List<T> results = jobDao.findAllUpdatedAfter(lastSuccessfulRunTime);

      if (results != null && !results.isEmpty()) {
        LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));

        // Spawn a reasonable number of threads to process all results.
        results.parallelStream().forEach(p -> {
          try {
            ApiPersonAware pers = (ApiPersonAware) p;
            final Person esp = new Person(p.getPrimaryKey().toString(), pers.getFirstName(),
                pers.getLastName(), pers.getGender(), DomainChef.cookDate(pers.getBirthDate()),
                pers.getSsn(), pers.getClass().getName(), mapper.writeValueAsString(p));

            // Bulk indexing! MUCH faster than indexing one doc at a time.
            bp.add(esDao.bulkAdd(mapper, esp.getId(), esp));
          } catch (JsonProcessingException e) {
            throw new JobsException("JSON error", e);
          }
        });

        // Track counts.
        recsProcessed.getAndAdd(results.size());
      }

      // Give it time to finish the last batch.
      LOGGER.info("Waiting on ElasticSearch to finish last batch");
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
   * Process a single bucket in a batch of buckets. This method runs on thread, and therefore, all
   * shared resources (DAO's, mappers, etc.) must be thread-safe or else you must construct or clone
   * instances as needed.
   * 
   * <p>
   * Note that both BulkProcessor are ElasticsearchDao are thread-safe.
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
    final List<T> results = StringUtils.isBlank(maxId) ? jobDao.bucketList(bucket, totalBuckets)
        : jobDao.partitionedBucketList(bucket, totalBuckets, minId, maxId);

    if (results != null && !results.isEmpty()) {
      LOGGER.warn("bucket #{} found {} people to index", bucket, results.size());

      // One bulk processor per bucket/thread.
      final BulkProcessor bp = buildBulkProcessor();

      // One thread per bucket up to max cores.
      // This bucket runs on one thread only. No parallel stream.
      results.stream().forEach(p -> {
        try {
          ApiPersonAware pers = (ApiPersonAware) p;
          final Person esp = new Person(p.getPrimaryKey().toString(), pers.getFirstName(),
              pers.getLastName(), pers.getGender(), DomainChef.cookDate(pers.getBirthDate()),
              pers.getSsn(), pers.getClass().getName(), mapper.writeValueAsString(p));

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

      // Track counts.
      recsProcessed.getAndAdd(results.size());
    }

    return recsProcessed.get();
  }

  /**
   * Lambda runs a number of threads up to max processor cores. Queued jobs wait until a worker
   * thread is available.
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
      // If the last run is more than 50 years old, the assume that this job populating
      // ElasticSearch for the first time.

      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -50);
      final boolean autoMode = this.opts.lastRunMode && lastSuccessfulRunTime.before(cal.getTime());

      if (autoMode) {
        LOGGER.warn("AUTO MODE!");

        if (!this.getPartitionRanges().isEmpty()) {
          getOpts().setStartBucket(1);
          getOpts().setStartBucket(8);
          getOpts().setTotalBuckets(8);

          for (Pair<String, String> pair : this.getPartitionRanges()) {
            getOpts().setMinId(pair.getLeft());
            getOpts().setMaxId(pair.getRight());

            LOGGER.warn("Process partition range {} to {}", getOpts().getMinId(),
                getOpts().getMaxId());
            LongStream.rangeClosed(1L, 8L).sorted().parallel().forEach(this::processBucket);
          }
        }

        ret = startTime;
      } else if (this.opts == null || this.opts.lastRunMode) {
        LOGGER.warn("LAST RUN MODE!");
        ret = processLastRun(lastSuccessfulRunTime);
      } else {
        LOGGER.warn("BUCKET MODE!");
        LongStream.rangeClosed(this.opts.getStartBucket(), this.opts.getEndBucket()).sorted()
            .parallel().forEach(this::processBucket);
        ret = startTime;
      }

      // Result stats:
      LOGGER.info(MessageFormat.format("Indexed {0} people", recsProcessed));
      LOGGER.info(MessageFormat.format("Updating last successful run time to {0}",
          jobDateFormat.format(startTime)));
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

}

