package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.IPersonAware;
import gov.ca.cwds.data.cms.ClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
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

  private static final String INDEX_PERSON = "person";
  private static final String DOCUMENT_TYPE_PERSON = "people";

  private static final String CMD_LINE_ES_CONFIG = "config";
  private static final String CMD_LINE_LAST_RUN = "last-run-file";
  private static final String CMD_LINE_BUCKET_RANGE = "bucket-range";
  private static final String CMD_LINE_BUCKET_TOTAL = "total-buckets";
  private static final String CMD_LINE_THREADS = "thread-num";

  /**
   * Definitions of batch job command line options.
   * 
   * @author CWDS API Team
   */
  public enum JobCmdLineOption {

    /**
     * ElasticSearch configuration file.
     */
    ES_CONFIG(JobOptions.makeOpt("c", CMD_LINE_ES_CONFIG, "ElasticSearch configuration file", true, 1, String.class, ',')),

    /**
     * last run date file (yyyy-MM-dd HH:mm:ss)
     */
    LAST_RUN_FILE(JobOptions.makeOpt("l", CMD_LINE_LAST_RUN, "last run date file (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

    /**
     * bucket range (-r 20-24).
     */
    BUCKET_RANGE(JobOptions.makeOpt("r", CMD_LINE_BUCKET_RANGE, "bucket range (-r 20-24)", false, 2, Integer.class, '-')),

    /**
     * total buckets.
     */
    BUCKET_TOTAL(JobOptions.makeOpt("b", CMD_LINE_BUCKET_TOTAL, "total buckets", false, 1, Integer.class, ',')),

    /**
     * Number of threads (optional).
     */
    THREADS(JobOptions.makeOpt("t", CMD_LINE_THREADS, "# of threads", false, 1, Integer.class, ','));

    private final Option opt;

    JobCmdLineOption(Option opt) {
      this.opt = opt;
    }

    /**
     * Getter for the type's command line option definition.
     * 
     * @return command line option definition
     */
    public final Option getOpt() {
      return opt;
    }

  }

  protected final ObjectMapper mapper;
  protected final BaseDaoImpl<T> jobDao;
  protected final ElasticsearchDao esDao;
  protected final SessionFactory sessionFactory;

  protected JobOptions opts;
  protected BulkProcessor bp;
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
   * Instantiate one Elasticsearch BulkProcessor for this batch run.
   * 
   * @return Elasticsearch BulkProcessor
   */
  protected BulkProcessor buildBulkProcessor() {
    return BulkProcessor.builder(esDao.getClient(), new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
        LOGGER.info("Ready to execute bulk of {} actions", request.numberOfActions());
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
   * Represents batch job options from the command line.
   * 
   * @author CWDS API Team
   */
  public static final class JobOptions implements Serializable {

    /**
     * Base serialization version. Increment by class change.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Location of Elasticsearch configuration file.
     */
    private final String esConfigLoc;

    /**
     * Location of last run file.
     */
    private final String lastRunLoc;

    /**
     * Whether to run in periodic "last run" mode or "initial" mode. Defaults to true.
     */
    private final boolean lastRunMode;

    /**
     * When running in "initial load" mode, specifies the starting bucket of records to be processed
     * by this job.
     * <p>
     * Required for "initial load" mode.
     * </p>
     */
    private final long startBucket;

    /**
     * When running in "initial load" mode, specifies the ending bucket of records to be processed
     * by this job.
     * <p>
     * Required for "initial load" mode.
     * </p>
     */
    private final long endBucket;

    /**
     * When running in "initial load" mode, specifies the total number buckets for all related batch
     * runs.
     * <p>
     * Required for "initial load" mode.
     * </p>
     */
    private final long totalBuckets;

    /**
     * Total threads to allocate to this batch run. Defaults to all available cores.
     */
    private final long threadCount;

    private JobOptions(String esConfigLoc, String lastRunLoc, boolean lastRunMode, long startBucket,
        long endBucket, long totalBuckets, long threadCount) {
      this.esConfigLoc = esConfigLoc;
      this.lastRunLoc = lastRunLoc;
      this.lastRunMode = lastRunMode;
      this.startBucket = startBucket;
      this.endBucket = endBucket;
      this.totalBuckets = totalBuckets;
      this.threadCount = threadCount;
    }

    /**
     * Getter for location of Elasticsearch configuration file.
     * 
     * @return location of Elasticsearch configuration file
     */
    public final String getEsConfigLoc() {
      return esConfigLoc;
    }

    /**
     * Getter for location of last run date/time file.
     * 
     * @return location of last run file
     */
    public final String getLastRunLoc() {
      return lastRunLoc;
    }

    /**
     * Getter for last run mode.
     * 
     * @return last run mode
     */
    public final boolean isLastRunMode() {
      return lastRunMode;
    }

    /**
     * Getter for starting bucket.
     * 
     * @return starting bucket
     */
    public final long getStartBucket() {
      return startBucket;
    }

    /**
     * Getter for last bucket.
     * 
     * @return last bucket
     */
    public final long getEndBucket() {
      return endBucket;
    }

    /**
     * Getter for total buckets.
     * 
     * @return total buckets
     */
    public final long getTotalBuckets() {
      return totalBuckets;
    }

    /**
     * Getter for thread count.
     * 
     * @return thread count
     */
    public final long getThreadCount() {
      return threadCount;
    }

    /**
     * Define a command line option.
     * 
     * @param shortOpt single letter option name
     * @param longOpt long option name
     * @param description option description
     * @return command line option
     */
    protected static Option makeOpt(String shortOpt, String longOpt, String description) {
      return Option.builder(shortOpt).argName(longOpt).longOpt(longOpt).desc(description)
          .numberOfArgs(0).build();
    }

    /**
     * Define a command line option.
     * 
     * @param shortOpt single letter option name
     * @param longOpt long option name
     * @param description option description
     * @param required true if required
     * @param argc number of arguments to this option
     * @param type arguments' Java class
     * @param sep argument separator
     * @return command line option
     */
    protected static Option makeOpt(String shortOpt, String longOpt, String description,
        boolean required, int argc, Class<?> type, char sep) {
      return Option.builder(shortOpt).argName(longOpt).required(required).longOpt(longOpt)
          .desc(description).numberOfArgs(argc).type(type).valueSeparator(sep).build();
    }

    /**
     * Define command line options.
     * 
     * @return command line option definitions
     */
    protected static Options buildCmdLineOptions() {
      Options ret = new Options();
      ret.addOption(JobCmdLineOption.ES_CONFIG.getOpt());
      ret.addOption(JobCmdLineOption.THREADS.getOpt());
      ret.addOption(JobCmdLineOption.BUCKET_RANGE.getOpt());

      // RUN MODE: mutually exclusive choice.
      OptionGroup group = new OptionGroup();
      group.setRequired(true);
      group.addOption(JobCmdLineOption.LAST_RUN_FILE.getOpt());
      group.addOption(JobCmdLineOption.BUCKET_TOTAL.getOpt());
      ret.addOptionGroup(group);

      return ret;
    }

    /**
     * Print usage.
     */
    protected static void printUsage() {
      try (final StringWriter sw = new StringWriter()) {
        new HelpFormatter().printHelp(new PrintWriter(sw), 100, "Batch loader",
            StringUtils.leftPad("", 90, '=') + "\nUSAGE: java <job class> ...\n"
                + StringUtils.leftPad("", 90, '='),
            buildCmdLineOptions(), 4, 8, StringUtils.leftPad("", 90, '='), true);
        LOGGER.error(sw.toString());
      } catch (IOException e) {
        throw new JobsException("ERROR PRINTING HELP! How ironic. :-)", e);
      }
    }

    /**
     * Parse the command line return the job settings.
     * 
     * @param args command line to parse
     * @return JobOptions defining this job
     * @throws JobsException if unable to parse command line
     */
    public static JobOptions parseCommandLine(String[] args) throws JobsException {
      String esConfigLoc = null;
      String lastRunLoc = null;
      boolean lastRunMode = false;
      long startBucket = 0L;
      long endBucket = 0L;
      long totalBuckets = 0L;
      long threadCount = 0L;

      try {
        Options options = buildCmdLineOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Java clincher: case statements only take constants. Even compile-time constants, like
        // enum members (evaluated at compile time), are not considered "constants."
        for (Option opt : cmd.getOptions()) {
          switch (opt.getArgName()) {
            case CMD_LINE_ES_CONFIG:
              LOGGER.info("ES config file  = " + opt.getValue());
              esConfigLoc = opt.getValue().trim();
              break;

            case CMD_LINE_LAST_RUN:
              lastRunMode = true;
              lastRunLoc = opt.getValue().trim();
              LOGGER.info("last run file = " + lastRunLoc);
              break;

            case CMD_LINE_BUCKET_TOTAL:
              LOGGER.info("INITIAL LOAD!");
              lastRunMode = false;
              totalBuckets = Long.parseLong(opt.getValue());
              break;

            case CMD_LINE_BUCKET_RANGE:
              lastRunMode = false;
              startBucket = Long.parseLong(opt.getValues()[0]);
              endBucket = Long.parseLong(opt.getValues()[1]);
              break;

            case CMD_LINE_THREADS:
              threadCount = Long.parseLong(opt.getValue());
              break;

            default:
              break;
          }
        }
      } catch (ParseException e) {
        printUsage();
        LOGGER.error("Error parsing command line: {}", e.getMessage(), e);
        throw new JobsException("Error parsing command line: " + e.getMessage(), e);
      }

      return new JobOptions(esConfigLoc, lastRunLoc, lastRunMode, startBucket, endBucket,
          totalBuckets, threadCount);
    }

  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   * @param <T> Person persistence type
   * @throws ParseException if unable to parse command line
   */
  public static <T extends BasePersonIndexerJob<?>> T newJob(final Class<T> klass, String... args)
      throws ParseException {
    final JobOptions opts = JobOptions.parseCommandLine(args);
    final Injector injector =
        Guice.createInjector(new JobsGuiceInjector(new File(opts.esConfigLoc), opts.lastRunLoc));
    final T ret = injector.getInstance(klass);
    ret.setOpts(opts);
    return ret;
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
    } catch (ParseException e) {
      LOGGER.error("Unable to parse command line: {}", e.getMessage(), e);
      throw new JobsException("Unable to parse command line: " + e.getMessage(), e);
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
      this.bp = buildBulkProcessor();

      final List<T> results = jobDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      if (results != null && !results.isEmpty()) {
        LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));

        // BulkProcessor is thread-safe.
        results.parallelStream().forEach((p) -> {
          try {
            IPersonAware pers = (IPersonAware) p;
            final Person esPerson = new Person(p.getPrimaryKey().toString(), pers.getFirstName(),
                pers.getLastName(), pers.getGender(), DomainChef.cookDate(pers.getBirthDate()),
                pers.getSsn(), pers.getClass().getName(), mapper.writeValueAsString(p));

            // Bulk indexing! MUCH faster than indexing one doc at a time.
            bp.add(
                esDao.prepareIndexRequest(mapper.writeValueAsString(esPerson), esPerson.getId()));
          } catch (JsonProcessingException e) {
            throw new JobsException("JSON error", e);
          }
        });

        // Track counts.
        recsProcessed.getAndAdd(results.size());
      }

      // Give it time to finish the last batch.
      LOGGER.info("Waiting on ElasticSearch to finish last batch");
      bp.awaitClose(30, TimeUnit.SECONDS);

      // Result stats:
      LOGGER.info(MessageFormat.format("Indexed {0} people", recsProcessed));
      LOGGER.info(MessageFormat.format("Updating last succesful run time to {0}",
          jobDateFormat.format(startTime)));
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
    final List<T> results = jobDao.bucketList(bucket, totalBuckets);

    if (results != null && !results.isEmpty()) {
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));

      results.stream().forEach((p) -> {
        try {
          IPersonAware pers = (IPersonAware) p;
          final Person esPerson = new Person(p.getPrimaryKey().toString(), pers.getFirstName(),
              pers.getLastName(), pers.getGender(), DomainChef.cookDate(pers.getBirthDate()),
              pers.getSsn(), pers.getClass().getName(), mapper.writeValueAsString(p));

          // Bulk indexing! MUCH faster than indexing one doc at a time.
          bp.add(esDao.prepareIndexRequest(mapper.writeValueAsString(esPerson), esPerson.getId()));
        } catch (JsonProcessingException e) {
          throw new JobsException("JSON error", e);
        }
      });

      // Track counts.
      recsProcessed.getAndAdd(results.size());
    }

    return recsProcessed.get();
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      final Date startTime = new Date();
      this.bp = buildBulkProcessor();

      if (this.opts == null || this.opts.lastRunMode) {
        LOGGER.warn("LAST RUN MODE!");
        return processLastRun(lastSuccessfulRunTime);
      } else {
        LOGGER.warn("BUCKET MODE!");
        LOGGER.warn("availableProcessors={}", Runtime.getRuntime().availableProcessors());

        LongStream.rangeClosed(this.opts.getStartBucket(), this.opts.getEndBucket()).parallel()
            .forEach(b -> this.processBucket(b));

        // Give it time to finish the last batch.
        LOGGER.info("Waiting on ElasticSearch to finish last batch");
        bp.awaitClose(30, TimeUnit.SECONDS);

        // Result stats:
        LOGGER.info(MessageFormat.format("Indexed {0} people", recsProcessed));
        LOGGER.info(MessageFormat.format("Updating last succesful run time to {0}",
            jobDateFormat.format(startTime)));
        return startTime;
      }
    } catch (JobsException e) {
      LOGGER.error("JobsException: {}", e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("General Exception: {}", e.getMessage(), e);
      throw new JobsException("General Exception: " + e.getMessage(), e);
    }
  }

  /**
   * Indexes a single document. Prefer batch mode.
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

