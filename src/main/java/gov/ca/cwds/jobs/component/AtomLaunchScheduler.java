package gov.ca.cwds.jobs.component;

import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.jobs.schedule.LaunchPad;
import gov.ca.cwds.jobs.schedule.NeutronRocket;

public interface AtomLaunchScheduler {

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  FlightRecord runScheduledJob(Class<?> klass, FlightPlan opts) throws NeutronException;

  void addExecutingJob(TriggerKey key, NeutronRocket job);

  LaunchPad scheduleJob(Class<?> klazz, DefaultFlightSchedule sched, FlightPlan opts);

  boolean isJobVetoed(String className) throws NeutronException;

  FlightRecord runScheduledJob(String jobName, FlightPlan opts) throws NeutronException;

}
