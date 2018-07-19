package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.timereport.JobTimeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersInitialJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersInitialJob.class);

  @Inject
  private CapUsersBatchProcessor batchProcessor;

  @Inject
  private CapUsersLocalDateTimeJobFinalizer jobModeFinalizer;

  @Override
  public void run() {
    JobTimeReport jobTimeReport = new JobTimeReport();
    LOGGER.info("CapUsersInitialJob running");
    batchProcessor.processBatches();
    if (LOGGER.isInfoEnabled()) {
      jobTimeReport.printTimeSpent();
    }
  }

  @Override
  public void close() {
    batchProcessor.destroy();
    jobModeFinalizer.doFinalizeJob();
  }
}
