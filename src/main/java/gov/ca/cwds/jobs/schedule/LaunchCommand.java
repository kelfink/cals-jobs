package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.io.IOException;
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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.tools.jmx.Manager;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomFlightRecorder;
import gov.ca.cwds.jobs.component.AtomLaunchScheduler;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.HyperCube;
import gov.ca.cwds.jobs.listener.NeutronJobListener;
import gov.ca.cwds.jobs.listener.NeutronSchedulerListener;
import gov.ca.cwds.jobs.listener.NeutronTriggerListener;
import gov.ca.cwds.jobs.manage.rest.NeutronRestServer;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Run stand-alone jobs or serve up jobs with Quartz. The master of ceremonies, AKA, Jimmy Neutron.
 * 
 * @author CWDS API Team
 */
public class LaunchCommand implements AtomLaunchScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchCommand.class);

  /**
   * Singleton instance. One director to rule them all.
   */
  private static LaunchCommand instance = new LaunchCommand();

  private LaunchCenterSettings settings = new LaunchCenterSettings();

  /**
   * HACK: inject dependencies.
   */
  private FlightPlan startingOpts;

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

  private FlightRecorder flightRecorder = new FlightRecorder();

  private LaunchScheduler neutronScheduler;

  private LaunchCommand() {
    // no-op
  }

  @Inject
  public LaunchCommand(final FlightRecorder jobHistory, final LaunchScheduler neutronScheduler,
      final ElasticsearchDao esDao) {
    this.flightRecorder = jobHistory;
    this.neutronScheduler = neutronScheduler;
    this.esDao = esDao;
  }

  protected void resetTimestamps(boolean initialMode, int hoursInPast) throws IOException {
    final DateFormat fmt =
        new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
    final Date now = new DateTime().minusHours(initialMode ? 876000 : hoursInPast).toDate();

    for (DefaultFlightSchedule sched : DefaultFlightSchedule.values()) {
      final FlightPlan opts = new FlightPlan(startingOpts);

      // Find the job's time file under the base directory:
      final StringBuilder buf = new StringBuilder();
      buf.append(opts.getBaseDirectory()).append(File.separatorChar).append(sched.getShortName())
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
      return JobLogs.stackToString(e);
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

  /**
   * Configure Quartz scheduling.
   * 
   * <p>
   * MORE: inject this dependency.
   * </p>
   * 
   * @param injector Guice injector
   * @return prepare launch scheduler
   * @throws SchedulerException Quartz error
   */
  protected LaunchScheduler configureQuartz(final Injector injector) throws SchedulerException {
    final Properties p = new Properties();
    p.put("org.quartz.scheduler.instanceName", NeutronSchedulerConstants.SCHEDULER_INSTANCE_NAME);

    // MORE: make configurable.
    p.put("org.quartz.threadPool.threadCount",
        instance.settings.isInitialMode() ? "1" : NeutronSchedulerConstants.SCHEDULER_THREAD_COUNT);
    final StdSchedulerFactory factory = new StdSchedulerFactory(p);
    final Scheduler scheduler = factory.getScheduler();

    // MORE: inject scheduler and rocket factory.
    scheduler.setJobFactory(injector.getInstance(RocketFactory.class));
    neutronScheduler.setScheduler(scheduler);

    // Scheduler listeners.
    final ListenerManager listenerMgr = neutronScheduler.getScheduler().getListenerManager();
    listenerMgr.addSchedulerListener(new NeutronSchedulerListener());
    listenerMgr.addTriggerListener(new NeutronTriggerListener(neutronScheduler));
    listenerMgr.addJobListener(instance.settings.isInitialMode()
        ? DefaultFlightSchedule.buildFullLoadJobChainListener() : new NeutronJobListener());
    return neutronScheduler;
  }

  /**
   * Find the job's time file under the base directory.
   * 
   * @param opts base options
   * @param fmt reusable date format
   * @param now current datetime
   * @param sched this schedule
   * @throws IOException on file error
   */
  protected void handleTimeFile(final FlightPlan opts, final DateFormat fmt, final Date now,
      final DefaultFlightSchedule sched) throws IOException {
    final StringBuilder buf = new StringBuilder();

    buf.append(opts.getBaseDirectory()).append(File.separatorChar).append(sched.getShortName())
        .append(".time");
    final String timeFileLoc =
        buf.toString().replaceAll(File.separator + File.separator, File.separator);
    opts.setLastRunLoc(timeFileLoc);
    LOGGER.warn("base directory: {}, job name: {}, last run loc: {}", opts.getBaseDirectory(),
        sched.getShortName(), opts.getLastRunLoc());

    // If timestamp file doesn't exist, create it.
    final File f = new File(timeFileLoc); // NOSONAR
    final boolean fileExists = f.exists();
    final boolean overrideLastRunTime = opts.getOverrideLastRunTime() != null;

    if (!fileExists || settings.isInitialMode()) {
      FileUtils.writeStringToFile(f,
          fmt.format(overrideLastRunTime ? opts.getOverrideLastRunTime() : now));
    }
  }

  protected void configureInitialMode(final Date now) {
    if (settings.isInitialMode()) {
      startingOpts.setOverrideLastRunTime(now);
      startingOpts.setLastRunMode(false);
      LOGGER.warn("\n\n\n\n>>>>>>> INITIAL, FULL LOAD! <<<<<<<\n\n\n\n");
    }
  }

  /**
   * Too many responsibilities: initialize Quartz, register jobs, expose operations to JMX, even
   * initialize HTTP ...
   * 
   * @param injector Guice injector. Soon to be removed.
   * @throws NeutronException on initialization error
   */
  protected void initScheduler(final Injector injector) throws NeutronException {
    try {
      configureQuartz(injector);

      // JMX:
      final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());

      // NOTE: make last change window configurable.
      final DateFormat fmt =
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
      final Date now = settings.isInitialMode() ? fmt.parse("1917-10-31 10:11:12.000")
          : new DateTime().minusHours(NeutronSchedulerConstants.LAST_CHG_WINDOW_HOURS).toDate();

      configureInitialMode(now);

      // Schedule jobs.
      for (DefaultFlightSchedule sched : DefaultFlightSchedule.values()) {
        final Class<?> klass = sched.getKlazz();
        final FlightPlan opts = new FlightPlan(startingOpts);
        handleTimeFile(opts, fmt, now, sched);

        final LaunchPad nj =
            new LaunchPad(neutronScheduler.getScheduler(), sched, flightRecorder, opts);
        neutronScheduler.getScheduleRegistry().put(klass, nj);
        neutronScheduler.getRocketOptions().addFlightSettings(klass, opts);
        exporter.export("Neutron:last_run_jobs=" + sched.getShortName(), nj);
      }

      // Expose JobRunner methods to JMX.
      exporter.export("Neutron:runner=master", this);

      // Expose Guice bean attributes to JMX.
      Manager.manage("Neutron_Guice", HyperCube.getInjector());

      // MOVE: move this responsibility to another class.
      if (startingOpts.isDropIndex()) {
        esDao.deleteIndex(esDao.getConfig().getElasticsearchAlias());
      }

      // Start last change jobs.
      for (LaunchPad j : neutronScheduler.getScheduleRegistry().values()) {
        j.schedule();
      }

      // Start your engines ...
      if (!this.settings.isTestMode()) {
        LOGGER.warn("start scheduler ...");
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
      throw JobLogs.checked(LOGGER, e, "INIT ERROR: {}", e.getMessage());
    }
  }

  /**
   * Build a registered job.
   * 
   * @param klass batch job class
   * @param opts command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createJob(final Class<?> klass, final FlightPlan opts)
      throws NeutronException {
    return this.neutronScheduler.createJob(klass, opts);
  }

  /**
   * Build a registered job.
   * 
   * @param jobName batch job class
   * @param opts command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createJob(final String jobName, final FlightPlan opts)
      throws NeutronException {
    final BasePersonIndexerJob ret = this.neutronScheduler.createJob(jobName, opts);
    ret.setOpts(opts);

    LOGGER.warn("CREATE JOB: {}", opts.getLastRunLoc());
    return ret;
  }

  @Override
  public FlightRecord runScheduledJob(final Class<?> klass, final FlightPlan opts)
      throws NeutronException {
    return this.neutronScheduler.runScheduledJob(klass, opts);
  }

  @Override
  public FlightRecord runScheduledJob(final String jobName, final FlightPlan opts)
      throws NeutronException {
    return this.neutronScheduler.runScheduledJob(jobName, opts);
  }

  /**
   * Load all job definitions and continue running after a job completes.
   * 
   * @return true if running in continuous mode
   */
  public static boolean isSchedulerMode() {
    return getInstance().settings.isContinuousMode();
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @return whether in test mode
   */
  public static boolean isTestMode() {
    return getInstance().settings.isTestMode();
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @param mode whether in test mode
   */
  public static void setTestMode(boolean mode) {
    getInstance().settings.setTestMode(mode);
  }

  public static boolean isInitialMode() {
    return getInstance().settings.isInitialMode();
  }

  public ElasticsearchDao getEsDao() {
    return esDao;
  }

  public void setEsDao(ElasticsearchDao esDao) {
    if (this.esDao == null) {
      this.esDao = esDao;
    }
  }

  public FlightPlan getStartingOpts() {
    return startingOpts;
  }

  public void setStartingOpts(FlightPlan startingOpts) {
    this.startingOpts = startingOpts;
  }

  @Override
  public boolean isJobVetoed(String className) throws NeutronException {
    return this.neutronScheduler.isJobVetoed(className);
  }

  @Override
  public LaunchPad scheduleJob(Class<?> klazz, DefaultFlightSchedule sched, FlightPlan opts) {
    return this.neutronScheduler.scheduleJob(klazz, sched, opts);
  }

  public AtomFlightRecorder getFlightRecorder() {
    return flightRecorder;
  }

  public void setFlightRecorder(FlightRecorder jobHistory) {
    this.flightRecorder = jobHistory;
  }

  public LaunchScheduler getNeutronScheduler() {
    return neutronScheduler;
  }

  public void setNeutronScheduler(LaunchScheduler neutronScheduler) {
    this.neutronScheduler = neutronScheduler;
  }

  public Scheduler getScheduler() {
    return neutronScheduler.getScheduler();
  }

  public Map<Class<?>, LaunchPad> getScheduleRegistry() {
    return neutronScheduler.getScheduleRegistry();
  }

  @Override
  public void addExecutingJob(TriggerKey key, NeutronRocket job) {
    neutronScheduler.addExecutingJob(key, job);
  }

  public void removeExecutingJob(TriggerKey key) {
    neutronScheduler.removeExecutingJob(key);
  }

  public Map<TriggerKey, NeutronRocket> getExecutingJobs() {
    return neutronScheduler.getExecutingJobs();
  }

  /**
   * One scheduler to rule them all. And in the multi-threading ... bind them? :-)
   * 
   * @return evil single instance
   */
  public static LaunchCommand getInstance() {
    return instance;
  }

  protected static synchronized LaunchCommand startContinuousMode(String[] args) {
    LOGGER.info("STARTING ON-DEMAND JOBS SERVER ...");
    try {
      final FlightPlan globalOpts = FlightPlan.parseCommandLine(args);
      if (globalOpts.isSimulateLaunch()) {
        instance.startingOpts = globalOpts;
        return instance;
      }

      final Injector injector = HyperCube.buildInjector(globalOpts);
      instance = injector.getInstance(LaunchCommand.class);
      instance.startingOpts = globalOpts;

      instance.settings.setContinuousMode(true);
      instance.settings.setInitialMode(!instance.startingOpts.isLastRunMode());
      instance.initScheduler(injector);

      LOGGER.info("ON-DEMAND JOBS SERVER STARTED!");
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }

    return instance;
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

    FlightPlan globalOpts;
    try {
      globalOpts = FlightPlan.parseCommandLine(args);
    } catch (Exception e) {
      throw JobLogs.runtime(LOGGER, e, "CMD LINE ERROR! {}", e.getMessage());
    }

    if (globalOpts.isSimulateLaunch()) {
      return; // Test "main" methods
    }

    final Injector injector = HyperCube.buildInjector(globalOpts);
    instance = injector.getInstance(LaunchCommand.class);
    instance.startingOpts = globalOpts;
    instance.settings.setContinuousMode(false);

    try (final T job = HyperCube.newJob(klass, args)) {
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
