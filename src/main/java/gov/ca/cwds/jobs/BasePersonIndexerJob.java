package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.IPersonAware;
import gov.ca.cwds.data.cms.ClientDao;
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
 * {@link ElasticsearchDao}.
 * </p>
 * 
 * @author CWDS API Team
 * @param <T> Person persistence type
 */
public abstract class BasePersonIndexerJob<T extends PersistentObject>
    extends JobBasedOnLastSuccessfulRunTime implements AutoCloseable {

  private static final Logger LOGGER = LogManager.getLogger(BasePersonIndexerJob.class);

  protected final ObjectMapper mapper;
  protected final BaseDaoImpl<T> jobDao;
  protected final ElasticsearchDao elasticsearchDao;
  protected final SessionFactory sessionFactory;

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
    this.elasticsearchDao = elasticsearchDao;
    this.mapper = mapper;
    this.sessionFactory = sessionFactory;
  }

  protected static final class JobOptions {
    public String esConfigLoc;
    public String lastRunLoc;
    public boolean lastRunMode = true;
  }

  protected static Option makeOpt(String single, String longName, String description) {
    return Option.builder(single).argName(longName).longOpt(longName).desc(description)
        .numberOfArgs(0).build();
  }

  protected static Option makeOpt(String single, String longName, String description,
      boolean required, int argc, Class<?> type) {
    return Option.builder(single).argName(longName).required(required).longOpt(longName)
        .desc(description).numberOfArgs(argc).type(type).build();
  }

  protected static Options buildCmdLineOptions() {
    Options ret = new Options();

    ret.addOption(
        makeOpt("c", "config", "ElasticSearch configuration file", true, 1, String.class));

    // MODE
    OptionGroup group = new OptionGroup();
    group.setRequired(true);
    group.addOption(makeOpt("l", "last-run-file", "last run date file (yyyy-MM-dd HH:mm:ss)", false,
        1, String.class));
    group.addOption(makeOpt("X", "initial-load", "initial load"));
    ret.addOptionGroup(group);

    return ret;
  }

  /**
   * Print usage and exit.
   */
  protected static void printUsage() {
    new HelpFormatter().printHelp("Client batch loader", buildCmdLineOptions());
    System.exit(-1);
  }

  public static JobOptions parseCommandLine(String[] args) {
    JobOptions ret = new JobOptions();
    try {
      Options options = buildCmdLineOptions();
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      for (Option opt : cmd.getOptions()) {
        switch (opt.getArgName()) {
          case "config":
            LOGGER.info("ES config file  = " + opt.getValue());
            ret.esConfigLoc = opt.getValue().trim();
            break;

          case "last-run-file":
            ret.lastRunMode = true;
            ret.lastRunLoc = opt.getValue().trim();
            LOGGER.info("last run file = " + ret.lastRunLoc);
            break;

          case "initial-load":
            LOGGER.info("INITIAL LOAD!");
            ret.lastRunMode = false;
            break;

          default:
            break;
        }
      }
    } catch (ParseException e) {
      printUsage();
    }

    return ret;
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   */
  public static <T extends BasePersonIndexerJob<?>> T newJob(final Class<T> klass, String... args) {
    final JobOptions opts = parseCommandLine(args);
    final Injector injector =
        Guice.createInjector(new JobsGuiceInjector(new File(opts.esConfigLoc), opts.lastRunLoc));
    return injector.getInstance(klass);
  }

  /**
   * Batch job entry point.
   * 
   * @param klass batch job class
   * @param args command line arguments
   */
  public static <T extends BasePersonIndexerJob<?>> void runJob(final Class<T> klass,
      String... args) {
    // Let session factory and ElasticSearch dao close themselves automatically.
    try (final T job = newJob(klass, args)) {
      job.run();
    } catch (JobsException e) {
      LOGGER.error("Unable to complete job: {}", e.getMessage(), e);
    } catch (IOException e) {
      LOGGER.error("Unable to close resource: {}", e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      final List<T> results = jobDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      final Date startTime = new Date();
      elasticsearchDao.start();

      final BulkProcessor bulkProcessor =
          BulkProcessor.builder(elasticsearchDao.getClient(), new BulkProcessor.Listener() {
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
              LOGGER.warn("Error executing bulk", failure);
            }
          }).setBulkActions(1000).build();

      for (T person : results) {
        final String json = mapper.writeValueAsString(person);
        IPersonAware pers = (IPersonAware) person;
        final Person esPerson = new Person(person.getPrimaryKey().toString(), pers.getFirstName(),
            pers.getLastName(), pers.getGender(), DomainChef.cookDate(pers.getBirthDate()),
            pers.getSsn(), pers.getClass().getName(), json);

        // Index one document at time. Slow.
        // indexDocument(esPerson);

        // Bulk indexing! Much faster.
        bulkProcessor.add(elasticsearchDao.prepareIndexRequest(mapper.writeValueAsString(esPerson),
            esPerson.getId().toString()));
      }

      // Give it time to finish the last batch.
      bulkProcessor.awaitClose(20, TimeUnit.SECONDS);
      LOGGER.info(MessageFormat.format("Indexed {0} people", results.size()));
      LOGGER.info(MessageFormat.format("Updating last succesful run time to {0}",
          jobDateFormat.format(startTime)));
      return startTime;

    } catch (IOException e) {
      LOGGER.error("IOException: {}", e.getMessage(), e);
      throw new JobsException("IOException: " + e.getMessage(), e);
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
    final String document = mapper.writeValueAsString(person);
    elasticsearchDao.index(document, person.getPrimaryKey().toString());
  }

  @Override
  public void close() throws IOException {
    this.elasticsearchDao.close();
    this.sessionFactory.close();
  }

}

