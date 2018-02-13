package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.DataAccessServicesModule;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.inject.CwsCmsRsDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.FasDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.LisDataAccessModule;
import gov.ca.cwds.jobs.cals.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticsearchIndexerDao;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import gov.ca.cwds.jobs.common.job.impl.AsyncReadWriteJob;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public final class FacilityIndexerJob extends BaseIndexerJob {

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
    bind(FacilityElasticJobWriter.class);
    bind(ChangedFacilityService.class);
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
