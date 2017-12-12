package gov.ca.cwds.neutron.inject;

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
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
import gov.ca.cwds.dao.cms.StaffPersonDao;
import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.data.cms.SystemCodeDao;
import gov.ca.cwds.data.cms.SystemMetaDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsChildPersonCase;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.EsClientPerson;
import gov.ca.cwds.data.persistence.cms.EsParentPersonCase;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.EsSafetyAlert;
import gov.ca.cwds.data.persistence.cms.StaffPerson;
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
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.elastic.XPackUtils;
import gov.ca.cwds.neutron.atom.AtomCommandCenterConsole;
import gov.ca.cwds.neutron.atom.AtomFlightPlanManager;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchDirector;
import gov.ca.cwds.neutron.atom.AtomRocketFactory;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.launch.FlightPlanRegistry;
import gov.ca.cwds.neutron.launch.FlightRecorder;
import gov.ca.cwds.neutron.launch.LaunchCommandSettings;
import gov.ca.cwds.neutron.launch.LaunchDirector;
import gov.ca.cwds.neutron.launch.RocketFactory;
import gov.ca.cwds.neutron.launch.StandardFlightSchedule;
import gov.ca.cwds.neutron.launch.listener.NeutronJobListener;
import gov.ca.cwds.neutron.launch.listener.NeutronSchedulerListener;
import gov.ca.cwds.neutron.launch.listener.NeutronTriggerListener;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.neutron.vox.XRaySpex;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.services.cms.CachingSystemCodeService;

/**
 * Guice dependency injection (DI), module which constructs and manages common class instances for
 * batch jobs.
 * 
 * <p>
 * Also known as, <a href="http://jimmyneutron.wikia.com/wiki/Hyper_Corn">Hyper Cube</a>, Jimmy's
 * invention to store an infinite number of items in a small place.
 * </p>
 * 
 * @author CWDS API Team
 */
public class HyperCube extends NeutronGuiceModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(HyperCube.class);

  private static final String HIBERNATE_CONFIG_CMS = "jobs-cms-hibernate.cfg.xml";

  private static final String HIBERNATE_CONFIG_NS = "jobs-ns-hibernate.cfg.xml";

  /**
   * The <strong>singleton</strong> Guice injector used for all rocket instances during the life of
   * this JVM.
   */
  private static Injector injector;

  private File esConfig;

  private String lastJobRunTimeFilename;

  private FlightPlan flightPlan;

  private String hibernateConfigCms = HIBERNATE_CONFIG_CMS;

  private String hibernateConfigNs = HIBERNATE_CONFIG_NS;

  private static HyperCube instance;

  private static Function<FlightPlan, HyperCube> cubeMaker = HyperCube::buildCube;

  /**
   * Default constructor.
   */
  public HyperCube() {
    this.flightPlan = null;
  }

  /**
   * Usual constructor.
   * 
   * @param opts command line options
   * @param esConfigFile location of Elasticsearch configuration file
   * @param lastJobRunTimeFilename location of last run file
   */
  public HyperCube(final FlightPlan opts, final File esConfigFile, String lastJobRunTimeFilename) {
    this.esConfig = esConfigFile;
    this.lastJobRunTimeFilename =
        !StringUtils.isBlank(lastJobRunTimeFilename) ? lastJobRunTimeFilename : "";
    this.flightPlan = opts;
  }

  public Configuration makeHibernateConfiguration() {
    return new Configuration();
  }

  protected void init() {
    // Optional initialization, mostly for testing.
  }

  public static synchronized HyperCube buildCube(final FlightPlan opts) {
    HyperCube ret;

    if (instance != null) {
      ret = instance;
    } else {
      ret = new HyperCube(opts, new ApiFileAssistant().validateFileLocation(opts.getEsConfigLoc()),
          opts.getLastRunLoc());
    }

    return ret;
  }

  public static synchronized Injector buildInjectorFunctional(final FlightPlan flightPlan) {
    try {
      return buildInjector(flightPlan);
    } catch (NeutronException e) {
      throw JobLogs.runtime(LOGGER, e, "FAILED TO BUILD INJECTOR! {}", e.getMessage());
    }
  }

  /**
   * Build the Guice Injector once and use it for all Job instances during the life of this batch
   * JVM.
   * 
   * @param flightPlan command line options
   * @return Guice Injector
   * @throws NeutronException if unable to construct dependencies
   */
  public static synchronized Injector buildInjector(final FlightPlan flightPlan)
      throws NeutronException {
    if (injector == null) {
      try {
        injector = Guice.createInjector(cubeMaker.apply(flightPlan));

        // Initialize system code cache.
        injector.getInstance(gov.ca.cwds.rest.api.domain.cms.SystemCodeCache.class);

        // Static injection.
        ElasticTransformer.setMapper(injector.getInstance(ObjectMapper.class));

        ElasticSearchPerson.getSystemCodes();
      } catch (Exception e) {
        throw JobLogs.checked(LOGGER, e, "FAILED TO BUILD INJECTOR! {}", e.getMessage());
      }
    }

    return injector;
  }

  /**
   * Prepare a rocket with all required dependencies.
   * 
   * @param klass batch rocket class
   * @param flightPlan command line options
   * @return batch rocket, ready to run
   * @param <T> Person persistence type
   * @throws NeutronException checked exception
   */
  public static <T extends BasePersonRocket<?, ?>> T newRocket(final Class<T> klass,
      final FlightPlan flightPlan) throws NeutronException {
    try {
      final T ret = buildInjector(flightPlan).getInstance(klass);
      ret.setFlightPlan(flightPlan);
      return ret;
    } catch (CreationException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO BUILD ROCKET!: {}", e.getMessage());
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
  public static <T extends BasePersonRocket<?, ?>> T newRocket(final Class<T> klass, String... args)
      throws NeutronException {
    return newRocket(klass, FlightPlan.parseCommandLine(args));
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

    // Singleton:
    bind(ObjectMapper.class).toInstance(ObjectMapperUtils.createObjectMapper());
    bind(ElasticsearchDao.class).asEagerSingleton();

    // Command Center:
    bind(AtomFlightRecorder.class).to(FlightRecorder.class).asEagerSingleton();
    bind(AtomFlightPlanManager.class).to(FlightPlanRegistry.class).asEagerSingleton();
    bind(AtomRocketFactory.class).to(RocketFactory.class).asEagerSingleton();
    bind(AtomCommandCenterConsole.class).to(XRaySpex.class);
  }

  /**
   * Initialize all Data Access Objects (DAO).
   */
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
    bind(StaffPersonDao.class);

    // PostgreSQL:
    bind(EsIntakeScreeningDao.class);

    // CMS system codes.
    bind(SystemCodeDao.class);
    bind(SystemMetaDao.class);
  }

  protected Configuration additionalDaos(Configuration config) {
    return config;
  }

  protected SessionFactory makeCmsSessionFactory() {
    LOGGER.info("make CMS session factory");
    Configuration config = makeHibernateConfiguration().configure(getHibernateConfigCms())
        .addAnnotatedClass(BatchBucket.class).addAnnotatedClass(EsClientAddress.class)
        .addAnnotatedClass(EsClientPerson.class).addAnnotatedClass(EsRelationship.class)
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
        .addAnnotatedClass(StaffPerson.class);
    return additionalDaos(config).buildSessionFactory();
  }

  protected SessionFactory makeNsSessionFactory() {
    LOGGER.info("make NS session factory");
    return makeHibernateConfiguration().configure(getHibernateConfigNs())
        .addAnnotatedClass(EsIntakeScreening.class).addAnnotatedClass(IntakeScreening.class)
        .buildSessionFactory();
  }

  @Provides
  @Singleton
  public SystemCodeCache provideSystemCodeCache(SystemCodeDao systemCodeDao,
      SystemMetaDao systemMetaDao) {
    if (isScaffoldSystemCodeCache()) {
      return scaffoldSystemCodeCache();
    } else {
      final long secondsToRefreshCache = 15 * 24 * 60 * (long) 60; // 15 days
      final SystemCodeCache ret =
          new CachingSystemCodeService(systemCodeDao, systemMetaDao, secondsToRefreshCache, false);
      ret.register();
      return ret;
    }
  }

  protected boolean isScaffoldSystemCodeCache() {
    return false;
  }

  protected SystemCodeCache scaffoldSystemCodeCache() {
    return null;
  }

  @Provides
  @Singleton
  public LaunchCommandSettings commandCenterSettings() {
    return LaunchCommand.getSettings();
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
  @Singleton
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
        if (client != null) {
          client.close();
        }
        throw JobLogs.checked(LOGGER, e, "Error initializing Elasticsearch client: {}",
            e.getMessage(), e);
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

  /**
   * Configure Quartz scheduling.
   * 
   * @param injector DI
   * @param flightRecorder flight recorder
   * @param rocketFactory rocket factory
   * @param flightPlanMgr flight plan manager
   * @return configured launch scheduler
   * @throws SchedulerException if unable to configure Quartz
   */
  @Provides
  @Singleton
  protected AtomLaunchDirector configureQuartz(final Injector injector,
      final AtomFlightRecorder flightRecorder, final AtomRocketFactory rocketFactory,
      final AtomFlightPlanManager flightPlanMgr) throws SchedulerException {
    final boolean initialMode = LaunchCommand.isInitialMode();
    final LaunchDirector ret = new LaunchDirector(flightRecorder, rocketFactory, flightPlanMgr);

    final Properties p = new Properties();
    p.put("org.quartz.scheduler.instanceName", NeutronSchedulerConstants.SCHEDULER_INSTANCE_NAME);

    // NEXT: make configurable.
    p.put("org.quartz.threadPool.threadCount",
        initialMode ? "1" : NeutronSchedulerConstants.SCHEDULER_THREAD_COUNT);
    final StdSchedulerFactory factory = new StdSchedulerFactory(p);
    final Scheduler scheduler = factory.getScheduler();

    // NEXT: inject scheduler and rocket factory.
    scheduler.setJobFactory(rocketFactory);
    ret.setScheduler(scheduler);

    // Quartz scheduler listeners.
    final ListenerManager mgr = ret.getScheduler().getListenerManager();
    mgr.addSchedulerListener(new NeutronSchedulerListener());
    mgr.addTriggerListener(new NeutronTriggerListener(ret));
    mgr.addJobListener(initialMode ? StandardFlightSchedule.buildInitialLoadJobChainListener()
        : new NeutronJobListener());
    return ret;
  }

  @SuppressWarnings("javadoc")
  public FlightPlan getFlightPlan() {
    return flightPlan;
  }

  @SuppressWarnings("javadoc")
  public void setFlightPlan(FlightPlan opts) {
    this.flightPlan = opts;
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

  public static Function<FlightPlan, HyperCube> getCubeMaker() {
    return cubeMaker;
  }

  public static void setCubeMaker(Function<FlightPlan, HyperCube> cubeMaker) {
    HyperCube.cubeMaker = cubeMaker;
  }

  public static void setInjector(Injector injector) {
    HyperCube.injector = injector;
  }

}
