package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Scheduler;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;

public class NeutronScheduler implements AtomJobScheduler {

  private Scheduler scheduler;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  /**
   * Scheduled jobs.
   */
  private final Map<Class<?>, NeutronJobMgtFacade> scheduleRegistry = new ConcurrentHashMap<>();

  private final Map<TriggerKey, NeutronInterruptableJob> executingJobs = new ConcurrentHashMap<>();

  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Map<Class<?>, JobOptions> getOptionsRegistry() {
    return optionsRegistry;
  }

  public Map<Class<?>, NeutronJobMgtFacade> getScheduleRegistry() {
    return scheduleRegistry;
  }

  @Override
  public <T extends BasePersonIndexerJob<?, ?>> void registerJob(Class<T> klass, JobOptions opts)
      throws NeutronException {
    // IMPL ME!
  }

  @Override
  public JobProgressTrack runScheduledJob(Class<?> klass, String... args) throws NeutronException {
    return null;
  }

  @Override
  public JobProgressTrack runScheduledJob(String jobName, String... args) throws NeutronException {
    return null;
  }

  @Override
  public NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched) {
    return null;
  }

  @Override
  public void addExecutingJob(final TriggerKey key, NeutronInterruptableJob job) {
    executingJobs.put(key, job);
  }

  public void removeExecutingJob(final TriggerKey key) {
    if (executingJobs.containsKey(key)) {
      executingJobs.remove(key);
    }
  }

  public Map<TriggerKey, NeutronInterruptableJob> getExecutingJobs() {
    return executingJobs;
  }

}
