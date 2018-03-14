package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessor;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessorImpl;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.impl.JobImpl;

import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class TestModule extends AbstractBaseJobModule {

    private ChangedIdentifiersService changedIdentifiersService;
    private ChangedEntitiesService changedEntitiesService;
    private Class<? extends JobBatchPreProcessor> jobBatchPreProcessorClass;
    private JobWriter jobWriter;

    public TestModule(String[] args) {
        super(args);
        initDefaults();
    }

    private void initDefaults() {
        changedIdentifiersService = new TestChangeIdentifiersService();
        changedEntitiesService = identifiers -> Stream.of(new Object());
        jobBatchPreProcessorClass = JobBatchPreProcessorImpl.class;
        jobWriter = items -> {};
    }

    @Override
    protected BaseJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
        TestJobConfiguration testJobConfiguration = new TestJobConfiguration();
        testJobConfiguration.setBatchSize(1);
        return testJobConfiguration;
    }

    @Override
    protected void configure() {
        super.setJobBatchPreProcessorClass(jobBatchPreProcessorClass);
        super.configure();
        bind(ChangedIdentifiersService.class).toInstance(changedIdentifiersService);
        bind(ChangedEntitiesService.class).toInstance(changedEntitiesService);
        bind(Job.class).to(JobImpl.class);
        bind(JobWriter.class).toInstance(jobWriter);
    }

    public void setChangedIdentifiersService(ChangedIdentifiersService changedIdentifiersService) {
        this.changedIdentifiersService = changedIdentifiersService;
    }

    public void setChangedEntitiesService(ChangedEntitiesService changedEntitiesService) {
        this.changedEntitiesService = changedEntitiesService;
    }

    public void setJobWriter(JobWriter jobWriter) {
        this.jobWriter = jobWriter;
    }

    public void setJobBatchPreProcessorClass(Class<? extends JobBatchPreProcessor> jobBatchPreProcessorClass) {
        this.jobBatchPreProcessorClass = jobBatchPreProcessorClass;
    }
}
