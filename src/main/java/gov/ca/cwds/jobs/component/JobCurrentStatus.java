package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobCurrentStatus extends ApiMarker {

  boolean isFailed();

  boolean isIndexDone();

  boolean isRetrieveDone();

  boolean isRunning();

  boolean isTransformDone();

  void markFailed();

  void markIndexDone();

  void markJobDone();

  void markRetrieveDone();

  void markTransformDone();

  // JobExecutionStatus status();

}
