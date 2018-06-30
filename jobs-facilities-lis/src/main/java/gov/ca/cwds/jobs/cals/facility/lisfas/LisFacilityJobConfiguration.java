package gov.ca.cwds.jobs.cals.facility.lisfas;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 1/18/2018.
 */
public class LisFacilityJobConfiguration extends BaseFacilityJobConfiguration {

  private DataSourceFactory fasDataSourceFactory;

  private DataSourceFactory lisDataSourceFactory;

  @JsonProperty
  public DataSourceFactory getFasDataSourceFactory() {
    return fasDataSourceFactory;
  }

  @JsonProperty
  public DataSourceFactory getLisDataSourceFactory() {
    return lisDataSourceFactory;
  }

  public void setFasDataSourceFactory(DataSourceFactory fasDataSourceFactory) {
    this.fasDataSourceFactory = fasDataSourceFactory;
  }

  public void setLisDataSourceFactory(DataSourceFactory lisDataSourceFactory) {
    this.lisDataSourceFactory = lisDataSourceFactory;
  }

}
