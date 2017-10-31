package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomJobCreator;
import gov.ca.cwds.jobs.component.AtomJobScheduler;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.util.JobLogs;

public class NeutronScheduler implements AtomJobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobDirector.class);

  private Scheduler scheduler;

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private boolean testMode = false;

  private final NeutronJobProgressHistory jobHistory;

  private AtomJobCreator jobCreator;

  private JobOptions opts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  /**
   * Scheduled jobs.
   */
  private final Map<Class<?>, NeutronJobMgtFacade> scheduleRegistry = new ConcurrentHashMap<>();

  private final Map<TriggerKey, NeutronInterruptableJob> executingJobs = new ConcurrentHashMap<>();

  @Inject
  public NeutronScheduler(final NeutronJobProgressHistory jobHistory) {
    this.jobHistory = jobHistory;
  }

  /**
   * Build a registered job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createJob(final Class<?> klass, String... args)
      throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());
      final JobOptions jobOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : getOptionsRegistry().get(klass);

      if (this.opts == null) { // HACK: **inject dependencies**
        this.opts = jobOpts;
      }

      final BasePersonIndexerJob<?, ?> job =
          (BasePersonIndexerJob<?, ?>) JobsGuiceInjector.getInjector().getInstance(klass);
      job.setOpts(jobOpts);
      return job;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!: {}", e.getMessage());
    }
  }

  /**
   * Build a registered job.
   * 
   * @param jobName batch job class
   * @param args command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createJob(final String jobName, String... args)
      throws NeutronException {
    try {
      return createJob(Class.forName(jobName), args);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!!: {}", e.getMessage());
    }
  }

  @Override
  public <T extends BasePersonIndexerJob<?, ?>> void registerJob(Class<T> klass, JobOptions opts)
      throws NeutronException {
    LOGGER.info("Register job: {}", klass.getName());
    if (!testMode) {
      try (final T job = JobsGuiceInjector.newJob(klass, opts)) {
        getOptionsRegistry().put(klass, job.getOpts());
        JobDirector.getInstance().setEsDao(job.getEsDao()); // MORE: **inject** dependencies.
      } catch (Throwable e) { // NOSONAR
        // Intentionally catch a Throwable, not an Exception or ClassNotFound or the like.
        throw JobLogs.checked(LOGGER, e, "JOB REGISTRATION FAILED!: {}", e.getMessage());
      }
    }
  }

  @Override
  public JobProgressTrack runScheduledJob(Class<?> klass, String... args) throws NeutronException {
    try {
      LOGGER.info("Run registered job: {}", klass.getName());
      final BasePersonIndexerJob<?, ?> job = createJob(klass, args);
      job.run();
      return job.getTrack();
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "ON-DEMAND JOB RUN FAILED!: {}", e.getMessage());
    }
  }

  @Override
  public JobProgressTrack runScheduledJob(String jobName, String... args) throws NeutronException {
    try {
      final Class<?> klass = Class.forName(jobName);
      return runScheduledJob(klass, args);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "ON-DEMAND JOB RUN FAILED!: {}", e.getMessage());
    }
  }

  @Override
  public NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched) {
    final NeutronJobMgtFacade nj = new NeutronJobMgtFacade(this.getScheduler(), sched, jobHistory);
    this.getScheduleRegistry().put(klazz, nj);
    return nj;
  }

  public void stopScheduler(boolean waitForJobsToComplete) throws NeutronException {
    LOGGER.warn("STOP SCHEDULER! wait for jobs to complete: {}", waitForJobsToComplete);
    try {
      this.getScheduler().shutdown(waitForJobsToComplete);
    } catch (SchedulerException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO STOP SCHEDULER! {}", e.getMessage());
    }
  }

  public void startScheduler() throws NeutronException {
    LOGGER.warn("START SCHEDULER!");
    try {
      this.getScheduler().start();
    } catch (SchedulerException e) {
      LOGGER.error("FAILED TO START SCHEDULER! {}", e.getMessage(), e);
      throw JobLogs.checked(LOGGER, e, "FAILED TO START SCHEDULER! {}", e.getMessage());
    }
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

  public AtomJobCreator getJobCreator() {
    return jobCreator;
  }

  public void setJobCreator(AtomJobCreator jobCreator) {
    this.jobCreator = jobCreator;
  }

  public JobOptions getOpts() {
    return opts;
  }

  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

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

}
