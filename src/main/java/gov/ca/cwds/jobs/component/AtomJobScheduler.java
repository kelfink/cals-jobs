package gov.ca.cwds.jobs.component;

import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.NeutronDefaultJobSchedule;
import gov.ca.cwds.jobs.schedule.NeutronInterruptableJob;
import gov.ca.cwds.jobs.schedule.NeutronJobMgtFacade;

public interface AtomJobScheduler {

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

  NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched);

  boolean isJobVetoed(String className) throws NeutronException;

  FlightRecord runScheduledJob(String jobName, JobOptions opts) throws NeutronException;

}
