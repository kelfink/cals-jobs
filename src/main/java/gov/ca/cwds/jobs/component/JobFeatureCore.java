package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobFeatureCore extends ApiMarker {

  /**
   * Transform (normalize) in the Job instead of relying on the transformation thread.
   * 
   * @return true if the transformer thread should run
   */
  default boolean useTransformThread() {
    return true;
  }

  /**
   * @return true if the job provides its own key ranges
   */
  default boolean isRangeSelfManaging() {
    return false;
  }

  /**
   * Determine if limited access records must be deleted from ES.
   * 
   * @return True if limited access records must be deleted from ES, false otherwise.
   */
  default boolean mustDeleteLimitedAccessRecords() {
    return false;
  }



}
