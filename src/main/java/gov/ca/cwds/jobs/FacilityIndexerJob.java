package gov.ca.cwds.jobs;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
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

import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.jobs.facility.FacilityProcessor;
import gov.ca.cwds.jobs.facility.FacilityRowMapper;
import gov.ca.cwds.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.jobs.util.JobProcessor;
import gov.ca.cwds.jobs.util.JobReader;
import gov.ca.cwds.jobs.util.JobWriter;
import gov.ca.cwds.jobs.util.elastic.ElasticJobWriter;
import gov.ca.cwds.jobs.util.jdbc.JdbcJobReader;
import gov.ca.cwds.jobs.util.jdbc.RowMapper;

/**
 * 
 * <p>
 * Command line arguments:
 * </p>
 * 
 * <pre>
 * {@code run script: $java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml}
 * </pre>
 * 
 * @author CWDS Elasticsearch Team
 */
public class FacilityIndexerJob extends AbstractModule {

  private static final Logger LOGGER = LogManager.getLogger(FacilityIndexerJob.class);

  private static final String LIS_SESSION_FACTORY_NM = "lis-session-factory";

  private File config;

  /**
   * Default constructor.
   * 
   * @param config configuration file
   */
  public FacilityIndexerJob(File config) {
    this.config = config;
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      LOGGER.warn(
          "usage: java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml");
    }
    try {
      File configFile = new File(args[0]);
      Injector injector = Guice.createInjector(new FacilityIndexerJob(configFile));
      Job job = injector.getInstance(Key.get(Job.class, Names.named("facility-job")));
      job.run();
    } catch (Exception e) {
      LOGGER.fatal("ERROR: ", e.getMessage(), e);
    }
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(Names.named(LIS_SESSION_FACTORY_NM))
        .toInstance(new Configuration().configure("lis-hibernate.cfg.xml").buildSessionFactory());
    bind(RowMapper.class).to(FacilityRowMapper.class);
  }

  @Provides
  @Inject
  public Client elasticsearchClient(JobConfiguration config) {
    TransportClient client = null;
    if (config != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        // Settings settings =
        // Settings.builder().put("cluster.name", config.getElasticsearchCluster()).build();

        // DRS: Incompatible with ES 2.3.5. Won't connect.
        // client = new PreBuiltTransportClient(settings);
        // client.addTransportAddress(
        // new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
        // Integer.parseInt(config.getElasticsearchPort())));
      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        throw new JobsException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }

  @Provides
  @Singleton
  @Inject
  public Elasticsearch5xDao elasticsearchDao(Client client, JobConfiguration configuration) {
    return new Elasticsearch5xDao(client, configuration);
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
        throw new JobsException("Error reading job configuration: " + e.getMessage(), e);
      }
    }
    return configuration;
  }

  @Provides
  @Named("facility-reader")
  @Inject
  public JobReader lisItemReader(JobConfiguration jobConfiguration,
      FacilityRowMapper facilityRowMapper,
      @Named(LIS_SESSION_FACTORY_NM) SessionFactory sessionFactory) {
    return new JdbcJobReader<>(sessionFactory, facilityRowMapper,
        jobConfiguration.getJobLisReaderQuery());
  }

  @Provides
  @Named("facility-processor")
  @Inject
  public JobProcessor lisItemProcessor() {
    return new FacilityProcessor();
  }

  @Provides
  @Named("facility-writer")
  @Inject
  public JobWriter lisItemWriter(Elasticsearch5xDao elasticsearchDao, ObjectMapper objectMapper) {
    return new ElasticJobWriter<ESFacility>(elasticsearchDao, objectMapper);
  }

  @Provides
  @Named("facility-job")
  @Inject
  public Job lisItemWriter(@Named("facility-reader") JobReader jobReader,
      @Named("facility-processor") JobProcessor jobProcessor,
      @Named("facility-writer") JobWriter jobWriter) {
    return new AsyncReadWriteJob(jobReader, jobProcessor, jobWriter);
  }
}
