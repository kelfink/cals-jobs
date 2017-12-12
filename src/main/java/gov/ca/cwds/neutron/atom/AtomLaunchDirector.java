package gov.ca.cwds.neutron.atom;

import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.launch.NeutronRocket;
import gov.ca.cwds.neutron.launch.StandardFlightSchedule;

public interface AtomLaunchDirector {

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

  void prepareLaunchPads();

  void markRocketAsInFlight(TriggerKey key, NeutronRocket rocket);

  AtomLaunchPad scheduleLaunch(StandardFlightSchedule sched, FlightPlan flightPlan)
      throws NeutronException;

  boolean isLaunchVetoed(String className) throws NeutronException;

  void stopScheduler(boolean waitForJobsToComplete) throws NeutronException;

  void startScheduler() throws NeutronException;

  Scheduler getScheduler();

  Map<Class<?>, AtomLaunchPad> getLaunchPads();

  AtomFlightPlanManager getFlightPlanManger();

  AtomFlightRecorder getFlightRecorder();

}
