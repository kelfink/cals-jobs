package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobAtomMisc extends ApiMarker {

  /**
   * Determine if limited access records must be deleted from ES.
   * 
   * @return True if limited access records must be deleted from ES, false otherwise.
   */
  default boolean mustDeleteLimitedAccessRecords() {
    return false;
  }

}
