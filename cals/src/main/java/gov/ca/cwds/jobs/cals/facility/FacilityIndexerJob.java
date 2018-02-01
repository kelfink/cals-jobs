package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.DataAccessServicesModule;
import gov.ca.cwds.cals.inject.FacilityParameterObjectBuilderProvider;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectCMSAwareBuilder;
import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.BaseCalsIndexerJob;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.CalsElasticsearchIndexerDao;
import gov.ca.cwds.jobs.cals.CalsJobConfiguration;
import gov.ca.cwds.jobs.cals.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.CwsCmsRsDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.FasDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.LisDataAccessModule;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class FacilityIndexerJob extends BaseCalsIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(FacilityIndexerJob.class);

  public static void main(String[] args) {
    runJob(FacilityIndexerJob.class, args);
  }

  @Override
  protected void configure() {
    super.configure();
    final FacilityJobConfiguration facilityJobsConfiguration = getCalsJobsConfiguration();
    install(new CwsCmsRsDataAccessModule(facilityJobsConfiguration.getCmsDataSourceFactory(), DataSourceName.CWSRS.name()));
    install(new LisDataAccessModule(facilityJobsConfiguration.getLisDataSourceFactory(), Constants.UnitOfWork.LIS));
    install(new FasDataAccessModule(facilityJobsConfiguration.getFasDataSourceFactory(), Constants.UnitOfWork.FAS));
    install(new NsDataAccessModule(facilityJobsConfiguration.getCalsnsDataSourceFactory(), Constants.UnitOfWork.CALSNS));
    install(new DataAccessServicesModule() {
      private SessionFactory getXaCmsSessionFactory(Injector injector) {
        return injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));
      }

      @Override
      protected SessionFactory getDataAccessSercvicesSessionFactory(Injector injector) {
        return getXaCmsSessionFactory(injector);
      }

    });
    bind(FacilityReader.class);
    bind(FacilityElasticJobWriter.class);
    bind(ChangedFacilityService.class);
    bind(FacilityParameterObjectCMSAwareBuilder.class).toProvider(FacilityParameterObjectBuilderProvider.class);
  }

  public FacilityJobConfiguration getCalsJobsConfiguration() {
    return CalsJobConfiguration.getCalsJobsConfiguration(FacilityJobConfiguration.class,
            getJobOptions().getEsConfigLoc());
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
    FacilityElasticJobWriter(CalsElasticsearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
      super(elasticsearchDao, objectMapper);
    }
  }
}
