package gov.ca.cwds.jobs.component;

import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.NeutronDefaultJobSchedule;
import gov.ca.cwds.jobs.schedule.NeutronInterruptableJob;
import gov.ca.cwds.jobs.schedule.NeutronJobMgtFacade;

public interface AtomJobScheduler {

  /**
   * Register a continuously running job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @param <T> Person persistence type
   * @throws NeutronException unexpected runtime error
   */
  <T extends BasePersonIndexerJob<?, ?>> void registerJob(Class<T> klass, JobOptions opts)
      throws NeutronException;

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  FlightRecord runScheduledJob(Class<?> klass, String... args) throws NeutronException;

  /**
   * Run a registered job.
   * 
   * @param jobName batch job class
   * @param args command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  FlightRecord runScheduledJob(String jobName, String... args) throws NeutronException;

  void addExecutingJob(TriggerKey key, NeutronInterruptableJob job);

  NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched);

}
