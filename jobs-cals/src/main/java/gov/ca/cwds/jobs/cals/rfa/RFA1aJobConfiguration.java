package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 2/4/2018.
 */
public class RFA1aJobConfiguration extends BaseJobConfiguration {

  private DataSourceFactory calsnsDataSourceFactory;

  public DataSourceFactory getCalsnsDataSourceFactory() {
    return calsnsDataSourceFactory;
  }

  public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
    this.calsnsDataSourceFactory = calsnsDataSourceFactory;
  }

}
