package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Marker interface for Neutron Rockets.
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
   * Launch the rocket.
   */
  @Override
  public void run();

}
