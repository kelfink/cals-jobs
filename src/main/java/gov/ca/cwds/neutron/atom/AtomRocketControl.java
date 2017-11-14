package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.neutron.flight.FlightLog;

/**
 * Job control interface.
 * 
 * @author CWDS API Team
 * @see FlightLog
 */
public interface AtomRocketControl extends ApiMarker {

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
  void fail();

  /**
   * Mark ES indexing done.
   */
  void doneIndex();

  /**
   * Mark the job done. Working threads should stop themselves.
   */
  void done();

  /**
   * Mark the retrieval step done.
   */
  void doneRetrieve();

  /**
   * Mark the normalize/transform step done.
   */
  void doneTransform();

}
