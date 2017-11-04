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
  FlightRecord launchScheduledFlight(Class<?> klass, FlightPlan opts) throws NeutronException;

  FlightRecord launchScheduled(String jobName, FlightPlan opts) throws NeutronException;

  void trackInFlightRocket(TriggerKey key, NeutronRocket rocket);

  LaunchPad scheduleLaunch(Class<?> klazz, DefaultFlightSchedule sched, FlightPlan opts);

  boolean isLaunchVetoed(String className) throws NeutronException;

}
