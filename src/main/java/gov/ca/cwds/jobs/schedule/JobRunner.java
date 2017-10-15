package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.MBeanExporter;

import com.google.inject.tools.jmx.Manager;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Run standalone jobs or serve up jobs in "continuous" mode.
 * 
 * @author CWDS API Team
 */
public class JobRunner {

  public static final String GROUP_LAST_CHG = "last_chg";

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  /**
   * Singleton instance. One scheduler to rule them all.
   */
  private static final JobRunner instance = new JobRunner();

  static Scheduler scheduler;

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

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  private final Map<Class<?>, NeutronJmxFacade> scheduleRegistry = new ConcurrentHashMap<>();

  private JobRunner() {
    // Default, no-op
  }

  /**
   * Expose job execution operations through JMX.
   * 
   * @throws NeutronException on initialization error
   */
  @SuppressWarnings("unchecked")
  protected static void init() throws NeutronException {
    try {
      // Quartz scheduling:
      final Properties p = new Properties();
      p.put("org.quartz.scheduler.instanceName", "neutron");
      p.put("org.quartz.threadPool.threadCount", "4"); // NOTE: make configurable
      final StdSchedulerFactory factory = new StdSchedulerFactory(p);

      scheduler = factory.getScheduler();
      final ListenerManager listenerManager = scheduler.getListenerManager();
      listenerManager.addSchedulerListener(new NeutronSchedulerListener());
      listenerManager.addJobListener(new NeutronJobListener());

      // JMX:
      final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());
      final DateFormat fmt =
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
      final Date now = new DateTime().minusHours(2).toDate(); // NOTE: make configurable

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
        if (!f.exists() && opts.getLastRunTime() == null) {
          FileUtils.writeStringToFile(f, fmt.format(now));
        } else if (!f.exists() && opts.getLastRunTime() != null) {
          FileUtils.writeStringToFile(f, fmt.format(opts.getLastRunTime()));
        }

        JobRunner.registerContinuousJob((Class<? extends BasePersonIndexerJob<?, ?>>) klass, opts);

        final NeutronJmxFacade nj = new NeutronJmxFacade(scheduler, sched);
        exporter.export("Neutron:last_run_jobs=" + sched.getName(), nj);
        instance.scheduleRegistry.put(klass, nj);
      }

      // Expose Guice bean attributes through JMX.
      Manager.manage("Neutron_Guice", JobsGuiceInjector.getInjector());

      scheduler.start();

      // Start last change jobs.
      for (NeutronJmxFacade j : instance.scheduleRegistry.values()) {
        j.schedule();
      }
    } catch (IOException | SchedulerException e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "INIT ERROR: {}", e.getMessage());
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
      instance.optionsRegistry.put(klass, job.getOpts());
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
          : instance.optionsRegistry.get(klass);
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
   * Build a registered job.
   * 
   * @param jobName batch job class
   * @param args command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public static BasePersonIndexerJob createJob(final String jobName, String... args)
      throws NeutronException {
    try {
      final Class<?> klass = Class.forName(jobName);
      return createJob(klass, args);
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!!: {}",
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

  /**
   * Run a registered job.
   * 
   * @param jobName batch job class
   * @param args command line arguments
   * @return job progress
   * @throws NeutronException unexpected runtime error
   */
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

  /**
   * One scheduler to rule them all. And in the multi-threading ... bind them? :-)
   * 
   * @return evil single instance
   */
  public static JobRunner getInstance() {
    return instance;
  }

  /**
   * OPTION: configure individual jobs, like Rundeck.
   * <p>
   * Best to load a configuration file with settings per job.
   * </p>
   * 
   * @param args command line
   */
  public static void main(String[] args) {
    LOGGER.info("STARTING ON-DEMAND JOBS SERVER ...");
    try {
      JobRunner.startingOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : JobRunner.startingOpts;
      JobRunner.continuousMode = true;
      JobRunner.init();

      LOGGER.info("ON-DEMAND JOBS SERVER STARTED!");
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
  }

}
