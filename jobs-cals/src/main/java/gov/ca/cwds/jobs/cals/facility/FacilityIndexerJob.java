package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.DataAccessServicesModule;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.inject.ChangedFacilityServiceProvider;
import gov.ca.cwds.jobs.cals.inject.CwsCmsRsDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.FasDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.LisDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticsearchIndexerDao;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.impl.AsyncReadWriteJob;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author CWDS TPT-2
 */
public final class FacilityIndexerJob extends BaseIndexerJob {

  private static final Logger LOG = LoggerFactory.getLogger(FacilityIndexerJob.class);

  public static void main(String[] args) {
    FacilityIndexerJob  facilityIndexerJob = new FacilityIndexerJob();
    facilityIndexerJob.run(args);
    System.out.println(String.format("Added %s facilities to ES bulk uploader", ConsumerCounter.getCounter()));
  }

  @Override
  protected void configure() {
    super.configure();
    install(new MappingModule());
    final FacilityJobConfiguration facilityJobsConfiguration = getJobsConfiguration();
    bind(FacilityJobConfiguration.class).toInstance(facilityJobsConfiguration);
    install(new CwsCmsRsDataAccessModule());
    install(new LisDataAccessModule());
    install(new FasDataAccessModule());
    install(new NsDataAccessModule());
    install(new DataAccessServicesModule() {
      private SessionFactory getXaCmsSessionFactory(Injector injector) {
        return injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));
      }

      @Override
      protected SessionFactory getDataAccessSercvicesSessionFactory(Injector injector) {
        return getXaCmsSessionFactory(injector);
      }

    });
    bind(FacilityElasticJobWriter.class);
    bind(ChangedFacilityService.class).toProvider(ChangedFacilityServiceProvider.class);
    bind(FacilityReader.class);
    bind(FacilityParameterObjectBuilder.class);
    bind(BaseJobConfiguration.class).toInstance(facilityJobsConfiguration);
    bind(FacilityJobConfiguration.class).toInstance(facilityJobsConfiguration);
  }

  public FacilityJobConfiguration getJobsConfiguration() {
    FacilityJobConfiguration facilityJobConfiguration = BaseJobConfiguration.getJobsConfiguration(FacilityJobConfiguration.class,
            getJobOptions().getEsConfigLoc());
    facilityJobConfiguration.setDocumentMapping("facility.mapping.json");
    facilityJobConfiguration.setIndexSettings("facility.settings.json");
    return facilityJobConfiguration;
  }

  @Override
  protected void close(Injector injector) {
    closeSessionFactory(injector, FasSessionFactory.class);
    closeSessionFactory(injector, CmsSessionFactory.class);
    closeSessionFactory(injector, LisSessionFactory.class);
  }

  private void closeSessionFactory(Injector injector, Class<? extends Annotation> clazz) {
    SessionFactory sessionFactory = injector.getInstance(Key.get(SessionFactory.class, clazz));
    sessionFactory.close();
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(@FasSessionFactory SessionFactory fasSessionFactory,
                                                                 @LisSessionFactory  SessionFactory lisSessionFactory,
                                                                 @CmsSessionFactory SessionFactory cwsSessionFactory) {
    try {
      ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap.<String, SessionFactory>builder()
              .put(Constants.UnitOfWork.CMS, cwsSessionFactory)
              .put(Constants.UnitOfWork.FAS, fasSessionFactory)
              .put(Constants.UnitOfWork.LIS, lisSessionFactory)
              .build();
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory();
      FieldUtils.writeField(unitOfWorkAwareProxyFactory, "sessionFactories", sessionFactories, true);
      return unitOfWorkAwareProxyFactory;
    } catch (IllegalAccessException e) {
      LOG.error("Can't build UnitOfWorkAwareProxyFactory", e);
      throw new JobsException(e);
    }
  }


  @Provides
  @Inject
  public Job provideJob(FacilityReader jobReader, FacilityElasticJobWriter jobWriter) {
    return new AsyncReadWriteJob(jobReader, jobWriter);
  }

  static class FacilityElasticJobWriter extends CalsElasticJobWriter<ChangedFacilityDTO> {

    /**
     * Constructor.
     *
     * @param elasticsearchDao ES DAO
     * @param objectMapper Jackson object mapper
     */
    @Inject
    FacilityElasticJobWriter(ElasticsearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
      super(elasticsearchDao, objectMapper);
    }
  }
}
