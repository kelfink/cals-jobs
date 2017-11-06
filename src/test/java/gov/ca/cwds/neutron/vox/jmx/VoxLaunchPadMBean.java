package gov.ca.cwds.neutron.vox.jmx;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.jobs.schedule.FlightRecorder;

public interface VoxLaunchPadMBean {

  String run(String cmdLine) throws NeutronException;

  void schedule() throws NeutronException;

  void unschedule() throws NeutronException;

  void stop() throws NeutronException;

  String status();

  String history();

  FlightPlan getFlightPlan();

  void setFlightPlan(FlightPlan flightPlan);

  DefaultFlightSchedule getFlightSchedule();

  FlightRecorder getFlightRecorder();

  String getJobName();

  String getTriggerName();

  JobDetail getJd();

  JobKey getJobKey();

}
