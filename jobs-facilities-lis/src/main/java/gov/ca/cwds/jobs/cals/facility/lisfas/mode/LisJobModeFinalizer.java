package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainer;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointService;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 7/1/2018.
 */
public class LisJobModeFinalizer implements JobModeFinalizer {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LisJobModeFinalizer.class);

  @Inject
  private LisTimestampSavePointService savePointService;

  @Inject
  private SavePointContainerService<TimestampSavePoint<BigInteger>, DefaultJobMode> savePointContainerService;

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Override
  public void doFinalizeJob() {
    LisTimestampSavePoint lisTimestampSavePoint = new LisTimestampSavePoint(
        changedEntitiesIdentifiersService.findMaxTimestamp());
    LOGGER.info("Updating job save point to the last batch save point {}", lisTimestampSavePoint);
    DefaultJobMode nextJobMode = DefaultJobMode.INCREMENTAL_LOAD;
    LOGGER.info("Updating next job mode to the {}", nextJobMode);
    LisTimestampSavePointContainer savePointContainer = new LisTimestampSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(lisTimestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }
}
