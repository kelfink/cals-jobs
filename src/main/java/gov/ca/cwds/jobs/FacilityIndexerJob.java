package gov.ca.cwds.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.*;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.net.InetAddress;

/**
 * Created by dmitry.rudenko on 5/1/2017.
 *
 * run script:
 * $java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml
 */

public class FacilityIndexerJob extends AbstractModule {
    private static final Logger LOGGER = LogManager.getLogger(FacilityIndexerJob.class);
    private File config;

    public FacilityIndexerJob(File config) {
        this.config = config;
    }

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("usage: java -cp jobs.jar gov.ca.cwds.jobs.FacilityIndexerJob path/to/config/file.yaml");
        }
        File configFile = new File(args[0]);
        Injector injector = Guice.createInjector(new FacilityIndexerJob(configFile));
        Job job = injector.getInstance(Key.get(Job.class, Names.named("facility-job")));
        job.run();
    }

    @Override
    protected void configure() {
        bind(SessionFactory.class).annotatedWith(Names.named("lis-session-factory")).toInstance(new Configuration()
                .configure("lis-hibernate.cfg.xml").buildSessionFactory());
        bind(RowMapper.class).to(FacilityRowMapper.class);
    }

    @Provides
    @Inject
    public Client elasticsearchClient(JobConfiguration configuration) {
        Client client = null;
        if (config != null) {
            LOGGER.warn("Create NEW ES client");
            try {
                Settings settings = Settings
                        .settingsBuilder()
                        .put("cluster.name", configuration.getElasticsearchCluster())
                        .build();
                client = TransportClient.builder()
                        .settings(settings).build()
                        .addTransportAddress(
                            new InetSocketTransportAddress(
                                    InetAddress.getByName(configuration.getElasticsearchHost()),
                                Integer.parseInt(configuration.getElasticsearchPort())));
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
    public ElasticsearchDao elasticsearchDao(JobConfiguration configuration, Client client) {
        return new ElasticsearchDao(client, configuration);
    }

    @Provides
    public JobConfiguration config() {
        JobConfiguration configuration = null;
        if (config != null) {
            try {
                configuration = new ObjectMapper(new YAMLFactory()).readValue(config, JobConfiguration.class);
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
        return new JdbcJobReader<>(sessionFactory, facilityRowMapper, jobConfiguration.getJobLisReaderQuery());
    }

    @Provides
    @Named("facility-writer")
    @Inject
    public JobWriter lisItemWriter(ElasticsearchDao elasticsearchDao, ObjectMapper objectMapper) {
        return new ElasticJobWriter<ESFacility>( elasticsearchDao, objectMapper);
    }

    @Provides
    @Named("facility-job")
    @Inject
    public Job lisItemWriter(@Named("facility-reader") JobReader jobReader,
                             @Named("facility-writer") JobWriter jobWriter) {
        return new AsyncReadWriteJob(jobReader, jobWriter);
    }
}
