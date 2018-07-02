package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.CognitoProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.service.CognitoIdmService;
import gov.ca.cwds.idm.service.IdmService;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.inject.ElasticSearchBulkSize;
import gov.ca.cwds.jobs.common.job.BulkWriter;
import gov.ca.cwds.jobs.common.job.Job;

import java.io.IOException;


public class CapUsersJobModule extends AbstractBaseJobModule {
  public CapUsersJobModule(String[] args) {
    super(args);
  }

  private CognitoProperties cognitoProperties = new CognitoProperties();

  private PerryProperties2 perryProperties2 = new PerryProperties2();

  {
    cognitoProperties.setIamAccessKeyId("AKIAJHZTTS36NDBH7FHA");
    cognitoProperties.setIamSecretKey("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");
    cognitoProperties.setRegion("us-east-2");
    cognitoProperties.setUserpool("us-east-2_Hp5BRwwOJ");
  }

  @Override
  protected void configure() {
    super.configure();

    bind(Job.class).to(CapUsersJob.class);
    //bind(JobBatchIterator.class).to(CapJobBatchIteratorImpl.class);
    bind(IdmService.class).to(CognitoIdmService.class);
    //bind(CognitoServiceFacade.class).to(CognitoServiceFacade.class);

    bind(new TypeLiteral<BulkWriter<User>>() {
    }).to(TestCapUserWriter.class);

    bind(CognitoProperties.class).toInstance(cognitoProperties);

    bind(PerryProperties.class).toInstance(perryProperties2);

    install(new CwsCmsDataAccessModule());

//    bindConstant().annotatedWith(ElasticSearchBulkSize.class).to(60);

  }


  @Provides
  @Override
  @Inject
  protected CapUsersJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
    return BaseJobConfiguration.getJobsConfiguration(CapUsersJobConfiguration.class, jobsOptions.getEsConfigLoc());

  }
}

