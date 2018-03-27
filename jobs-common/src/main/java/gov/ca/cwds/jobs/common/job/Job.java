package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * @author CWDS API Team
 */
public interface Job extends ApiMarker {

  /**
   * Run the job.
   */
  void run();

  default void close() {
  }

}
