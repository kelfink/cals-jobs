package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessor;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessorImpl;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.elastic.ElasticUtils;
import gov.ca.cwds.jobs.common.job.timestamp.FilesystemTimestampOperator;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import org.elasticsearch.client.Client;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public abstract class AbstractBaseJobModule extends AbstractModule {

    private JobOptions jobOptions;

    private Class<? extends JobBatchPreProcessor> jobBatchPreProcessorClass = JobBatchPreProcessorImpl.class;

    public AbstractBaseJobModule(String[] args) {
        this.jobOptions = JobOptions.parseCommandLine(args);
    }

    public void setJobBatchPreProcessorClass(Class<? extends JobBatchPreProcessor> jobBatchPreProcessorClass) {
        this.jobBatchPreProcessorClass = jobBatchPreProcessorClass;
    }

    @Override
    protected void configure() {
        bind(JobOptions.class).toInstance(jobOptions);
        bindConstant().annotatedWith(LastRunDir.class).to(jobOptions.getLastRunLoc());
        bind(TimestampOperator.class).to(FilesystemTimestampOperator.class).asEagerSingleton();
        bind(JobBatchPreProcessor.class).to(jobBatchPreProcessorClass);
        bindConstant().annotatedWith(JobBatchSize.class).to(getJobsConfiguration(jobOptions).getBatchSize());
    }

    protected abstract BaseJobConfiguration getJobsConfiguration(JobOptions jobsOptions);

    @Provides
    @Inject
    public BaseJobConfiguration getBaseJobsConfiguration(JobOptions jobsOptions) {
        return getJobsConfiguration(jobsOptions);
    }

    @Provides
    @Inject
    // the client should not be closed here, it is closed when job is done
    @SuppressWarnings("squid:S2095")
    public Client elasticsearchClient(BaseJobConfiguration config) {
        return ElasticUtils.createAndConfigureESClient(config);
    }

    @Provides
    @Singleton
    @Inject
    public ElasticSearchIndexerDao provideElasticSearchDao(Client client,
                                                    BaseJobConfiguration configuration) {

        ElasticSearchIndexerDao esIndexerDao = new ElasticSearchIndexerDao(client,
                configuration);
        esIndexerDao.createIndexIfMissing();

        return esIndexerDao;
    }



}
