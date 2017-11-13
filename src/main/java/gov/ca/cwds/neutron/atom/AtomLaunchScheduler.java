package gov.ca.cwds.neutron.atom;

import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.NeutronRocket;
import gov.ca.cwds.jobs.schedule.StandardFlightSchedule;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.launch.LaunchPad;

public interface AtomLaunchScheduler {

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  FlightLog launchScheduledFlight(Class<?> klass, FlightPlan opts) throws NeutronException;

  FlightLog launchScheduledFlight(String jobName, FlightPlan opts) throws NeutronException;

  void trackInFlightRocket(TriggerKey key, NeutronRocket rocket);

  LaunchPad scheduleLaunch(Class<?> klazz, StandardFlightSchedule sched, FlightPlan opts);

  boolean isLaunchVetoed(String className) throws NeutronException;

  void stopScheduler(boolean waitForJobsToComplete) throws NeutronException;

  void startScheduler() throws NeutronException;

  Map<Class<?>, AtomLaunchPad> getScheduleRegistry();

  Scheduler getScheduler();

  AtomFlightPlanManager getFlightPlanManger();

  AtomFlightRecorder getFlightRecorder();

}
