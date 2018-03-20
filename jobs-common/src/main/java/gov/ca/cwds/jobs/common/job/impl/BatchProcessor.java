package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;


public class BatchProcessor<T> {

  @Inject
  private ElasticSearchBulkCollector<T> elasticSearchBulkCollector;

  @Inject
  private BatchReadersPool<T> batchReadersPool;

  public void init() {
    batchReadersPool.init(elasticSearchBulkCollector);
  }

  public void process(JobBatch jobBatch) {
    batchReadersPool.loadEntities(jobBatch.getChangedEntityIdentifiers());
    elasticSearchBulkCollector.flush();
  }

  public void destroy() {
    batchReadersPool.destroy();
    elasticSearchBulkCollector.destroy();
  }

}