package gov.ca.cwds.jobs.common.batch;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.timereport.JobTimeReport;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 4/2/2018.
 */
public class BatchProcessor<E, S extends SavePoint, J extends JobMode> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

  @Inject
  private ElasticSearchBulkCollector<E> elasticSearchBulkCollector;

  @Inject
  private BatchReadersPool<E, S, J> batchReadersPool;

  @Inject
  private JobModeImplementor<E, S, J> jobModeImplementor;

  public void init() {
    batchReadersPool.init(elasticSearchBulkCollector);
  }

  public void processBatches() {
    JobTimeReport jobTimeReport = new JobTimeReport();
    List<JobBatch<S>> portion = jobModeImplementor.getNextPortion();
    do {
      for (JobBatch<S> aPortion : portion) {
        batchReadersPool.loadEntities(aPortion.getChangedEntityIdentifiers());
      }
      if (!portion.isEmpty()) {
        handleLastBatchInPortion(portion.get(portion.size() - 1));
        portion = jobModeImplementor.getNextPortion();
      }

    } while (!portion.isEmpty());
    jobModeImplementor.finalizeJob();
    jobTimeReport.printTimeSpent();
  }

  private void handleLastBatchInPortion(JobBatch<S> lastJobBatchInPortion) {
    S savePoint = jobModeImplementor.defineSavepoint(lastJobBatchInPortion);
    LOGGER.info("Last batch in portion save point {}", savePoint);
    if (!JobExceptionHandler.isExceptionHappened()) {
      LOGGER.info("Save point has been reached. Batch save point is {}. Trying to save", savePoint);
      jobModeImplementor.saveSavePoint(savePoint);
      if (LOGGER.isInfoEnabled()) {
        //TODO  jobTimeReport.printTimeReport(portionBatchNumber);
      }
    } else {
      LOGGER.error("Exception occured during batch processing. Job has been terminated." +
          " Batch timestamp {} has not been recorded", savePoint);
      throw new JobsException("Exception occured during batch processing");
    }
  }

  public void destroy() {
    batchReadersPool.destroy();
    elasticSearchBulkCollector.destroy();
  }
}