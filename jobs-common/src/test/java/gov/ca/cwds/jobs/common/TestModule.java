package gov.ca.cwds.jobs.common;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.api.JobModeService;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.core.JobImpl;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.TimestampDefaultJobModeService;
import gov.ca.cwds.jobs.common.mode.TimestampIncrementalModeImplementor;
import gov.ca.cwds.jobs.common.mode.TimestampInitialModeImplementor;
import gov.ca.cwds.jobs.common.mode.TimestampInitialResumeModeImplementor;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointService;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class TestModule extends AbstractBaseJobModule {

  private ChangedEntityService<TestEntity> changedEntityService;
  private ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint> changedEntitiesIdentifiers;
  private BulkWriter<TestEntity> bulkWriter;

  public TestModule(String[] args) {
    super(args);
    initDefaults();
  }

  private void initDefaults() {
    setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
      }
    });
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
    bind(new TypeLiteral<JobModeService<DefaultJobMode>>() {
    }).to(TimestampDefaultJobModeService.class);
    bindJobModeImplementor();
    bind(new TypeLiteral<SavePointContainerService<TimestampSavePointContainer>>() {
    }).to(TimestampSavePointContainerService.class);
    bind(new TypeLiteral<SavePointService<TimestampSavePoint>>() {
    }).to(TimestampSavePointService.class);
    bind(new TypeLiteral<ChangedEntityService<TestEntity>>() {
    }).toInstance(changedEntityService);
    bind(
        new TypeLiteral<ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint>>() {
        }).toInstance(changedEntitiesIdentifiers);
    bind(new TypeLiteral<BulkWriter<TestEntity>>() {
    }).toInstance(bulkWriter);
  }

  private void bindJobModeImplementor() {
    Class<? extends JobModeImplementor<TestEntity, TimestampSavePoint>> clazz = null;

    TimestampDefaultJobModeService timestampDefaultJobModeService =
        new TimestampDefaultJobModeService();
    TimestampSavePointContainerService savePointContainerService =
        new TimestampSavePointContainerService(getJobOptions().getLastRunLoc());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    switch (timestampDefaultJobModeService.getCurrentJobMode()) {
      case INITIAL_LOAD:
        clazz = TestInitialModeImplementor.class;
        break;
      case INITIAL_LOAD_RESUME:
        clazz = TestInitialResumeModeImplementor.class;
        break;
      case INCREMENTAL_LOAD:
        clazz = TestIncrementalModeImplementor.class;
        break;
    }
    bind(new TypeLiteral<JobModeImplementor<TestEntity, TimestampSavePoint>>() {
    }).to(clazz);
  }

  private static class TestJobImpl extends JobImpl<TestEntity, TimestampSavePoint> {

  }

  private static class TestInitialModeImplementor extends
      TimestampInitialModeImplementor<TestEntity> {

  }

  private static class TestInitialResumeModeImplementor extends
      TimestampInitialResumeModeImplementor<TestEntity> {

  }

  private static class TestIncrementalModeImplementor extends
      TimestampIncrementalModeImplementor<TestEntity> {

  }

  public void setChangedEntityService(ChangedEntityService<TestEntity> changedEntityService) {
    this.changedEntityService = changedEntityService;
  }

  public void setChangedEntitiesIdentifiers(
      ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint> changedEntitiesIdentifiers) {
    this.changedEntitiesIdentifiers = changedEntitiesIdentifiers;
  }

  public void setBulkWriter(BulkWriter bulkWriter) {
    this.bulkWriter = bulkWriter;
  }

}
