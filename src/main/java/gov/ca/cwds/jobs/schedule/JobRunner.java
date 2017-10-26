package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.MBeanExporter;
import org.weakref.jmx.Managed;

import com.google.inject.tools.jmx.Manager;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.rest.NeutronRestServer;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Run standalone jobs or serve up jobs with Quartz.
 * 
 * @author CWDS API Team
 */
public class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  /**
   * Singleton instance. One scheduler to rule them all.
   */
  private static final JobRunner instance = new JobRunner();

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private static boolean testMode = false;

  /**
   * Run a single server for all jobs. Launch one JVM, serve many jobs.
   */
  private static boolean continuousMode = false;

  private static boolean initialMode = false;

  private Scheduler scheduler;

  private JobOptions startingOpts;

  private ElasticsearchDao esDao;

  private NeutronRestServer restServer = new NeutronRestServer();

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  private final Map<Class<?>, NeutronJobMgtFacade> scheduleRegistry = new ConcurrentHashMap<>();

  private final Map<TriggerKey, NeutronInterruptableJob> executingJobs = new ConcurrentHashMap<>();

  private final Map<Class<?>, List<JobProgressTrack>> trackHistory = new ConcurrentHashMap<>();

  private final Map<Class<?>, JobProgressTrack> lastTracks = new ConcurrentHashMap<>();

  // CircularFifoQueue

  private JobRunner() {
    // Default, no-op
  }

  protected void resetTimestamps(boolean initialMode, int hoursInPast) throws IOException {
    final DateFormat fmt =
        new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
    final Date now = new DateTime().minusHours(initialMode ? 876000 : hoursInPast).toDate();

    for (NeutronDefaultJobSchedule sched : NeutronDefaultJobSchedule.values()) {
      final JobOptions opts = new JobOptions(startingOpts);

      // Find the job's time file under the base directory:
      final StringBuilder buf = new StringBuilder();
      buf.append(opts.getBaseDirectory()).append(File.separatorChar).append(sched.getName())
          .append(".time");
      opts.setLastRunLoc(buf.toString());

      final File f = new File(opts.getLastRunLoc());
      final boolean fileExists = f.exists();

      if (!fileExists) {
        FileUtils.writeStringToFile(f, fmt.format(now));
      }
    }
  }

  @Managed(description = "Reset for initial load.")
  public String resetTimestampsForInitialLoad() {
    LOGGER.warn("RESET TIMESTAMPS FOR INITIAL LOAD!");
    try {
      resetTimestamps(true, 0);
    } catch (IOException e) {
      LOGGER.error("FAILED TO RESET TIMESTAMPS! {}", e.getMessage(), e);
      final StringWriter sw = new StringWriter();
      final PrintWriter w = new PrintWriter(sw);
      e.printStackTrace(w);
      return sw.toString();
    }

    return "Timestamps reset for initial load";
  }

  @Managed(description = "Reset for last change.")
  public void resetTimestampsForLastChange(int hoursInPast) throws IOException {
    LOGGER.warn("RESET TIMESTAMPS FOR LAST CHANGE! hours in past: {}", hoursInPast);
    resetTimestamps(false, hoursInPast);
  }

  @Managed(description = "Stop the scheduler")
  public void stopScheduler(boolean waitForJobsToComplete) throws NeutronException {
    LOGGER.warn("STOP SCHEDULER! wait for jobs to complete: {}", waitForJobsToComplete);
    try {
      scheduler.shutdown(waitForJobsToComplete);
    } catch (SchedulerException e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "FAILED TO STOP SCHEDULER! {}",
          e.getMessage());
    }
  }

  @Managed(description = "Start the scheduler")
  public void startScheduler() throws NeutronException {
    LOGGER.warn("START SCHEDULER!");
    try {
      scheduler.start();
    } catch (SchedulerException e) {
      LOGGER.error("FAILED TO START SCHEDULER! {}", e.getMessage(), e);
      throw JobLogs.buildCheckedException(LOGGER, e, "FAILED TO START SCHEDULER! {}",
          e.getMessage());
    }
  }

  protected void configureQuartz() throws SchedulerException {
    // Quartz scheduling:
    final Properties p = new Properties();
    p.put("org.quartz.scheduler.instanceName", NeutronSchedulerConstants.SCHEDULER_INSTANCE_NAME);

    // NOTE: make configurable.
    p.put("org.quartz.threadPool.threadCount",
        initialMode ? "1" : NeutronSchedulerConstants.SCHEDULER_THREAD_COUNT);
    final StdSchedulerFactory factory = new StdSchedulerFactory(p);
    scheduler = factory.getScheduler();

    // Scheduler listeners.
    final ListenerManager listenerMgr = scheduler.getListenerManager();
    listenerMgr.addSchedulerListener(new NeutronSchedulerListener());
    listenerMgr.addTriggerListener(new NeutronTriggerListener());
    listenerMgr.addJobListener(initialMode ? NeutronDefaultJobSchedule.fullLoadJobChainListener()
        : new NeutronJobListener());
  }

  protected void handleTimeFile(final JobOptions opts, final DateFormat fmt, final Date now,
      NeutronDefaultJobSchedule sched) throws IOException {
    // Find the job's time file under the base directory:
    final StringBuilder buf = new StringBuilder();
    buf.append(opts.getBaseDirectory()).append(File.separatorChar).append(sched.getName())
        .append(".time");
    final String timeFileLoc = buf.toString();
    opts.setLastRunLoc(timeFileLoc);

    // If timestamp file doesn't exist, create it.
    final File f = new File(timeFileLoc);
    final boolean fileExists = f.exists();
    final boolean overrideLastRunTime = opts.getLastRunTime() != null;

    if (!fileExists || initialMode) {
      FileUtils.writeStringToFile(f, fmt.format(overrideLastRunTime ? opts.getLastRunTime() : now));
    }
  }

  protected void configureInitialMode(final Date now) {
    if (initialMode) {
      startingOpts.setLastRunTime(now);
      startingOpts.setLastRunMode(false);
      LOGGER.warn("\n\n\n\n>>>>>>> INITIAL, FULL LOAD! <<<<<<<\n\n\n\n");
    }
  }

  /**
   * Expose job execution operations through JMX.
   * 
   * @throws NeutronException on initialization error
   */
  @SuppressWarnings("unchecked")
  protected void initScheduler() throws NeutronException {
    try {
      configureQuartz();

      // JMX:
      final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());

      // NOTE: make last change window configurable.
      final DateFormat fmt =
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
      final Date now = initialMode ? fmt.parse("1917-10-31 10:11:12.000")
          : new DateTime().minusHours(NeutronSchedulerConstants.LAST_CHG_WINDOW_HOURS).toDate();

      configureInitialMode(now);

      // Schedule jobs.
      for (NeutronDefaultJobSchedule sched : NeutronDefaultJobSchedule.values()) {
        final Class<?> klass = sched.getKlazz();
        final JobOptions opts = new JobOptions(startingOpts);

        handleTimeFile(opts, fmt, now, sched);

        if (!testMode) {
          registerJob((Class<? extends BasePersonIndexerJob<?, ?>>) klass, opts);
        }

        final NeutronJobMgtFacade nj = new NeutronJobMgtFacade(scheduler, sched);
        exporter.export("Neutron:last_run_jobs=" + sched.getName(), nj);
        scheduleRegistry.put(klass, nj);
        trackHistory.put(sched.getKlazz(), new ArrayList<JobProgressTrack>());
      }

      exporter.export("Neutron:runner=master", this);

      // Expose Guice bean attributes through JMX.
      Manager.manage("Neutron_Guice", JobsGuiceInjector.getInjector());

      // Start last change jobs.
      for (NeutronJobMgtFacade j : scheduleRegistry.values()) {
        j.schedule();
      }

      if (startingOpts.isDropIndex()) {
        final ElasticsearchDao anEsDao = getInstance().esDao;
        anEsDao.deleteIndex(anEsDao.getConfig().getElasticsearchAlias());
      }

      // Start your engines ...
      if (!testMode) {
        scheduler.start();
      }

      // Jetty for REST administration.
      Thread jettyServer = new Thread(restServer::run);
      jettyServer.start();

    } catch (IOException | SchedulerException | ParseException e) {
      try {
        scheduler.shutdown(false);
      } catch (SchedulerException e2) {
        LOGGER.warn("FAILED TO STOP SCHEDULER! {}", e2.getMessage(), e2);
      }
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
  public <T extends BasePersonIndexerJob<?, ?>> void registerJob(final Class<T> klass,
      final JobOptions opts) throws NeutronException {
    LOGGER.info("Register job: {}", klass.getName());
    if (!testMode) {
      try (final T job = JobsGuiceInjector.newJob(klass, opts)) {
        optionsRegistry.put(klass, job.getOpts());
        getInstance().setEsDao(job.getEsDao());
      } catch (Throwable e) { // NOSONAR
        // Intentionally catch a Throwable, not an Exception or ClassNotFound or the like.
        throw JobLogs.buildCheckedException(LOGGER, e, "JOB REGISTRATION FAILED!: {}",
            e.getMessage());
      }
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
  public BasePersonIndexerJob createJob(final Class<?> klass, String... args)
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
  public JobProgressTrack runScheduledJob(final Class<?> klass, String... args)
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
  public JobProgressTrack runScheduledJob(final String jobName, String... args)
      throws NeutronException {
    try {
      final Class<?> klass = Class.forName(jobName);
      return runScheduledJob(klass, args);
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
  public static boolean isSchedulerMode() {
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
      throw JobLogs.buildRuntimeException(LOGGER, e, "STANDALONE JOB FAILED!: {}", e.getMessage());
    } finally {
      // WARNING: kills the JVM in testing but may be needed to shutdown resources.
      if (!isTestMode() && !isSchedulerMode()) {
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

  public static boolean isInitialMode() {
    return initialMode;
  }

  public Map<Class<?>, List<JobProgressTrack>> getTrackHistory() {
    return trackHistory;
  }

  public void addTrack(Class<?> klazz, JobProgressTrack track) {
    lastTracks.put(klazz, track);

    if (!trackHistory.containsKey(klazz)) {
      trackHistory.put(klazz, new ArrayList<>());
    }
    trackHistory.get(klazz).add(track);
  }

  public ElasticsearchDao getEsDao() {
    return esDao;
  }

  public void setEsDao(ElasticsearchDao esDao) {
    if (this.esDao == null) {
      this.esDao = esDao;
    }
  }

  public JobOptions getStartingOpts() {
    return startingOpts;
  }

  public void setStartingOpts(JobOptions startingOpts) {
    this.startingOpts = startingOpts;
  }

  public Map<Class<?>, JobProgressTrack> getLastTracks() {
    return lastTracks;
  }

  public JobProgressTrack getLastTrack(final Class<?> klazz) {
    return lastTracks.get(klazz);
  }

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

  public boolean isJobVetoed(String className) throws NeutronException {
    Class<?> klazz = null;
    try {
      klazz = Class.forName(className);
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "UNKNOWN JOB CLASS! {}", className, e);
    }
    return scheduleRegistry.get(klazz).isVetoExecution();
  }

  public NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched) {
    final NeutronJobMgtFacade nj = new NeutronJobMgtFacade(scheduler, sched);
    scheduleRegistry.put(klazz, nj);
    return nj;
  }

  protected static void startContinuousMode(String[] args) {
    LOGGER.info("STARTING ON-DEMAND JOBS SERVER ...");
    try {
      instance.startingOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : instance.startingOpts;
      JobRunner.continuousMode = true;
      JobRunner.initialMode = !instance.startingOpts.isLastRunMode();
      instance.initScheduler();

      LOGGER.info("ON-DEMAND JOBS SERVER STARTED!");
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
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
    startContinuousMode(args);
  }

}
