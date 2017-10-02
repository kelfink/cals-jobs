package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Common security features for Elasticsearch indexing jobs, especially legacy CMS.
 * 
 * @author CWDS API Team
 */
public interface JobAtomSecurity extends ApiMarker {

  /**
   * Determine if limited access records must be deleted from ES.
   * 
   * @return True if limited access records must be deleted from ES, false otherwise.
   */
  default boolean mustDeleteLimitedAccessRecords() {
    return false;
  }

}
