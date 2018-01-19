package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.cals.CalsJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 1/18/2018.
 */
public class FacilityJobConfiguration extends CalsJobConfiguration {

    private DataSourceFactory fasDataSourceFactory;

    private DataSourceFactory lisDataSourceFactory;

    private DataSourceFactory cmsDataSourceFactory;

    @JsonProperty
    public DataSourceFactory getFasDataSourceFactory() {
        return fasDataSourceFactory;
    }

    @JsonProperty
    public DataSourceFactory getLisDataSourceFactory() {
        return lisDataSourceFactory;
    }

    @JsonProperty
    public DataSourceFactory getCmsDataSourceFactory() {
        return cmsDataSourceFactory;
    }

    public void setFasDataSourceFactory(DataSourceFactory fasDataSourceFactory) {
        this.fasDataSourceFactory = fasDataSourceFactory;
    }

    public void setLisDataSourceFactory(DataSourceFactory lisDataSourceFactory) {
        this.lisDataSourceFactory = lisDataSourceFactory;
    }

    public void setCmsDataSourceFactory(DataSourceFactory cmsDataSourceFactory) {
        this.cmsDataSourceFactory = cmsDataSourceFactory;
    }

}
