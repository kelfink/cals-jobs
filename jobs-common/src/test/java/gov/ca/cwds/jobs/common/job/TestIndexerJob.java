package gov.ca.cwds.jobs.common.job;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class TestIndexerJob extends BaseIndexerJob {

    private Job job;

    public TestIndexerJob(Job job) {
        this.job = job;
    }

    @Override
    protected AbstractModule getJobModule(JobOptions jobOptions) {
        return new AbstractBaseJobModule(jobOptions) {

            @Override
            protected BaseJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
                return new TestJobConfiguration();
            }

            @Provides
            public Job provideJob() {
                return job;
            }

        };
    }

}
