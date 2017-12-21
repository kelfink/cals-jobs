package gov.ca.cwds.generic.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Job control interface.
 * 
 * @author CWDS API Team
 * @see JobProgressTrack
 */
public interface AtomJobControl extends ApiMarker {

  /**
   * Is the job still running?
   * 
   * @return true if job has not completed
   */
  boolean isRunning();

  /**
   * Did the job failed?
   * 
   * @return true if Job has failed
   */
  boolean isFailed();

  /**
   * Has Elasticsearch indexing step finished?
   * 
   * @return true if indexing has completed
   */
  boolean isIndexDone();

  /**
   * Has the normalization/transformation step finished?
   * 
   * @return true if normalization has completed
   */
  boolean isTransformDone();

  /**
   * Has the retrieval step finished?
   * 
   * @return true if retrieval has completed
   */
  boolean isRetrieveDone();

  /**
   * Mark the job as failed and stop the job. Working threads should stop themselves.
   */
  void markFailed();

  /**
   * Mark ES indexing done.
   */
  void markIndexDone();

  /**
   * Mark the job done. Working threads should stop themselves.
   */
  void markJobDone();

  /**
   * Mark the retrieval step done.
   */
  void markRetrieveDone();

  /**
   * Mark the normalize/transform step done.
   */
  void markTransformDone();

}
