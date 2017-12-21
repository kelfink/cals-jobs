package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.cals.BaseIncrementalLoadDateStrategy;

import java.time.LocalDateTime;

/**
 * @author CWDS TPT-2
 */
public final class FacilityIncrementalLoadDateStrategy extends BaseIncrementalLoadDateStrategy {

  private static final String RUNNING_FILE_NAME = "CALS_Facility_last_load_time";

  @Override
  protected String getDateFileName() {
    return RUNNING_FILE_NAME;
  }

  @Override
  protected LocalDateTime getDateTimeForInitialLoad() {
    return null;
  }
}
