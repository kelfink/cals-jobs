package gov.ca.cwds.jobs.cals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import gov.ca.cwds.generic.jobs.util.elastic.XPackUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

/**
 * @author CWDS TPT-2
 */
public abstract class BaseIndexerJob<T extends ElasticsearchConfiguration> extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexerJob.class);

  private JobOptions jobOptions;

  protected abstract T getJobsConfiguration();

  @Override
  protected void configure() {install(new MappingModule());
    bind(JobOptions.class).toInstance(jobOptions);
  }

  private JobOptions validateJobOptions(JobOptions jobOptions) {
    // check option: -c
    File configFile = new File(jobOptions.getEsConfigLoc());
    if (!configFile.exists()) {
      throw new JobsException(
          "job arguments error: specified config file " + configFile.getPath() + " not found");
    }

    // check option: -l
    File timeFilesDir = new File(jobOptions.getLastRunLoc());
    if (!timeFilesDir.exists()) {
      if (timeFilesDir.mkdir() && (LOGGER.isInfoEnabled())) {
        LOGGER.info(getPathToOutputDirectory() + " was created in file system");
      }
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Using " + getPathToOutputDirectory() + " as output folder");
    }
    return jobOptions;
  }

  private String getPathToOutputDirectory() {
    return Paths.get(jobOptions.getLastRunLoc()).normalize().toAbsolutePath().toString();
  }

  protected void run(String[] args) {
    try {
      final JobOptions jobOptions = JobOptions.parseCommandLine(args);
      setJobOptions(jobOptions);
      validateJobOptions(jobOptions);
      final Injector injector = Guice.createInjector(this);
      injector.getInstance(Job.class).run();
    } catch (RuntimeException e) {
      LOGGER.error("ERROR: ", e.getMessage(), e);
      System.exit(1);
    }
  }

  void setJobOptions(JobOptions jobOptions) {
    this.jobOptions = jobOptions;
  }

  public JobOptions getJobOptions() {
    return jobOptions;
  }

  @Provides
  @Inject
  // the client should not be closed here, it is closed when job is done
  @SuppressWarnings("squid:S2095")
  public Client elasticsearchClient(BaseJobConfiguration config) {
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
  public ElasticsearchIndexerDao elasticsearchDao(Client client,
                                                  BaseJobConfiguration configuration) {

    ElasticsearchIndexerDao esIndexerDao = new ElasticsearchIndexerDao(client,
        configuration);
    esIndexerDao.createIndexIfMissing();

    return esIndexerDao;
  }

}
