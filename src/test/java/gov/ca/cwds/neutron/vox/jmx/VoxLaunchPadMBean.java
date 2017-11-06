package gov.ca.cwds.neutron.vox.jmx;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.jobs.schedule.FlightRecorder;

public interface VoxLaunchPadMBean {

  String run(String cmdLine);

  void schedule();

  void unschedule();

  void vetoScheduledJob();

  String status();

  String history();

  void stop();

  FlightPlan getFlightPlan();

  void setFlightPlan(FlightPlan flightPlan);

  DefaultFlightSchedule getFlightSchedule();

  FlightRecorder getFlightRecorder();

  String getJobName();

  String getTriggerName();

  JobDetail getJd();

  JobKey getJobKey();

  boolean isVetoExecution();

  void setVetoExecution(boolean vetoExecution);

}
