package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CapUsersIncrementalJob extends AbstractCapUsersJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersIncrementalJob.class);

  @Inject
  @CmsSessionFactory
  private SessionFactory cmsSessionFactory;


  @Override
  void runJob() {
    LocalDateTime dateTimeAtStart = LocalDateTime.now();
    LOGGER.info("CapUsersIncrementalJob is running");
    batchProcessor.processBatches();
    LOGGER.info("finishing Incremental Cap Users Job, creating timestampSavePoint at {}", dateTimeAtStart);
    createSavePoint(dateTimeAtStart);
  }

  @Override
  public void close() {
    super.close();
    cmsSessionFactory.close();
  }
}
