package gov.ca.cwds.jobs.cals.facility.cws;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.CwsFacilityServiceProvider;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDTO;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.api.ChangedEntityService;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.job.Job;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class CwsFacilityJobModule extends BaseFacilityJobModule {

  public CwsFacilityJobModule(String[] args) {
    super(args);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(ChangedEntitiesIdentifiersService.class)
        .toProvider(CwsChangedIdentifiersServiceProvider.class);
    bind(CwsFacilityService.class).toProvider(CwsFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDTO>>() {
    }).to(CwsChangedFacilityService.class);
    bind(Job.class).to(CwsFacilityJob.class);
    install(new CwsCmsRsDataAccessModule());
  }

  @Provides
  @Override
  @Inject
  public CwsFacilityJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
    return super.getJobsConfiguration(jobOptions, CwsFacilityJobConfiguration.class);
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @CmsSessionFactory SessionFactory cwsSessionFactory) {
    return new UnitOfWorkAwareProxyFactory(Constants.UnitOfWork.CMS, cwsSessionFactory);
  }

}
