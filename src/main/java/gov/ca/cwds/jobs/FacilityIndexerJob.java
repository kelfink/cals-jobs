package gov.ca.cwds.jobs;

import java.io.File;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.jobs.facility.FacilityRowMapper;
import gov.ca.cwds.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.jobs.util.JobReader;
import gov.ca.cwds.jobs.util.JobWriter;
import gov.ca.cwds.jobs.util.elastic.ElasticJobWriter;
import gov.ca.cwds.jobs.util.jdbc.JdbcJobReader;
import gov.ca.cwds.jobs.util.jdbc.RowMapper;
import gov.ca.cwds.rest.api.ApiException;

/**
 * Created by dmitry.rudenko on 5/1/2017.
 *
 * run script: $java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml
 */
public class FacilityIndexerJob extends AbstractModule {
  private static final Logger LOGGER = LogManager.getLogger(FacilityIndexerJob.class);
  private File config;

  public FacilityIndexerJob(File config) {
    this.config = config;
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println(
          "usage: java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml");
    }
    try {
      /* todo rm
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final T ret = BasePersonIndexerJob.buildInjector(JobOptions.parseCommandLine(args)).getInstance(klass);

      new JobsGuiceInjector(new File(opts.esConfigLoc), opts.lastRunLoc)
       */

      File configFile = new File(args[0]);
      //JobsGuiceInjector jobsGuiceInjector = new JobsGuiceInjector(configFile, "");
      //Injector injector = Guice.createInjector(new FacilityIndexerJob(configFile), jobsGuiceInjector);
      Injector injector = Guice.createInjector(new FacilityIndexerJob(configFile));
      Job job = injector.getInstance(Key.get(Job.class, Names.named("facility-job")));
      job.run();
    } catch (Exception e) {
      e.printStackTrace(); // todo
      LOGGER.error("ERROR: ", e.getMessage(), e);
    }
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(Names.named("lis-session-factory"))
        .toInstance(new Configuration().configure("lis-hibernate.cfg.xml").buildSessionFactory());
    bind(RowMapper.class).to(FacilityRowMapper.class);
  }

  /* todo rm ?   it is in JobsGuiceInjector !
  @Provides
  @Inject
  public Client elasticsearchClient(JobConfiguration configuration) {
    TransportClient client = null;
    if (config != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        Settings settings = Settings.builder()
            .put("cluster.name", configuration.getElasticsearchCluster()).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(
                InetAddress.getByName(configuration.getElasticsearchHost()),
                Integer.parseInt(configuration.getElasticsearchPort())));
      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  } */
  /**
   * Instantiate the singleton ElasticSearch client on demand.
   *
   * @return initialized singleton ElasticSearch client
   */
  /*
  @Provides
  @Inject
  public Client elasticsearchClient(JobConfiguration configuration) {
    Client client = null;
    if (config != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        Settings settings = Settings.settingsBuilder()
            .put("cluster.name", configuration.getElasticsearchCluster()).build();
        client = TransportClient.builder().settings(settings).build()
            .addTransportAddress(new InetSocketTransportAddress(
                InetAddress.getByName(configuration.getElasticsearchHost()),
                Integer.parseInt(configuration.getElasticsearchPort())));
      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }
   */

  @Provides
  @Singleton
  @Inject
  public Client elasticsearchClient(JobConfiguration config) {  // todo pass JobConfiguration config ?
    TransportClient client = null;
    if (config != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", false)

                //.put("threadpool.indexing.type", "fixed")
                .put("thread_pool.index.size", 1) // todo try 2
                .put("thread_pool.index.queue_size", 100) // todo increase

                //.put("threadpool.bulk.type", "fixed")
                .put("thread_pool.bulk.size", 1) // todo try 2
                .put("thread_pool.bulk.queue_size", 100) // todo increase

                .put("cluster.name", config.getElasticsearchCluster()).build();

        // TODO try other SERVER and client VERSIONS

        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(
                new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
                        Integer.parseInt(config.getElasticsearchPort())));
        // todo
        // .put("client.transport.sniff", true)
        // client.node=false ?

      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }

  @Provides
  @Singleton
  @Inject
  // todo was elasticsearchDao(JobConfiguration configuration, Client client)
  public ElasticsearchDao elasticsearchDao(Client client, JobConfiguration configuration) {
    return new ElasticsearchDao(client, configuration);
  }

  @Provides
  public JobConfiguration config() {
    JobConfiguration configuration = null;
    if (config != null) {
      try {
        configuration =
            new ObjectMapper(new YAMLFactory()).readValue(config, JobConfiguration.class);
      } catch (Exception e) {
        LOGGER.error("Error reading job configuration: {}", e.getMessage(), e);
        throw new ApiException("Error reading job configuration: " + e.getMessage(), e);
      }
    }
    return configuration;
  }

  @Provides
  @Named("facility-reader")
  @Inject
  public JobReader lisItemReader(JobConfiguration jobConfiguration,
      FacilityRowMapper facilityRowMapper,
      @Named("lis-session-factory") SessionFactory sessionFactory) {
    return new JdbcJobReader<>(sessionFactory, facilityRowMapper,
        jobConfiguration.getJobLisReaderQuery());
  }

  @Provides
  @Named("facility-writer")
  @Inject
  public JobWriter lisItemWriter(ElasticsearchDao elasticsearchDao, ObjectMapper objectMapper) {
    return new ElasticJobWriter<ESFacility>(elasticsearchDao, objectMapper);
  }

  @Provides
  @Named("facility-job")
  @Inject
  public Job lisItemWriter(@Named("facility-reader") JobReader jobReader,
      @Named("facility-writer") JobWriter jobWriter) {
    return new AsyncReadWriteJob(jobReader, jobWriter);
  }
}
