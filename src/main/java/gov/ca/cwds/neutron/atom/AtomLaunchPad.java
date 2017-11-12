package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.StandardFlightSchedule;

public interface AtomLaunchPad extends ApiMarker {

  /**
   * Action: run immediately.
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

  String status();

  String history();

  /**
   * Display this rocket's logs.
   * 
   * @return logs
   */
  String logs();

  /**
   * Aborts a flying rocket.
   * 
   * @throws NeutronException general error
   */
  void stop() throws NeutronException;

  /**
   * Abort rockets, shutdown command, and exit.
   * 
   * @throws NeutronException general error
   */
  void shutdown() throws NeutronException;


  FlightPlan getFlightPlan();

  void setFlightPlan(FlightPlan flightPlan);


  StandardFlightSchedule getFlightSchedule();

  FlightRecorder getFlightRecorder();


  boolean isVetoExecution();

  void setVetoExecution(boolean vetoExecution);

}
