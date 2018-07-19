package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CapUsersLocalDateTimeJobFinalizer implements JobModeFinalizer {
  private static final Logger LOGGER = LoggerFactory
          .getLogger(CapUsersLocalDateTimeJobFinalizer.class);

  @Inject
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode> savePointContainerService;

  @Override
  public void doFinalizeJob() {
    LocalDateTimeSavePoint timestampSavePoint = new LocalDateTimeSavePoint(LocalDateTime.now());
    DefaultJobMode nextJobMode = DefaultJobMode.INCREMENTAL_LOAD;
    LOGGER.info("finalizing Initial Cap Users Job, creating timestampSavePoint at {}", timestampSavePoint);
    LocalDateTimeSavePointContainer savePointContainer = new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(timestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }
}
