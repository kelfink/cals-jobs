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
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.NeutronGuiceModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.common.ApiFileAssistant;
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
import gov.ca.cwds.data.es.ElasticSearchPerson;
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
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.component.AtomLaunchScheduler;
import gov.ca.cwds.jobs.component.AtomRocketFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.AtomFlightPlanLog;
import gov.ca.cwds.jobs.schedule.FlightPlanLog;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.schedule.RocketFactory;
import gov.ca.cwds.jobs.service.NeutronElasticValidator;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.elastic.XPackUtils;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.services.cms.CachingSystemCodeService;

/**
 * Guice dependency injection (DI), module which constructs and manages common class instances for
 * batch jobs.
 * 
 * <p>
 * Also known as, "Hyper Cube", Jimmy's invention to store an infinite number of items in a small
 * place.
 * </p>
 * 
 * @author CWDS API Team
 */
public class HyperCube extends NeutronGuiceModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(HyperCube.class);

  private static final String HIBERNATE_CONFIG_CMS = "jobs-cms-hibernate.cfg.xml";

  private static final String HIBERNATE_CONFIG_NS = "jobs-ns-hibernate.cfg.xml";

  /**
   * The <strong>singleton</strong> Guice Injector used for all Job instances during the life of
   * this batch JVM.
   */
  private static Injector injector;

  private File esConfig;

  private String lastJobRunTimeFilename;

  private FlightPlan opts;

  private String hibernateConfigCms = HIBERNATE_CONFIG_CMS;

  private String hibernateConfigNs = HIBERNATE_CONFIG_NS;

  private static HyperCube instance;

  /**
   * Default constructor.
   */
  public HyperCube() {
    this.opts = null;
  }

  /**
   * Usual constructor.
   * 
   * @param opts command line options
   * @param esConfigFile location of Elasticsearch configuration file
   * @param lastJobRunTimeFilename location of last run file
   */
  public HyperCube(final FlightPlan opts, final File esConfigFile,
      String lastJobRunTimeFilename) {
    this.esConfig = esConfigFile;
    this.lastJobRunTimeFilename =
        !StringUtils.isBlank(lastJobRunTimeFilename) ? lastJobRunTimeFilename : "";
    this.opts = opts;
  }

  private static synchronized HyperCube buildCube(final FlightPlan opts) {
    HyperCube ret;

    if (instance != null) {
      ret = instance;
    } else {
      ret = new HyperCube(opts,
          new ApiFileAssistant().validateFileLocation(opts.getEsConfigLoc()), opts.getLastRunLoc());
    }

    return ret;
  }

  /**
   * Build the Guice Injector once and use it for all Job instances during the life of this batch
   * JVM.
   * 
   * @param opts command line options
   * @return Guice Injector
   * @throws JobsException if unable to construct dependencies
   */
  public static synchronized Injector buildInjector(final FlightPlan opts) {
    if (injector == null) {
      try {
        injector = Guice.createInjector(buildCube(opts));

        // Initialize system code cache.
        injector.getInstance(gov.ca.cwds.rest.api.domain.cms.SystemCodeCache.class);

        // Static injection.
        ElasticTransformer.setMapper(injector.getInstance(ObjectMapper.class));

        ElasticSearchPerson.getSystemCodes();
      } catch (Exception e) {
        throw JobLogs.runtime(LOGGER, e, "FAILED TO BUILD INJECTOR! {}", e.getMessage());
      }
    }

    return injector;
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param opts command line options
   * @return batch job, ready to run
   * @param <T> Person persistence type
   * @throws NeutronException checked exception
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newJob(final Class<T> klass,
      final FlightPlan opts) throws NeutronException {
    try {
      final T ret = buildInjector(opts).getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO CREATE JOB!: {}", e.getMessage());
    }
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   * @param <T> Person persistence type
   * @throws NeutronException checked exception
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newJob(final Class<T> klass,
      String... args) throws NeutronException {
    final FlightPlan opts = FlightPlan.parseCommandLine(args);
    return newJob(klass, opts);
  }

  /**
   * Register DB2 and PostgreSQL replication entity classes with Hibernate.
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
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class)
        .toInstance(makeCmsSessionFactory());

    // PostgreSQL session factory:
    bind(SessionFactory.class).annotatedWith(NsSessionFactory.class)
        .toInstance(makeNsSessionFactory());

    bindDaos();

    // Inject annotations.
    bindConstant().annotatedWith(LastRunFile.class).to(this.lastJobRunTimeFilename);

    // Miscellaneous:
    bind(NeutronElasticValidator.class);

    // Singleton:
    bind(ObjectMapper.class).toInstance(ObjectMapperUtils.createObjectMapper());
    bind(ElasticsearchDao.class).asEagerSingleton();
    bind(FlightRecorder.class).asEagerSingleton();
    bind(AtomFlightPlanLog.class).to(FlightPlanLog.class).asEagerSingleton();
    bind(AtomRocketFactory.class).to(RocketFactory.class).asEagerSingleton();
  }

  protected void bindDaos() {
    LOGGER.info("make DAOs");

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

    // CMS system codes.
    bind(SystemCodeDao.class);
    bind(SystemMetaDao.class);
  }

  protected SessionFactory makeCmsSessionFactory() {
    LOGGER.info("make CMS session factory");
    return new Configuration().configure(getHibernateConfigCms())
        .addAnnotatedClass(BatchBucket.class).addAnnotatedClass(EsClientAddress.class)
        .addAnnotatedClass(EsRelationship.class).addAnnotatedClass(EsPersonReferral.class)
        .addAnnotatedClass(EsChildPersonCase.class).addAnnotatedClass(EsParentPersonCase.class)
        .addAnnotatedClass(ReplicatedAttorney.class)
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
        .buildSessionFactory();
  }

  protected SessionFactory makeNsSessionFactory() {
    LOGGER.info("make NS session factory");
    return new Configuration().configure(getHibernateConfigNs())
        .addAnnotatedClass(EsIntakeScreening.class).addAnnotatedClass(IntakeScreening.class)
        .buildSessionFactory();
  }

  @Provides
  @Singleton
  public AtomLaunchScheduler provideLaunchDirector() {
    return LaunchCommand.getInstance();
  }

  @Provides
  @Singleton
  public SystemCodeCache provideSystemCodeCache(SystemCodeDao systemCodeDao,
      SystemMetaDao systemMetaDao) {
    final long secondsToRefreshCache = 15 * 24 * 60 * (long) 60; // 15 days
    final SystemCodeCache systemCodeCache =
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
      LOGGER.warn("ENABLE X-PACK");
      final Settings.Builder settings =
          Settings.builder().put("cluster.name", config.getElasticsearchCluster());
      ret = XPackUtils.secureClient(config.getUser(), config.getPassword(), settings);
    } else {
      LOGGER.warn("DISABLE X-PACK");
      ret = new PreBuiltTransportClient(
          Settings.builder().put("cluster.name", config.getElasticsearchCluster()).build());
    }
    return ret;
  }

  /**
   * Elasticsearch 5x. Instantiate the singleton ElasticSearch client on demand.
   * 
   * <p>
   * Initializes X-Pack security.
   * </p>
   * 
   * @return initialized singleton ElasticSearch client
   * @throws NeutronException on ES connection error
   */
  @Provides
  public Client elasticsearchClient() throws NeutronException {
    TransportClient client = null;
    if (esConfig != null) {
      LOGGER.debug("Create NEW ES client");
      try {
        final ElasticsearchConfiguration config = elasticSearchConfig();
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
        throw JobLogs.checked(LOGGER, e,
            "Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }

  /**
   * Read Elasticsearch configuration on demand.
   * 
   * @return ES configuration
   * @throws NeutronException on error
   */
  @Provides
  public ElasticsearchConfiguration elasticSearchConfig() throws NeutronException {
    ElasticsearchConfiguration ret = null;
    if (esConfig != null) {
      LOGGER.debug("Create NEW ES configuration");
      try {
        ret = new ObjectMapper(new YAMLFactory()).readValue(esConfig,
            ElasticsearchConfiguration.class);
      } catch (Exception e) {
        throw JobLogs.checked(LOGGER, e, "ERROR READING ES CONFIG! {}", e.getMessage(), e);
      }
    }
    return ret;
  }

  @SuppressWarnings("javadoc")
  public FlightPlan getOpts() {
    return opts;
  }

  @SuppressWarnings("javadoc")
  public void setOpts(FlightPlan opts) {
    this.opts = opts;
  }

  @SuppressWarnings("javadoc")
  public static Injector getInjector() {
    return injector;
  }

  public String getHibernateConfigCms() {
    return hibernateConfigCms;
  }

  public void setHibernateConfigCms(String hibernateConfigCms) {
    this.hibernateConfigCms = hibernateConfigCms;
  }

  public String getHibernateConfigNs() {
    return hibernateConfigNs;
  }

  public void setHibernateConfigNs(String hibernateConfigNs) {
    this.hibernateConfigNs = hibernateConfigNs;
  }

  public static HyperCube getInstance() {
    return instance;
  }

  public static void setInstance(HyperCube instance) {
    HyperCube.instance = instance;
  }

}
