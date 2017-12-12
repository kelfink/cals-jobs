package gov.ca.cwds.neutron.atom;

import org.quartz.spi.JobFactory;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;

public interface AtomRocketFactory extends JobFactory {

  /**
   * Build a registered rocket.
   * 
   * @param klass rocket class
   * @param flightPlan command line arguments
   * @return the rocket
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  BasePersonRocket fuelRocket(final Class<?> klass, final FlightPlan flightPlan)
      throws NeutronException;

  /**
   * Build a registered rocket.
   * 
   * @param rocketName rocket class
   * @param flightPlan command line arguments
   * @return the rocket
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonRocket fuelRocket(final String rocketName, final FlightPlan flightPlan)
      throws NeutronException;

}
