package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.JobPreparator;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobImpl<T> implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);

  @Inject
  private TimestampOperator timestampOperator;

  @Inject
  private BatchProcessor<T> batchProcessor;

  @Inject
  private JobPreparator jobPreparator;

  @Override
  public void run() {
    try {
      jobPreparator.run();
      batchProcessor.init();
      batchProcessor.processBatches();
      LocalDateTime now = LocalDateTime.now();
      timestampOperator.writeTimestamp(now);
      LOGGER.info("Updating job timestamp to the current moment {}", now);
      LOGGER.info(String
          .format("Added %s entities to the Elastic Search index", ConsumerCounter.getCounter()));
    } catch (RuntimeException e) {
      LOGGER.error("ERROR: ", e.getMessage(), e);
    } finally {
      JobExceptionHandler.reset();
      close();
    }
  }

  @Override
  public void close() {
    batchProcessor.destroy();
  }
}
