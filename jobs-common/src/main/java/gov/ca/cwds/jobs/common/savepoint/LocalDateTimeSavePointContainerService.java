package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 6/21/2018.
 */
public class LocalDateTimeSavePointContainerService extends
    SavePointContainerServiceImpl<TimestampSavePoint<LocalDateTime>, DefaultJobMode> {

  @Inject
  public LocalDateTimeSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

}
