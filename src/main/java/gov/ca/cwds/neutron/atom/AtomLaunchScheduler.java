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
   * Launch a registered rocket.
   * 
   * @param klass rocket class
   * @param flightPlan command line arguments
   * @return rocket flight progress
   * @throws NeutronException unexpected runtime error
   */
  FlightLog launch(Class<?> klass, FlightPlan flightPlan) throws NeutronException;

  FlightLog launch(String jobName, FlightPlan flightPlan) throws NeutronException;

  void markRocketAsInFlight(TriggerKey key, NeutronRocket rocket);

  LaunchPad scheduleLaunch(Class<?> klazz, StandardFlightSchedule sched, FlightPlan flightPlan);

  boolean isLaunchVetoed(String className) throws NeutronException;

  void stopScheduler(boolean waitForJobsToComplete) throws NeutronException;

  void startScheduler() throws NeutronException;

  Scheduler getScheduler();

  Map<Class<?>, AtomLaunchPad> getLaunchPads();

  AtomFlightPlanManager getFlightPlanManger();

  AtomFlightRecorder getFlightRecorder();

}
