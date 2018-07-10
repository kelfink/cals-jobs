package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD_RESUME;

public class CapUsersJobModule extends AbstractBaseJobModule {
  private static final Logger LOG = LoggerFactory.getLogger(CapUsersJobModule.class);

  private Class<? extends BulkWriter<ChangedUserDTO>> capElasticWriterClass;

  private Class<? extends CapUsersIterator> capUsersJobBatchIterator;

  public CapUsersJobModule(String[] args) {
    super(args);
    this.capElasticWriterClass = CapUsersWriter.class;
    this.capUsersJobBatchIterator = CapUsersJobBatchIterator.class;
  }

  public void setCapElasticWriterClass(
          Class<? extends BulkWriter<ChangedUserDTO>> capUsersElasticWriterClass) {
    this.capElasticWriterClass = capUsersElasticWriterClass;
  }

  public void setCapUsersJobBatchIterator(Class<? extends CapUsersIterator> capUsersJobBatchIterator) {
    this.capUsersJobBatchIterator = capUsersJobBatchIterator;
  }

  @Override
  protected void configure() {
    super.configure();
    configureJobModes();

    bind(new TypeLiteral<BulkWriter<ChangedUserDTO>>() {
    }).to(capElasticWriterClass);

    bindConstant().annotatedWith(PerryApiUrl.class)
            .to(getJobsConfiguration(getJobOptions()).getPerryApiUrl());

    bind(CapUsersIterator.class).to(capUsersJobBatchIterator);

  }

  private void configureJobModes() {
    DefaultJobMode jobMode = defineJobMode();
    if (jobMode == INITIAL_LOAD) {
      configureInitialMode();
    } else {
      LOG.info("not initial mode selected");
      throw new UnsupportedOperationException("the job mode other that INITIAL is not supported");
    }
  }

  private void configureInitialMode() {
    bind(Job.class).to(CapUsersInitialJob.class);
  }

  private DefaultJobMode defineJobMode() {
    return DefaultJobMode.INITIAL_LOAD;
  }


  @Provides
  @Override
  @Inject
  protected CapUsersJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
    CapUsersJobConfiguration capUsersJobConfiguration = BaseJobConfiguration.getJobsConfiguration(CapUsersJobConfiguration.class, jobsOptions.getEsConfigLoc());
    capUsersJobConfiguration.setIndexSettings("cap.users.settings.json");
    capUsersJobConfiguration.setDocumentMapping("cap.users.mapping.json");
    return capUsersJobConfiguration;

  }

  @Provides
  public Client provideClient() {
    JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
            .property(ClientProperties.CONNECT_TIMEOUT, getJobsConfiguration(getJobOptions()).getJerseyClientConnectTimeout())
            .property(ClientProperties.READ_TIMEOUT, getJobsConfiguration(getJobOptions()).getJerseyClientReadTimeout())
            // Just ignore host verification, client will call trusted resources only
            .hostnameVerifier((hostName, sslSession) -> true);
    return clientBuilder.build();
  }
}

