package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LisTimestampSavePointService extends
    SavePointServiceImpl<LisTimestampSavePoint, DefaultJobMode> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LisTimestampSavePointService.class);

  @Inject
  private LisJobModeService jobModeService;

  @Inject
  private LisTimestampSavePointContainerService savePointContainerService;

  @Override
  public void saveSavePoint(LisTimestampSavePoint savePoint) {
    if (savePoint.getTimestamp() != null) {
      DefaultJobMode jobMode = jobModeService.getCurrentJobMode();
      LisTimestampSavePointContainer savePointContainer = new LisTimestampSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint(savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }
}
