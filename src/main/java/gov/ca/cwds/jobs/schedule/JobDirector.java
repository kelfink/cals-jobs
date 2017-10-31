package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

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
import gov.ca.cwds.jobs.component.AtomJobScheduler;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.listener.NeutronJobListener;
import gov.ca.cwds.jobs.listener.NeutronSchedulerListener;
import gov.ca.cwds.jobs.listener.NeutronTriggerListener;
import gov.ca.cwds.jobs.rest.NeutronRestServer;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Run stand-alone jobs or serve up jobs with Quartz. The master of ceremonies, AKA, Jimmy Neutron.
 * 
 * @author CWDS API Team
 */
public class JobDirector implements AtomJobScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobDirector.class);

  /**
   * Singleton instance. One director to rule them all.
   */
  private static final JobDirector instance = new JobDirector();

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private static boolean testMode = false;

  /**
   * Run a single server for all jobs. Launch one JVM, serve many jobs.
   */
  private static boolean continuousMode = false;

  /**
   * Launch one JVM, run initial load jobs sequentially, and exit.
   */
  private static boolean initialMode = false;

  /**
   * HACK: inject dependencies.
   */
  private JobOptions startingOpts;

  /**
   * Only used to drop and create indexes.
   * 
   * <p>
   * HACK: **move to another module**
   * </p>
   */
  private ElasticsearchDao esDao;

  /**
   * REST administration.
   */
  private NeutronRestServer restServer = new NeutronRestServer();

  private NeutronJobProgressHistory jobHistory = new NeutronJobProgressHistory();

  private NeutronScheduler neutronScheduler = new NeutronScheduler(jobHistory);

  private JobDirector() {
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

      final File f = new File(opts.getLastRunLoc()); // NOSONAR
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

      // Return String output to JMX or other interface.
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
    this.neutronScheduler.stopScheduler(waitForJobsToComplete);
  }

  @Managed(description = "Start the scheduler")
  public void startScheduler() throws NeutronException {
    this.neutronScheduler.startScheduler();
  }

  protected void configureQuartz() throws SchedulerException {
    // Quartz scheduling:
    final Properties p = new Properties();
    p.put("org.quartz.scheduler.instanceName", NeutronSchedulerConstants.SCHEDULER_INSTANCE_NAME);

    // NOTE: make configurable.
    p.put("org.quartz.threadPool.threadCount",
        initialMode ? "1" : NeutronSchedulerConstants.SCHEDULER_THREAD_COUNT);
    final StdSchedulerFactory factory = new StdSchedulerFactory(p);
    neutronScheduler.setScheduler(factory.getScheduler());

    // Scheduler listeners.
    final ListenerManager listenerMgr = neutronScheduler.getScheduler().getListenerManager();
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
    final File f = new File(timeFileLoc); // NOSONAR
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

        final NeutronJobMgtFacade nj =
            new NeutronJobMgtFacade(neutronScheduler.getScheduler(), sched, jobHistory);
        exporter.export("Neutron:last_run_jobs=" + sched.getName(), nj);
        neutronScheduler.getScheduleRegistry().put(klass, nj);
      }

      // Expose JobRunner methods to JMX.
      exporter.export("Neutron:runner=master", this);

      // Expose Guice bean attributes to JMX.
      Manager.manage("Neutron_Guice", JobsGuiceInjector.getInjector());

      // Start last change jobs.
      for (NeutronJobMgtFacade j : neutronScheduler.getScheduleRegistry().values()) {
        j.schedule();
      }

      // NOTE: move this responsibility to another class.
      if (startingOpts.isDropIndex()) {
        final ElasticsearchDao anEsDao = getInstance().esDao;
        anEsDao.deleteIndex(anEsDao.getConfig().getElasticsearchAlias());
      }

      // Start your engines ...
      if (!testMode) {
        neutronScheduler.getScheduler().start();
      }

      // Jetty for REST administration.
      Thread jettyServer = new Thread(restServer::run);
      jettyServer.start();

    } catch (IOException | SchedulerException | ParseException e) {
      try {
        neutronScheduler.getScheduler().shutdown(false);
      } catch (SchedulerException e2) {
        LOGGER.warn("FAILED TO STOP SCHEDULER! {}", e2.getMessage(), e2);
      }
      throw JobLogs.buildCheckedException(LOGGER, e, "INIT ERROR: {}", e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.jobs.schedule.AtomJobScheduler#registerJob(java.lang.Class,
   * gov.ca.cwds.jobs.config.JobOptions)
   */
  @Override
  public <T extends BasePersonIndexerJob<?, ?>> void registerJob(final Class<T> klass,
      final JobOptions opts) throws NeutronException {
    this.neutronScheduler.registerJob(klass, opts);
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
    return this.neutronScheduler.createJob(klass, args);
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
    return this.neutronScheduler.createJob(jobName, args);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.jobs.schedule.AtomJobScheduler#runScheduledJob(java.lang.Class,
   * java.lang.String)
   */
  @Override
  public JobProgressTrack runScheduledJob(final Class<?> klass, String... args)
      throws NeutronException {
    return this.neutronScheduler.runScheduledJob(klass, args);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.jobs.schedule.AtomJobScheduler#runScheduledJob(java.lang.String,
   * java.lang.String)
   */
  @Override
  public JobProgressTrack runScheduledJob(final String jobName, String... args)
      throws NeutronException {
    return this.runScheduledJob(jobName, args);
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
    JobDirector.continuousMode = false;

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

  public static boolean isInitialMode() {
    return initialMode;
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

  public boolean isJobVetoed(String className) throws NeutronException {
    Class<?> klazz = null;
    try {
      klazz = Class.forName(className);
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "UNKNOWN JOB CLASS! {}", className, e);
    }
    return neutronScheduler.getScheduleRegistry().get(klazz).isVetoExecution();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.jobs.schedule.AtomJobScheduler#scheduleJob(java.lang.Class,
   * gov.ca.cwds.jobs.schedule.NeutronDefaultJobSchedule)
   */
  @Override
  public NeutronJobMgtFacade scheduleJob(Class<?> klazz, NeutronDefaultJobSchedule sched) {
    return this.scheduleJob(klazz, sched);
  }

  protected static void startContinuousMode(String[] args) {
    LOGGER.info("STARTING ON-DEMAND JOBS SERVER ...");
    try {
      instance.startingOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : instance.startingOpts;
      JobDirector.continuousMode = true;
      JobDirector.initialMode = !instance.startingOpts.isLastRunMode();
      instance.initScheduler();

      LOGGER.info("ON-DEMAND JOBS SERVER STARTED!");
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
  }

  public NeutronJobProgressHistory getJobHistory() {
    return jobHistory;
  }

  public void setJobHistory(NeutronJobProgressHistory jobHistory) {
    this.jobHistory = jobHistory;
  }

  /**
   * <strong>FOR TESTING ONLY!</strong>
   * 
   * @param scheduler scheduler implementation
   */
  public void setScheduler(Scheduler scheduler) {
    this.neutronScheduler.setScheduler(scheduler);
  }

  public NeutronScheduler getNeutronScheduler() {
    return neutronScheduler;
  }

  public void setNeutronScheduler(NeutronScheduler neutronScheduler) {
    this.neutronScheduler = neutronScheduler;
  }

  public Scheduler getScheduler() {
    return neutronScheduler.getScheduler();
  }

  public Map<Class<?>, JobOptions> getOptionsRegistry() {
    return neutronScheduler.getOptionsRegistry();
  }

  public Map<Class<?>, NeutronJobMgtFacade> getScheduleRegistry() {
    return neutronScheduler.getScheduleRegistry();
  }

  @Override
  public void addExecutingJob(TriggerKey key, NeutronInterruptableJob job) {
    neutronScheduler.addExecutingJob(key, job);
  }

  public void removeExecutingJob(TriggerKey key) {
    neutronScheduler.removeExecutingJob(key);
  }

  public Map<TriggerKey, NeutronInterruptableJob> getExecutingJobs() {
    return neutronScheduler.getExecutingJobs();
  }

  /**
   * One scheduler to rule them all. And in the multi-threading ... bind them? :-)
   * 
   * @return evil single instance
   */
  public static JobDirector getInstance() {
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
    startContinuousMode(args);
  }

}
