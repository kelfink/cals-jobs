package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;

/**
 * Created by Alexander Serbin on 6/21/2018.
 */
public class TimestampSavePointContainerService extends
    SavePointContainerServiceImpl<TimestampSavePoint, DefaultJobMode> {

  @Inject
  public TimestampSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

}
