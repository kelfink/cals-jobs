package gov.ca.cwds.jobs.inject;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import gov.ca.cwds.dao.NeutronReferentialIntegrityInterceptor;
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
import gov.ca.cwds.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.dao.cms.ReplicatedSubstituteCareProviderDao;
import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeCache;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeDao;
import gov.ca.cwds.data.persistence.cms.CmsSystemCodeCacheService;
import gov.ca.cwds.data.persistence.cms.EsChildPersonCase;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.EsParentPersonCase;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.SystemCodeDaoFileImpl;
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
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;

/**
 * Guice dependency injection (DI), module which constructs and manages common class instances for
 * batch jobs.
 * 
 * @author CWDS API Team
 */
public class JobsGuiceInjector extends AbstractModule {
  private static final Logger LOGGER = LogManager.getLogger(JobsGuiceInjector.class);

  private File esConfig;
  private String lastJobRunTimeFilename;

  /**
   * Default constructor.
   */
  public JobsGuiceInjector() {
    // Default, no-op.
  }

  /**
   * Usual constructor.
   * 
   * @param esConfigFileLoc location of Elasticsearch configuration file
   * @param lastJobRunTimeFilename location of last run file
   */
  public JobsGuiceInjector(final File esConfigFileLoc, String lastJobRunTimeFilename) {
    this.esConfig = esConfigFileLoc;
    this.lastJobRunTimeFilename =
        !StringUtils.isBlank(lastJobRunTimeFilename) ? lastJobRunTimeFilename : "";
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
    final Interceptor interceptor = new NeutronReferentialIntegrityInterceptor();
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class)
        .toInstance(new Configuration().configure("jobs-cms-hibernate.cfg.xml")
            .setInterceptor(interceptor).addAnnotatedClass(BatchBucket.class)
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
            .addAnnotatedClass(ReplicatedClient.class)
            .addAnnotatedClass(ReplicatedClientAddress.class)
            .addAnnotatedClass(ReplicatedAddress.class).buildSessionFactory());

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

    // PostgreSQL:
    bind(EsIntakeScreeningDao.class);

    // Instantiate as a singleton, else Guice creates a new instance each time.
    bind(ObjectMapper.class).asEagerSingleton();

    // Required for annotation injection.
    bindConstant().annotatedWith(LastRunFile.class).to(this.lastJobRunTimeFilename);

    // Register CMS system code translator.
    bind(ApiSystemCodeDao.class).to(SystemCodeDaoFileImpl.class);
    bind(ApiSystemCodeCache.class).to(CmsSystemCodeCacheService.class).asEagerSingleton();
    bind(CmsSystemCodeSerializer.class).asEagerSingleton();

    // Only one instance of ES DAO.
    bind(ElasticsearchDao.class).asEagerSingleton();

    // Static injection?
    // requestStaticInjection(ElasticSearchPerson.class);
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
        client = new PreBuiltTransportClient(
            Settings.builder().put("cluster.name", config.getElasticsearchCluster()).build());
        client.addTransportAddress(
            new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
                Integer.parseInt(config.getElasticsearchPort())));
      } catch (Exception e) {
        LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
        throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
      }
    }
    return client;
  }

  /**
   * Instantiate the singleton ElasticSearch 2.x client on demand.
   * 
   * @return initialized singleton ElasticSearch client
   */
  // @Singleton
  // @Provides
  // public Client elasticsearchClient() {
  // Client client = null;
  // if (esConfig != null) {
  // LOGGER.info("Create NEW ES client");
  // try {
  // final ElasticsearchConfiguration config = elasticSearchConfig();
  // Settings settings = Settings.settingsBuilder()
  // .put("cluster.name", config.getElasticsearchCluster()).build();
  // client = TransportClient.builder().settings(settings).build().addTransportAddress(
  // new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
  // Integer.parseInt(config.getElasticsearchPort())));
  // } catch (Exception e) {
  // LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
  // throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
  // }
  // }
  // return client;
  // }

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

}
