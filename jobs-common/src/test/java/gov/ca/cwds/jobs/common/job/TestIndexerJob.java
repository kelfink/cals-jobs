package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public class TestIndexerJob extends BaseIndexerJob {

    @Override
    protected ElasticsearchConfiguration getJobsConfiguration() {
        return new ElasticsearchConfiguration();
    }

    @Override
    public void run(String[] args) {
        super.run(args);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(BaseJobConfiguration.class).toInstance(new BaseJobConfiguration() {
            @Override
            public DataSourceFactory getCalsnsDataSourceFactory() {
                return new DataSourceFactory();
            }
        });

    }

}
