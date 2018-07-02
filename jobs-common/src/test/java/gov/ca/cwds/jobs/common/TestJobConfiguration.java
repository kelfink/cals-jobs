package gov.ca.cwds.jobs.common;

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
