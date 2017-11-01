package gov.ca.cwds.jobs.component;

import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.jobs.schedule.NeutronInterruptableJob;
import gov.ca.cwds.jobs.schedule.LaunchPad;

public interface AtomLaunchScheduler {

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  FlightRecord runScheduledJob(Class<?> klass, JobOptions opts) throws NeutronException;

  void addExecutingJob(TriggerKey key, NeutronInterruptableJob job);

  LaunchPad scheduleJob(Class<?> klazz, DefaultFlightSchedule sched);

  boolean isJobVetoed(String className) throws NeutronException;

  FlightRecord runScheduledJob(String jobName, JobOptions opts) throws NeutronException;

}
