package gov.ca.cwds.neutron.atom;

import org.quartz.spi.JobFactory;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;

public interface AtomRocketFactory extends JobFactory {

  /**
   * Build a registered rocket.
   * 
   * @param klass rocket class
   * @param opts command line arguments
   * @return the rocket
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  BasePersonRocket createJob(final Class<?> klass, final FlightPlan opts)
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
  public BasePersonRocket createJob(final String rocketName, final FlightPlan flightPlan)
      throws NeutronException;

}