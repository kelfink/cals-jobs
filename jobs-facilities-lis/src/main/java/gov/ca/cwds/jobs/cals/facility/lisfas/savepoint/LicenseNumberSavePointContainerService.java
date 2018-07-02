package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerServiceImpl;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LicenseNumberSavePointContainerService extends
    SavePointContainerServiceImpl<LicenseNumberSavePoint, DefaultJobMode> {

  @Inject
  public LicenseNumberSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }
}
