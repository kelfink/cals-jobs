package gov.ca.cwds.jobs.cals;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.cals.CalsApiConfiguration;
import gov.ca.cwds.rest.BaseApiConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 1/29/2018.
 */
public class TestCalsJobsConfiguration extends BaseApiConfiguration {

    private DataSourceFactory cmsrsDataSourceFactory;
    private DataSourceFactory fasDataSourceFactory;
    private DataSourceFactory lisDataSourceFactory;
    private DataSourceFactory calsnsDataSourceFactory;

    @JsonProperty
    public DataSourceFactory getCmsrsDataSourceFactory() {
        return cmsrsDataSourceFactory;
    }

    public void setCmsnsDataSourceFactory(DataSourceFactory cmsnsDataSourceFactory) {
        this.cmsrsDataSourceFactory = cmsnsDataSourceFactory;
    }

    public void setCmsrsDataSourceFactory(DataSourceFactory cmsrsDataSourceFactory) {
        this.cmsrsDataSourceFactory = cmsrsDataSourceFactory;
    }

    @JsonProperty
    public DataSourceFactory getFasDataSourceFactory() {
        return fasDataSourceFactory;
    }

    public void setFasDataSourceFactory(DataSourceFactory fasDataSourceFactory) {
        this.fasDataSourceFactory = fasDataSourceFactory;
    }

    @JsonProperty
    public DataSourceFactory getLisDataSourceFactory() {
        return lisDataSourceFactory;
    }

    public void setLisDataSourceFactory(DataSourceFactory lisDataSourceFactory) {
        this.lisDataSourceFactory = lisDataSourceFactory;
    }

    @JsonProperty
    public DataSourceFactory getCalsnsDataSourceFactory() {
        return calsnsDataSourceFactory;
    }

    public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
        this.calsnsDataSourceFactory = calsnsDataSourceFactory;
    }
}
