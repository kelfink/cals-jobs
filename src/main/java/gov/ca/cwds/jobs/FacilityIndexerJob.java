package gov.ca.cwds.jobs;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import gov.ca.cwds.common.ApiFileAssistant;
import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.jobs.component.Job;
import gov.ca.cwds.jobs.config.JobConfiguration;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.facility.FacilityProcessor;
import gov.ca.cwds.jobs.facility.FacilityRowMapper;
import gov.ca.cwds.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.jobs.util.JobLogs;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(FacilityIndexerJob.class);

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

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(Names.named(LIS_SESSION_FACTORY_NM))
        .toInstance(new Configuration().configure("lis-hibernate.cfg.xml").buildSessionFactory());
    bind(RowMapper.class).to(FacilityRowMapper.class);
  }

  /**
   * Only used to facilitate code coverage.
   * 
   * @param settings ES settings
   * @return ES transport client
   */
  protected TransportClient produceTransportClient(final Settings settings) {
    return new PreBuiltTransportClient(settings);
  }

  @Provides
  @Inject
  public Client elasticsearchClient(JobConfiguration config) throws NeutronException {
    TransportClient client = null;
    if (config != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        client = produceTransportClient(
            Settings.builder().put("cluster.name", config.getElasticsearchCluster()).build());
        client.addTransportAddress(
            new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
                Integer.parseInt(config.getElasticsearchPort())));
      } catch (Exception e) {
        throw JobLogs.buildCheckedException(LOGGER, e,
            "Error initializing Elasticsearch client: {}", e.getMessage());
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
  public JobConfiguration config() throws NeutronException {
    JobConfiguration configuration = null;
    if (config != null) {
      try {
        configuration =
            new ObjectMapper(new YAMLFactory()).readValue(config, JobConfiguration.class);
      } catch (Exception e) {
        throw JobLogs.buildCheckedException(LOGGER, e,
            "Error initializing Elasticsearch client: {}", e.getMessage());
      }
    }
    return configuration;
  }

  /**
   * SonarQube complains loudly about a "vulnerability" with
   * {@code connection.prepareStatement(query)}.
   * 
   * @param jobConfig config file containing the SQL query.
   * @return Function that produces a PreparedStatement
   */
  @Provides
  @Named("facility-statement-maker")
  @Inject
  public Function<Connection, PreparedStatement> createPreparedStatementMaker(
      final JobConfiguration jobConfig) {
    return c -> {
      try {
        return c.prepareStatement(jobConfig.getJobLisReaderQuery()); // NOSONAR
      } catch (SQLException e) {
        throw JobLogs.buildRuntimeException(LOGGER, e, "FAILED TO PREPARE STATEMENT!",
            e.getMessage());
      }
    };
  }

  @Provides
  @Named("facility-reader")
  @Inject
  public JobReader lisItemReader(JobConfiguration jobConfiguration,
      FacilityRowMapper facilityRowMapper,
      @Named(LIS_SESSION_FACTORY_NM) SessionFactory sessionFactory,
      Function<Connection, PreparedStatement> createPreparedStatementMaker) {
    return new JdbcJobReader<>(sessionFactory, facilityRowMapper, createPreparedStatementMaker);
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

  public static void main(String[] args) {
    if (args.length == 0) {
      LOGGER.warn("usage: java -cp jobs.jar {} {}", FacilityIndexerJob.class.getName(),
          " path/to/config/file.yaml");
    }
    try {
      final File configFile = new ApiFileAssistant().validateFileLocation(args[0]);
      Injector injector = Guice.createInjector(new FacilityIndexerJob(configFile)); // NOSONAR
      Job job = injector.getInstance(Key.get(Job.class, Names.named("facility-job")));
      job.run();
    } catch (Exception e) {
      LOGGER.error("ERROR: {}", e.getMessage(), e);
    }
  }

}
