package gov.ca.cwds.jobs;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Marker interface for batch Jobs.
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface Job extends ApiMarker {

  /**
   * Run the job.
   */
  public void run();

}
