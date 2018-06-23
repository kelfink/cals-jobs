package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.api.JobModeService;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public class TimestampSavePointService implements SavePointService<TimestampSavePoint> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(TimestampSavePointService.class);

  @Inject
  private TimestampSavePointContainerService savePointContainerService;

  @Inject
  private JobModeService<DefaultJobMode> jobModeService;

  @Override
  public TimestampSavePoint loadSavePoint() {
    return savePointContainerService.readSavePointContainer().getSavePoint();
  }

  @Override
  public TimestampSavePoint defineSavepoint(JobBatch<TimestampSavePoint> jobBatch) {
    return jobBatch.getChangedEntityIdentifiers()
        .get(jobBatch.getChangedEntityIdentifiers().size() - 1).getSavePoint();
  }

  @Override
  public void saveSavePoint(TimestampSavePoint savePoint) {
    if (savePoint.getTimestamp() != null) {
      DefaultJobMode jobMode = jobModeService.getCurrentJobMode();
      TimestampSavePointContainer savePointContainer = new TimestampSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint(savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

}
