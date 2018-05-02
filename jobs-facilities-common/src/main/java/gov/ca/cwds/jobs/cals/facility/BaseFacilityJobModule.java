package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.inject.BatchProcessor;
import gov.ca.cwds.jobs.common.job.BulkWriter;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public abstract class BaseFacilityJobModule extends AbstractBaseJobModule {

  private Class<? extends BulkWriter<ChangedFacilityDTO>> facilityElasticWriterClass;

  public BaseFacilityJobModule(String[] args) {
    super(args);
    this.facilityElasticWriterClass = FacilityElasticWriter.class;
  }

  public void setFacilityElasticWriterClass(
      Class<? extends BulkWriter<ChangedFacilityDTO>> facilityElasticWriterClass) {
    this.facilityElasticWriterClass = facilityElasticWriterClass;
  }

  @Override
  protected void configure() {
    super.configure();
    bind(new TypeLiteral<BulkWriter<ChangedFacilityDTO>>() {
    }).to(facilityElasticWriterClass);
    bind(new TypeLiteral<BatchProcessor<ChangedFacilityDTO>>() {
    }).to(FacilityBatchProcessor.class);
    bind(FacilityParameterObjectBuilder.class);
    install(new MappingModule());
    install(new CalsnsDataAccessModule());
  }

  protected <T extends BaseFacilityJobConfiguration> T getJobsConfiguration(JobOptions jobOptions,
      Class<T> configurationClass) {
    T facilityJobConfiguration =
        BaseFacilityJobConfiguration.getJobsConfiguration(configurationClass,
            jobOptions.getEsConfigLoc());
    facilityJobConfiguration.setDocumentMapping("facility.mapping.json");
    facilityJobConfiguration.setIndexSettings("facility.settings.json");
    return facilityJobConfiguration;
  }

}
