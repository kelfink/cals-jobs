package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.BatchProcessor;
import gov.ca.cwds.jobs.common.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersJob.class);

  @Inject
  private CapUsersBatchProcessor batchProcessor;

  @Override
  public void run() {
    LOGGER.info("Runing");
    batchProcessor.processBatches();

  }
}
