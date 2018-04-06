package gov.ca.cwds.jobs.cals.facility.lis;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.FasFacilityServiceProvider;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisFacilityServiceProvider;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.service.FasFacilityService;
import gov.ca.cwds.cals.service.LisFacilityService;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDTO;
import gov.ca.cwds.jobs.cals.facility.fas.FasDataAccessModule;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.api.ChangedEntityService;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.job.Job;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class LisFacilityJobModule extends BaseFacilityJobModule {

  private static final Logger LOG = LoggerFactory.getLogger(LisFacilityJobModule.class);

  public LisFacilityJobModule(String[] args) {
    super(args);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(ChangedEntitiesIdentifiersService.class)
        .toProvider(LisChangedIdentifiersServiceProvider.class);
    bind(LisFacilityService.class).toProvider(LisFacilityServiceProvider.class);
    bind(FasFacilityService.class).toProvider(FasFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDTO>>() {
    }).to(LisChangedFacilityService.class);
    bind(Job.class).to(LisFacilityJob.class);
    install(new LisDataAccessModule());
    install(new FasDataAccessModule());
  }

  @Provides
  @Override
  @Inject
  public LisFacilityJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
    return super.getJobsConfiguration(jobOptions, LisFacilityJobConfiguration.class);
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @FasSessionFactory SessionFactory fasSessionFactory,
      @LisSessionFactory SessionFactory lisSessionFactory) {
    try {
      ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap.<String, SessionFactory>builder()
          .put(Constants.UnitOfWork.FAS, fasSessionFactory)
          .put(Constants.UnitOfWork.LIS, lisSessionFactory)
          .build();
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory();
      FieldUtils
          .writeField(unitOfWorkAwareProxyFactory, "sessionFactories", sessionFactories, true);
      return unitOfWorkAwareProxyFactory;
    } catch (IllegalAccessException e) {
      LOG.error("Can't build UnitOfWorkAwareProxyFactory", e);
      throw new JobsException(e);
    }
  }

}
