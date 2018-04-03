package gov.ca.cwds.jobs.common.job;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersProvider;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.impl.BatchProcessor;
import gov.ca.cwds.jobs.common.job.impl.JobImpl;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class TestModule extends AbstractBaseJobModule {

  private ChangedEntityService changedEntityService;
  private ChangedIdentifiersProvider changedIdentifiersProvider;
  private BulkWriter bulkWriter;

  public TestModule(String[] args) {
    super(args);
    initDefaults();
  }

  private void initDefaults() {
    changedEntityService = identifier -> new Object();
    setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
      }
    });
    bulkWriter = items -> {
    };
  }

  @Override
  protected BaseJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
    TestJobConfiguration testJobConfiguration = new TestJobConfiguration();
    testJobConfiguration.setBatchSize(1);
    return testJobConfiguration;
  }

  @Override
  protected void configure() {
    super.configure();
    bind(Job.class).to(TestJobImpl.class);
    bind(new TypeLiteral<BatchProcessor<Object>>() {
    }).to(TestBatchProcessor.class);
    bind(new TypeLiteral<ChangedEntityService<Object>>() {
    }).toInstance(changedEntityService);
    bind(new TypeLiteral<BulkWriter<Object>>() {
    }).toInstance(bulkWriter);
  }

  private static class TestJobImpl extends JobImpl<Object> {

  }

  private static class TestBatchProcessor extends BatchProcessor<Object> {

  }

  public void setChangedEntityService(ChangedEntityService changedEntityService) {
    this.changedEntityService = changedEntityService;
  }

  public void setBulkWriter(BulkWriter bulkWriter) {
    this.bulkWriter = bulkWriter;
  }

}
