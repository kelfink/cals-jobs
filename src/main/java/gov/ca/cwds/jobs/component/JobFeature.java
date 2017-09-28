package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobFeature extends ApiMarker {

  /**
   * Transform (normalize) in the Job instead of relying on the transformation thread.
   * 
   * @return true if the transformer thread should run
   */
  default boolean useTransformThread() {
    return true;
  }


}
