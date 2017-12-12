package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.launch.StandardFlightSchedule;

public interface AtomLaunchPad extends ApiMarker {

  /**
   * Action: launch immediately and wait synchronously for results.
   * 
   * @param cmdLine command line
   * @return JXM output
   * @throws NeutronException on error
   */
  String run(String cmdLine) throws NeutronException;

  // ==============
  // COMMANDS:
  // ==============

  void schedule() throws NeutronException;

  void unschedule() throws NeutronException;

  /**
   * Show last flight status.
   * 
   * @return last flight status
   */
  String status();

  /**
   * Show history of last N flights.
   * 
   * @return flight history
   */
  String history();

  /**
   * Display this rocket's logs.
   * 
   * @return logs
   */
  String logs();

  /**
   * Abort a rocket <strong>in flight</strong>.
   * 
   * @throws NeutronException general error
   */
  void stop() throws NeutronException;

  /**
   * Pause a rocket's schedule.
   * 
   * @throws NeutronException general error
   */
  void pause() throws NeutronException;

  /**
   * Resume a rocket's schedule.
   * 
   * @throws NeutronException general error
   */
  void resume() throws NeutronException;

  /**
   * Abort all rockets, shutdown command center, and exit JVM.
   * 
   * @throws NeutronException general error
   */
  void shutdown() throws NeutronException;


  FlightPlan getFlightPlan();

  void setFlightPlan(FlightPlan flightPlan);


  StandardFlightSchedule getFlightSchedule();

  AtomFlightRecorder getFlightRecorder();


  boolean isVetoExecution();

  void setVetoExecution(boolean vetoExecution);

}
