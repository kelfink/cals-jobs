package gov.ca.cwds.jobs.cals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.*;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import gov.ca.cwds.generic.jobs.util.elastic.XPackUtils;
import gov.ca.cwds.rest.api.ApiException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author CWDS TPT-2
 */
public abstract class BaseCalsIndexerJob extends AbstractModule {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaseCalsIndexerJob.class);

  private JobOptions jobOptions;

  @Override
  protected void configure() {
    install(new MappingModule());
    bind(JobOptions.class).toInstance(jobOptions);
  }

  static <T extends BaseCalsIndexerJob> JobOptions buildJobOptions(Class<T> jobRunnerClass,
      String[] args) {
    try {
      if (args.length < 4) {
        throw new JobsException("not enough command line arguments");
      }
      return validateJobOptions(JobOptions.parseCommandLine(args));
    } catch (JobsException e) {
      LOGGER.error(
          "usage: java -D... -cp cals-jobs.jar {} -c path/to/config/file.yaml -l dir/for/time/files/",
          jobRunnerClass.getName());
      throw e;
    }
  }

  private static JobOptions validateJobOptions(JobOptions jobOptions) {
    // check option: -c
    File configFile = new File(jobOptions.getEsConfigLoc());
    if (!configFile.exists()) {
      throw new JobsException(
          "job arguments error: specified config file " + configFile.getPath() + " not found");
    }

    if (!configFile.isFile()) {
      throw new JobsException("job arguments error: specified config file " + configFile.getPath()
          + " is not really a file");
    }

    // check option: -l
    File timeFilesDir = new File(jobOptions.getLastRunLoc());
    if (!timeFilesDir.exists()) {
      throw new JobsException(
          "job arguments error: specified time files directory " + timeFilesDir.getPath()
              + " not found");
    }

    if (!timeFilesDir.isDirectory()) {
      throw new JobsException(
          "job arguments error: specified time files directory " + timeFilesDir.getPath()
              + " is not really a directory");
    }

    return jobOptions;
  }

  private static <T extends BaseCalsIndexerJob> T newJobRunner(Class<T> jobRunnerClass,
      String[] args) {
    try {
      JobOptions jobOptions = buildJobOptions(jobRunnerClass, args);
      T jobRunner = jobRunnerClass.newInstance();
      jobRunner.setJobOptions(jobOptions);
      return jobRunner;
    } catch (CreationException | InstantiationException | IllegalAccessException e) {
      throw new JobsException(e);
    }
  }

  protected static <T extends BaseCalsIndexerJob> void runJob(Class<T> jobRunnerClass,
      String[] args) {
    try {
      final T jobRunner = newJobRunner(jobRunnerClass, args);
      final Injector injector = Guice.createInjector(jobRunner);
      injector.getInstance(Job.class).run();
    } catch (RuntimeException e) {
      LOGGER.error("ERROR: ", e.getMessage(), e);
      System.exit(1);
    }
  }

  void setJobOptions(JobOptions jobOptions) {
    this.jobOptions = jobOptions;
  }

  @Provides
  @Inject
  // the client should not be closed here, it is closed when job is done
  @SuppressWarnings("squid:S2095")
  public Client elasticsearchClient(CalsElasticsearchConfiguration config) {
    TransportClient client = null;
    LOGGER.warn("Create NEW ES client");
    try {
      Settings.Builder settings =
          Settings.builder().put("cluster.name", config.getElasticsearchCluster());
      client = XPackUtils.secureClient(config.getUser(), config.getPassword(), settings);
      client.addTransportAddress(
          new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
              Integer.parseInt(config.getElasticsearchPort())));
    } catch (RuntimeException | UnknownHostException e) {
      LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
      if (client != null) {
        client.close();
      }
      throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
    }
    return client;
  }

  @Provides
  @Singleton
  @Inject
  public CalsElasticsearchIndexerDao elasticsearchDao(Client client,
      CalsElasticsearchConfiguration configuration) {

    CalsElasticsearchIndexerDao esIndexerDao = new CalsElasticsearchIndexerDao(client,
        configuration);
    esIndexerDao.createIndexIfMissing();

    return esIndexerDao;
  }

  @Provides
  public CalsElasticsearchConfiguration config() {
    try {
      File jobConfigFile = new File(jobOptions.getEsConfigLoc());
      return new ObjectMapper(new YAMLFactory())
          .readValue(jobConfigFile, CalsElasticsearchConfiguration.class);
    } catch (Exception e) {
      LOGGER.error("Error reading job configuration: {}", e.getMessage(), e);
      throw new JobsException("Error reading job configuration: " + e.getMessage(), e);
    }
  }
}
