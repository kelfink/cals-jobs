package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.impl.JobImpl;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class TestModule extends AbstractBaseJobModule {

    private JobReader jobReader;
    private JobWriter jobWriter;

    public TestModule(String[] args, JobReader jobReader, JobWriter jobWriter) {
        super(args);
        this.jobReader = jobReader;
        this.jobWriter = jobWriter;
    }

    @Override
    protected BaseJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
        return new TestJobConfiguration();
    }

    @Override
    protected void configure() {
            super.configure();
            bind(Job.class).toInstance(new JobImpl());
            bind(JobReader.class).toInstance(jobReader);
            bind(JobWriter.class).toInstance(jobWriter);
    }
}
