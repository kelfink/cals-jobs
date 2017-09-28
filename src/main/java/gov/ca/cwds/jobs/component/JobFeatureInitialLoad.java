package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobFeatureInitialLoad extends ApiMarker {

  /**
   * @return true if the job provides its own key ranges
   */
  default boolean providesInitialKeyRanges() {
    return false;
  }


}
