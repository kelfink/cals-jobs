package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CapUsersInitialJob extends AbstractCapUsersJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersInitialJob.class);

  @Inject
  CapUsersBatchProcessor batchProcessor;

  @Override
  void runJob() {
    LocalDateTime dateTimeAtStart = LocalDateTime.now();
    LOGGER.info("CapUsersInitialJob is running");
    batchProcessor.processBatches();
    LOGGER.info("Initial Cap Users Job, creating timestampSavePoint at {}", dateTimeAtStart);
    createSavePoint(dateTimeAtStart);
  }

  @Override
  public void close() {
    batchProcessor.destroy();
    super.close();
  }
}
