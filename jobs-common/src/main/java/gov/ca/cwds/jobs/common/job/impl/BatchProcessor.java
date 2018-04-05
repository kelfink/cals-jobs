package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchIterator;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 4/2/2018.
 */
public class BatchProcessor<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

  @Inject
  private ElasticSearchBulkCollector<T> elasticSearchBulkCollector;

  @Inject
  private BatchReadersPool<T> batchReadersPool;

  @Inject
  private JobBatchIterator batchIterator;

  @Inject
  private TimestampOperator timestampOperator;

  public void init() {
    batchIterator.init();
    batchReadersPool.init(elasticSearchBulkCollector);
  }

  public void processBatches() {
    //JobTimeReport jobTimeReport = new JobTimeReport(jobBatches);
    List<JobBatch> portion = batchIterator.getNextPortion();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Portion size = {}", portion.size());
      portion.forEach(jobBatch ->
          LOGGER.info("Batch size = {}, batch timestamp = {}", jobBatch.getSize(),
              jobBatch.getTimestamp()));
    }
    do {
      for (int portionBatchNumber = 0; portionBatchNumber < portion.size(); portionBatchNumber++) {
        processBatch(portion.get(portionBatchNumber));
        if (!JobExceptionHandler.isExceptionHappened()) {
          timestampOperator.writeTimestamp(portion.get(portionBatchNumber).getTimestamp());
          if (LOGGER.isInfoEnabled()) {
//          jobTimeReport.printTimeReport(portionBatchNumber);
          }
          if (!portion.get(portionBatchNumber).isEmptyTimestamp()) {
            LOGGER.info("Save point has been reached. Save point batch timestamp is " + portion
                .get(portionBatchNumber).getTimestamp());
          }
        } else {
          LOGGER.error("Exception occured during batch processing. Job has been terminated." +
              " Batch timestamp " + portion.get(portionBatchNumber).getTimestamp()
              + "has not been recorded");
          throw new RuntimeException("Exception occured during batch processing");
        }
      }
      portion = batchIterator.getNextPortion();
    } while (!portion.isEmpty());

    //   jobTimeReport.printTimeSpent();
  }

  private void processBatch(JobBatch jobBatch) {
    batchReadersPool.loadEntities(jobBatch.getChangedEntityIdentifiers());
    elasticSearchBulkCollector.flush();
  }

  public void destroy() {
    batchReadersPool.destroy();
    elasticSearchBulkCollector.destroy();
  }
}