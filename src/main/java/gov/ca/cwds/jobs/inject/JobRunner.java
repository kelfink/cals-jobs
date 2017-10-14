package gov.ca.cwds.jobs.inject;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.MBeanExporter;
import org.weakref.jmx.Managed;

import com.google.inject.tools.jmx.Manager;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.NeutronDefaultJobSchedule;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Run standalone jobs or serve up jobs in "continuous" mode.
 * 
 * @author CWDS API Team
 */
public class JobRunner {

  public static final class NeutronJmxJob implements Serializable {

    private static final String GROUP_LAST_CHG = "last_chg";

    private final NeutronDefaultJobSchedule jobSchedule;
    private final String scheduleJobName;
    private final String scheduleTriggerName;

    public NeutronJmxJob(NeutronDefaultJobSchedule sched) {
      this.jobSchedule = sched;
      this.scheduleJobName = "job_" + jobSchedule.getName();
      this.scheduleTriggerName = "trg_" + jobSchedule.getName();
    }

    @Managed(description = "Run job now, show results immediately")
    public String run(String cmdLineArgs) throws NeutronException {
      try {
        LOGGER.info("RUN JOB: {}", jobSchedule.getName());
        final JobProgressTrack track = JobRunner.runRegisteredJob(jobSchedule.getKlazz(),
            StringUtils.isBlank(cmdLineArgs) ? null : cmdLineArgs.split("\\s+"));
        return track.toString();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        return "Job failed. Check the logs!";
      }
    }

    @Managed(description = "Schedule job on repeat")
    public void schedule() throws SchedulerException {
      final JobDetail jobDetail =
          newJob(NeutronScheduledJob.class).withIdentity(scheduleJobName, GROUP_LAST_CHG)
              .usingJobData("job_class", jobSchedule.getKlazz().getName()).build();
      final Trigger trigger = newTrigger().withIdentity(scheduleTriggerName, GROUP_LAST_CHG)
          .startNow()
          .withSchedule(simpleSchedule().withIntervalInSeconds(jobSchedule.getPeriodSeconds())
              .repeatForever())
          .startAt(DateTime.now().plusSeconds(jobSchedule.getStartDelaySeconds()).toDate()).build();

      scheduler.scheduleJob(jobDetail, trigger);
    }

    @Managed(description = "Unschedule job")
    public void unschedule() throws SchedulerException {
      LOGGER.warn("unschedule");
      final TriggerKey triggerKey = new TriggerKey(scheduleTriggerName, GROUP_LAST_CHG);
      scheduler.pauseTrigger(triggerKey);
    }

    @Managed(description = "Show job status")
    public void status() {
      LOGGER.debug("in progress 2");
    }

    @Managed(description = "Stop running job")
    public void stop() throws SchedulerException {
      LOGGER.warn("Stop running job");
      unschedule();

      final JobKey key = new JobKey(scheduleJobName, GROUP_LAST_CHG);
      scheduler.interrupt(key);
    }

  }

  public static class NeutronScheduledJob implements org.quartz.Job {

    public NeutronScheduledJob() {}

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      final String className = context.getJobDetail().getJobDataMap().getString("job_class");
      final String cmdLine = context.getJobDetail().getJobDataMap().getString("cmd_line");
      LOGGER.info("Executing {}", className);
      try {
        final JobProgressTrack track = JobRunner.runRegisteredJob(className,
            StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
        context.setResult(track);
      } catch (Exception e) {
        throw new JobExecutionException("SCHEDULED JOB FAILED!", e);
      }
    }

  }

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private static boolean testMode = false;

  /**
   * Run a single server for all last change jobs. Launch once, use many.
   */
  private static boolean continuousMode = false;

  private static JobOptions startingOpts;

  private static Scheduler scheduler;

  /**
   * Job options by job type.
   */
  private static final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  private static final Map<Class<?>, NeutronJmxJob> scheduleRegistry = new ConcurrentHashMap<>();

  private JobRunner() {
    // Default, no-op
  }

  @SuppressWarnings("unchecked")
  protected static void init(String[] args) throws Exception {
    // Expose job execution operations through JMX.
    final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());
    final DateFormat fmt =
        new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
    final Date now = new DateTime().minusHours(8).toDate(); // past window

    for (NeutronDefaultJobSchedule sched : NeutronDefaultJobSchedule.values()) {
      final Class<?> klass = sched.getKlazz();
      final JobOptions opts = new JobOptions(startingOpts);

      // Job's time file under base directory:
      final StringBuilder buf = new StringBuilder();
      buf.append(opts.getBaseDirectory()).append(File.separatorChar).append(sched.getName())
          .append(".time");
      opts.setLastRunLoc(buf.toString());

      // If timestamp file doesn't exist, create it.
      final File f = new File(opts.getLastRunLoc());
      if (!f.exists()) {
        if (opts.getLastRunTime() == null) {
          FileUtils.writeStringToFile(f, fmt.format(now));
        } else {
          FileUtils.writeStringToFile(f, fmt.format(opts.getLastRunTime()));
        }
      }

      JobRunner.registerContinuousJob((Class<? extends BasePersonIndexerJob<?, ?>>) klass, opts);

      final NeutronJmxJob nj = new NeutronJmxJob(sched);
      exporter.export("Neutron:last_run_jobs=" + sched.getName(), nj);
      scheduleRegistry.put(klass, nj);
    }

    // Expose Guice bean attributes through JMX.
    Manager.manage("Neutron_Guice", JobsGuiceInjector.getInjector());

    // Quartz scheduling:
    final Properties p = new Properties();
    p.put("org.quartz.scheduler.instanceName", "neutron");
    p.put("org.quartz.threadPool.threadCount", "4"); // NOTE: make configurable
    final StdSchedulerFactory factory = new StdSchedulerFactory(p);

    scheduler = factory.getScheduler();
    scheduler.start();

    // Start last change jobs.
    for (NeutronJmxJob j : scheduleRegistry.values()) {
      j.schedule();
    }
  }

  /**
   * Register a continuously running job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @param <T> Person persistence type
   * @throws NeutronException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void registerContinuousJob(
      final Class<T> klass, final JobOptions opts) throws NeutronException {
    LOGGER.info("Register job: {}", klass.getName());
    try (final T job = JobsGuiceInjector.newJob(klass, opts)) {
      optionsRegistry.put(klass, job.getOpts());
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception, for ClassNotFound or the like.
      throw JobLogs.buildCheckedException(LOGGER, e, "JOB REGISTRATION FAILED!: {}",
          e.getMessage());
    }
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
  public static BasePersonIndexerJob createJob(final Class<?> klass, String... args)
      throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());
      final JobOptions opts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : optionsRegistry.get(klass);
      final BasePersonIndexerJob<?, ?> job =
          (BasePersonIndexerJob<?, ?>) JobsGuiceInjector.getInjector().getInstance(klass);
      job.setOpts(opts);
      return job;
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!: {}",
          e.getMessage());
    }
  }

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
  public static JobProgressTrack runRegisteredJob(final Class<?> klass, String... args)
      throws NeutronException {
    try {
      LOGGER.info("Run registered job: {}", klass.getName());
      final BasePersonIndexerJob<?, ?> job = createJob(klass, args);
      job.run();
      return job.getTrack();
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "ON-DEMAND JOB RUN FAILED!: {}",
          e.getMessage());
    }
  }

  public static JobProgressTrack runRegisteredJob(final String jobName, String... args)
      throws NeutronException {
    try {
      final Class<?> klass = Class.forName(jobName);
      return runRegisteredJob(klass, args);
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "ON-DEMAND JOB RUN FAILED!: {}",
          e.getMessage());
    }
  }

  /**
   * Load all job definitions and continue running after a job completes.
   * 
   * @return true if running in continuous mode
   */
  public static boolean isContinuousMode() {
    return continuousMode;
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @return whether in test mode
   */
  public static boolean isTestMode() {
    return testMode;
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @param mode whether in test mode
   */
  public static void setTestMode(boolean mode) {
    testMode = mode;
  }

  /**
   * Entry point for standalone batch jobs, typically for initial load. Not used in continuous mode.
   * 
   * <p>
   * This method automatically closes the Hibernate session factory and ElasticSearch DAO and EXITs
   * the JVM.
   * </p>
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void runStandalone(final Class<T> klass,
      String... args) {
    int exitCode = 0;
    JobRunner.continuousMode = false;

    try (final T job = JobsGuiceInjector.newJob(klass, args)) {
      job.run();
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      exitCode = 1;
      throw JobLogs.buildException(LOGGER, e, "STANDALONE JOB FAILED!: {}", e.getMessage());
    } finally {
      // WARNING: kills the JVM in testing but may be needed to shutdown resources.
      if (!isTestMode() && !isContinuousMode()) {
        // Shutdown all remaining resources, even those not attached to this job.
        Runtime.getRuntime().exit(exitCode); // NOSONAR
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    LOGGER.info("STARTING ON-DEMAND JOBS SERVER ...");
    try {
      // OPTION: configure individual jobs, like Rundeck.
      // Best to load a configuration file with settings per job.

      // -c config/local.yaml -l /Users/CWS-NS3/client_indexer_time.txt


      JobRunner.startingOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : JobRunner.startingOpts;
      JobRunner.continuousMode = true;
      JobRunner.init(args);

      LOGGER.info("ON-DEMAND JOBS SERVER STARTED!");
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
  }

}
