package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class CapUsersJobConfiguration extends BaseJobConfiguration {

  private DataSourceFactory cmsDataSourceFactory;

  @JsonProperty
  public DataSourceFactory getCmsDataSourceFactory() {
    return cmsDataSourceFactory;
  }

  public void setCmsDataSourceFactory(DataSourceFactory cmsDataSourceFactory) {
    this.cmsDataSourceFactory = cmsDataSourceFactory;
  }
}
