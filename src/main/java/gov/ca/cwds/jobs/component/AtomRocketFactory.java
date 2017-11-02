package gov.ca.cwds.jobs.component;

import org.quartz.spi.JobFactory;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;

public interface AtomRocketFactory extends JobFactory {

  /**
   * Build a registered job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  BasePersonIndexerJob createJob(final Class<?> klass, final FlightPlan opts)
      throws NeutronException;

  /**
   * Build a registered job.
   * 
   * @param jobName batch job class
   * @param opts command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createJob(final String jobName, final FlightPlan opts)
      throws NeutronException;

}
