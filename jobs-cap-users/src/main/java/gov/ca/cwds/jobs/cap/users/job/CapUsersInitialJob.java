package gov.ca.cwds.jobs.cap.users.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CapUsersInitialJob extends AbstractCapUsersJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersInitialJob.class);

  @Override
  void runJob() {
    LocalDateTime dateTimeAtStart = LocalDateTime.now();
    LOGGER.info("CapUsersInitialJob is running");
    batchProcessor.processBatches();
    LOGGER.info("Initial Cap Users Job, creating timestampSavePoint at {}", dateTimeAtStart);
    createSavePoint(dateTimeAtStart);
  }
}
