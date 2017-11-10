package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.StandardFlightSchedule;
import gov.ca.cwds.jobs.schedule.FlightRecorder;


public interface AtomLaunchPad extends ApiMarker {

  /**
   * Action: run immediately.
   * 
   * @param cmdLine command line
   * @return JXM output
   * @throws NeutronException on error
   */
  String run(String cmdLine) throws NeutronException;

  void schedule() throws NeutronException;

  void unschedule() throws NeutronException;

  String status();

  String history();

  String logs();

  void stop() throws NeutronException;


  boolean isVetoExecution();

  void setVetoExecution(boolean vetoExecution);



  FlightPlan getFlightPlan();

  void setFlightPlan(FlightPlan flightPlan);


  StandardFlightSchedule getFlightSchedule();

  FlightRecorder getFlightRecorder();

}
