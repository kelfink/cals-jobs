package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
class TestJobConfiguration extends BaseJobConfiguration {

  public TestJobConfiguration() {
    setBatchSize(1);
    setReaderThreadsCount(1);
    setElasticSearchBulkSize(1);
  }
}
