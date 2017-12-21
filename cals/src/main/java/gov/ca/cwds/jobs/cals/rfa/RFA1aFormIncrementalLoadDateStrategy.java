package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.cals.BaseIncrementalLoadDateStrategy;

/**
 * @author CWDS TPT-2
 */
public final class RFA1aFormIncrementalLoadDateStrategy extends BaseIncrementalLoadDateStrategy {

  private static final String RUNNING_FILE_NAME = "CALS_RFA1aForm_last_load_time";

  @Override
  protected String getDateFileName() {
    return RUNNING_FILE_NAME;
  }
}
