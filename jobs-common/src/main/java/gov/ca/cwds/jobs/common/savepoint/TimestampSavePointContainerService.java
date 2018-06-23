package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.LastRunDir;

/**
 * Created by Alexander Serbin on 6/21/2018.
 */
public class TimestampSavePointContainerService extends
    SavePointContainerServiceImpl<TimestampSavePointContainer> {

  @Inject
  public TimestampSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

}
