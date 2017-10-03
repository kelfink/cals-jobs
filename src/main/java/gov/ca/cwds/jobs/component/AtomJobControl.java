package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Job control interface.
 * 
 * @author CWDS API Team
 * @see JobProgressTrack
 */
public interface AtomJobControl extends ApiMarker {

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

}
