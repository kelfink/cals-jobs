package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Marker interface for batch Jobs.
 * 
 * <p>
 * In Jimmy Neutron's world, "jobs" are "rockets."
 * </p>
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface Rocket extends ApiMarker, Runnable {

  /**
   * Run the job.
   */
  @Override
  public void run();

}
