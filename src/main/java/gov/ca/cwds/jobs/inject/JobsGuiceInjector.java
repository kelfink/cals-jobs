package gov.ca.cwds.jobs.inject;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
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
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.dao.cms.ReplicatedEducationProviderContactDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherChildInPlacemtHomeDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.dao.cms.ReplicatedSafetyAlertsDao;
import gov.ca.cwds.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.dao.cms.ReplicatedSubstituteCareProviderDao;
import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.data.cms.SystemCodeDao;
import gov.ca.cwds.data.cms.SystemMetaDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsChildPersonCase;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.EsParentPersonCase;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.EsSafetyAlert;
import gov.ca.cwds.data.persistence.cms.SystemCode;
import gov.ca.cwds.data.persistence.cms.SystemMeta;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAttorney;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedEducationProviderContact;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedReporter;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedServiceProvider;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider;
import gov.ca.cwds.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.data.persistence.ns.IntakeScreening;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.elastic.XPackUtils;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.services.cms.CachingSystemCodeService;

/**
 * Guice dependency injection (DI), module which constructs and manages common class instances for
 * batch jobs.
 * 
 * @author CWDS API Team
 */
public class JobsGuiceInjector extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobsGuiceInjector.class);

  /**
   * Guice Injector used for all Job instances during the life of this batch JVM.
   */
  private static Injector injector;

  private File esConfig;
  private String lastJobRunTimeFilename;
  private String altInputFilename;
  private JobOptions opts;

  /**
   * Default constructor.
   */
  public JobsGuiceInjector() {
    this.opts = null;
  }

  /**
   * Usual constructor.
   * 
   * @param opts command line options
   * @param esConfigFile location of Elasticsearch configuration file
   * @param lastJobRunTimeFilename location of last run file
   * @param altInputFilename alternate input file
   */
  public JobsGuiceInjector(final JobOptions opts, final File esConfigFile,
      String lastJobRunTimeFilename, String altInputFilename) {
    this.esConfig = esConfigFile;
    this.lastJobRunTimeFilename =
        !StringUtils.isBlank(lastJobRunTimeFilename) ? lastJobRunTimeFilename : "";
    this.altInputFilename = !StringUtils.isBlank(altInputFilename) ? altInputFilename : "";
    this.opts = opts;
  }

  /**
   * Build the Guice Injector once and use it for all Job instances during the life of this batch
   * JVM.
   * 
   * @param opts command line options
   * @return Guice Injector
   * @throws JobsException if unable to construct dependencies
   */
  public static synchronized Injector buildInjector(final JobOptions opts) throws JobsException {
    if (injector == null) {
      try {
        injector = Guice.createInjector(new JobsGuiceInjector(opts, new File(opts.getEsConfigLoc()),
            opts.getLastRunLoc(), opts.getAltInputFile()));

        // Initialize system code cache.
        injector.getInstance(gov.ca.cwds.rest.api.domain.cms.SystemCodeCache.class);

        ElasticTransformer.setMapper(injector.getInstance(ObjectMapper.class));

      } catch (CreationException e) {
        throw JobLogs.buildException(LOGGER, e, "FAILED TO BUILD INJECTOR! {}", e.getMessage());
      }
    }

    return injector;
  }

  /**
   * Prepare a batch job with all required dependencies for <strong>continuous mode</strong> with
   * the shared injector.
   * 
   * @param klass batch job class
   * @param opts options for this job execution
   * @return batch job, ready to run
   * @param <T> Person persistence type
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newContinuousJob(final Class<T> klass,
      final JobOptions opts) {
    try {
      final T ret = injector.getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      throw JobLogs.buildException(LOGGER, e, "FAILED TO CREATE JOB!: {}", e.getMessage());
    }
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   * @param <T> Person persistence type
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newJob(final Class<T> klass,
      String... args) {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final T ret = buildInjector(opts).getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      throw JobLogs.buildException(LOGGER, e, "FAILED TO CREATE JOB!: {}", e.getMessage());
    }
  }

  /**
   * Register all DB2 replication entity classes and PostgreSQL view entity classes with Hibernate.
   * Note that method addPackage() is not working as hoped.
   * 
   * <p>
   * Parent class:
   * </p>
   * {@inheritDoc}
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    // DB2 session factory:
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class).toInstance(new Configuration()
        .configure("jobs-cms-hibernate.cfg.xml").addAnnotatedClass(BatchBucket.class)
        .addAnnotatedClass(EsClientAddress.class).addAnnotatedClass(EsRelationship.class)
        .addAnnotatedClass(EsPersonReferral.class).addAnnotatedClass(EsChildPersonCase.class)
        .addAnnotatedClass(EsParentPersonCase.class).addAnnotatedClass(ReplicatedAttorney.class)
        .addAnnotatedClass(ReplicatedCollateralIndividual.class)
        .addAnnotatedClass(ReplicatedEducationProviderContact.class)
        .addAnnotatedClass(ReplicatedOtherAdultInPlacemtHome.class)
        .addAnnotatedClass(ReplicatedOtherChildInPlacemtHome.class)
        .addAnnotatedClass(ReplicatedOtherClientName.class)
        .addAnnotatedClass(ReplicatedReporter.class)
        .addAnnotatedClass(ReplicatedServiceProvider.class)
        .addAnnotatedClass(ReplicatedSubstituteCareProvider.class)
        .addAnnotatedClass(ReplicatedClient.class).addAnnotatedClass(ReplicatedClientAddress.class)
        .addAnnotatedClass(ReplicatedAddress.class).addAnnotatedClass(SystemCode.class)
        .addAnnotatedClass(EsSafetyAlert.class).addAnnotatedClass(SystemMeta.class)
        .buildSessionFactory());

    // PostgreSQL session factory:
    bind(SessionFactory.class).annotatedWith(NsSessionFactory.class)
        .toInstance(new Configuration().configure("jobs-ns-hibernate.cfg.xml")
            .addAnnotatedClass(EsIntakeScreening.class).addAnnotatedClass(IntakeScreening.class)
            .buildSessionFactory());

    // DB2 replicated tables:
    bind(ReplicatedRelationshipsDao.class);
    bind(ReplicatedClientDao.class);
    bind(ReplicatedReporterDao.class);
    bind(ReplicatedAttorneyDao.class);
    bind(ReplicatedCollateralIndividualDao.class);
    bind(ReplicatedOtherAdultInPlacemtHomeDao.class);
    bind(ReplicatedOtherChildInPlacemtHomeDao.class);
    bind(ReplicatedOtherClientNameDao.class);
    bind(ReplicatedServiceProviderDao.class);
    bind(ReplicatedSubstituteCareProviderDao.class);
    bind(ReplicatedEducationProviderContactDao.class);
    bind(ReplicatedPersonReferralsDao.class);
    bind(ReplicatedPersonCasesDao.class);
    bind(ReplicatedSafetyAlertsDao.class);

    // PostgreSQL:
    bind(EsIntakeScreeningDao.class);

    // Instantiate as a singleton, else Guice creates a new instance each time.
    bind(ObjectMapper.class).asEagerSingleton();

    // Required for annotation injection.
    bindConstant().annotatedWith(LastRunFile.class).to(this.lastJobRunTimeFilename);

    // For alternative, file-based sources.
    bindConstant().annotatedWith(AltInputFile.class).to(this.altInputFilename);

    bind(SystemCodeDao.class);
    bind(SystemMetaDao.class);

    // Only one instance of ES DAO.
    bind(ElasticsearchDao.class).asEagerSingleton();
  }

  @Provides
  public SystemCodeCache provideSystemCodeCache(SystemCodeDao systemCodeDao,
      SystemMetaDao systemMetaDao) {
    final long secondsToRefreshCache = 15 * 24 * 60 * (long) 60; // 15 days
    SystemCodeCache systemCodeCache =
        new CachingSystemCodeService(systemCodeDao, systemMetaDao, secondsToRefreshCache, false);
    systemCodeCache.register();
    return systemCodeCache;
  }

  @Provides
  @Singleton
  public CmsSystemCodeSerializer provideCmsSystemCodeSerializer(SystemCodeCache systemCodeCache) {
    return new CmsSystemCodeSerializer(systemCodeCache);
  }

  protected TransportClient makeTransportClient(final ElasticsearchConfiguration config,
      boolean es55) {
    TransportClient ret;
    if (es55) {
      LOGGER.info("ENABLE X-PACK");
      final Settings.Builder settings =
          Settings.builder().put("cluster.name", config.getElasticsearchCluster());
      ret = XPackUtils.secureClient(config.getUser(), config.getPassword(), settings);
    } else {
      LOGGER.info("DISABLE X-PACK");
      ret = new PreBuiltTransportClient(
          Settings.builder().put("cluster.name", config.getElasticsearchCluster()).build());
    }
    return ret;
  }

  /**
   * Elasticsearch 5x. Instantiate the singleton ElasticSearch client on demand.
   * 
   * @return initialized singleton ElasticSearch client
   */
  @Provides
  public Client elasticsearchClient() {
    TransportClient client = null;
    if (esConfig != null) {
      LOGGER.warn("Create NEW ES client");
      try {
        final ElasticsearchConfiguration config = elasticSearchConfig();
        // X-Pack security
        client = makeTransportClient(config, StringUtils.isNotBlank(config.getUser())
            && StringUtils.isNotBlank(config.getPassword()));
        client.addTransportAddress(
            new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
                Integer.parseInt(config.getElasticsearchPort())));
      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        if (client != null) {
          client.close();
        }
        throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }

  /**
   * Read Elasticsearch configuration on demand.
   * 
   * @return ES configuration
   */
  @Provides
  public ElasticsearchConfiguration elasticSearchConfig() {
    ElasticsearchConfiguration ret = null;
    if (esConfig != null) {
      LOGGER.info("Create NEW ES configuration");
      try {
        ret = new ObjectMapper(new YAMLFactory()).readValue(esConfig,
            ElasticsearchConfiguration.class);
      } catch (Exception e) {
        LOGGER.error("Error reading Elasticsearch configuration: {}", e.getMessage(), e);
        throw new ApiException("Error reading Elasticsearch configuration: " + e.getMessage(), e);
      }
    }
    return ret;
  }

  public JobOptions getOpts() {
    return opts;
  }

  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

  public static Injector getInjector() {
    return injector;
  }

}
