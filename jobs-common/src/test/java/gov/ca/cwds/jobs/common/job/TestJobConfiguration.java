package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class TestJobConfiguration extends BaseJobConfiguration {

    @Override
    public DataSourceFactory getCalsnsDataSourceFactory() {
        return new DataSourceFactory();
    }

}
