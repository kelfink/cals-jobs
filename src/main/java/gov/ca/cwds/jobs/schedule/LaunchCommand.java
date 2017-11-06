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
import java.util.function.Function;

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

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomFlightRecorder;
import gov.ca.cwds.jobs.component.AtomLaunchScheduler;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.HyperCube;
import gov.ca.cwds.neutron.enums.NeutronDateTimeFormat;
import gov.ca.cwds.neutron.launch.listener.NeutronJobListener;
import gov.ca.cwds.neutron.launch.listener.NeutronSchedulerListener;
import gov.ca.cwds.neutron.launch.listener.NeutronTriggerListener;
import gov.ca.cwds.neutron.log.JobLogs;
import gov.ca.cwds.neutron.manage.rest.NeutronRestServer;

/**
 * Run stand-alone jobs or serve up jobs with Quartz. The master of ceremonies, AKA, Jimmy Neutron.
 * 
 * @author CWDS API Team
 */
public class LaunchCommand implements AtomLaunchScheduler, AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchCommand.class);

  /**
   * Singleton instance. One director to rule them all.
   */
  private static LaunchCommand instance = new LaunchCommand();

  private static Function<FlightPlan, Injector> injectorMaker = HyperCube::buildInjectorFunctional;

  private Injector injector;

  private LaunchCommandSettings settings = new LaunchCommandSettings();

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

  private LaunchScheduler launchScheduler;

  private int exitCode = -1; // Single launch mode only.

  private LaunchCommand() {
    // no-op
  }

  @Inject
  public LaunchCommand(final FlightRecorder jobHistory, final LaunchScheduler launchScheduler,
      final ElasticsearchDao esDao) {
    this.flightRecorder = jobHistory;
    this.launchScheduler = launchScheduler;
    this.esDao = esDao;
  }

  /**
   * MOVE this responsibility to another unit.
   * 
   * @param initialMode obvious
   * @param hoursInPast number of hours in past
   * @throws IOException on file read/write error
   */
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
    this.launchScheduler.stopScheduler(waitForJobsToComplete);
  }

  @Managed(description = "Start the scheduler")
  public void startScheduler() throws NeutronException {
    this.launchScheduler.startScheduler();
  }

  /**
   * Configure Quartz scheduling.
   * 
   * <p>
   * MORE: inject this dependency. MOVE this responsibility to another unit.
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
    launchScheduler.setScheduler(scheduler);

    // Scheduler listeners.
    final ListenerManager listenerMgr = launchScheduler.getScheduler().getListenerManager();
    listenerMgr.addSchedulerListener(new NeutronSchedulerListener());
    listenerMgr.addTriggerListener(new NeutronTriggerListener(launchScheduler));
    listenerMgr.addJobListener(instance.settings.isInitialMode()
        ? DefaultFlightSchedule.buildFullLoadJobChainListener() : new NeutronJobListener());
    return launchScheduler;
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
      LOGGER.warn("\n\n\n\n>>>>>>> FULL, INITIAL LOAD! <<<<<<<\n\n\n\n");
    }
  }

  protected void exposeREST() {
    // Jetty for REST administration.
    Thread jettyServer = new Thread(restServer::run);
    jettyServer.start();
  }

  protected void exposeJMX() {
    final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());
    for (LaunchPad pad : launchScheduler.getScheduleRegistry().values()) {
      exporter.export("Neutron:rocket=" + pad.getFlightSchedule().getShortName(), pad);
    }

    // Expose Command Center methods to JMX.
    exporter.export("Neutron:runner=command_center", this);
    exporter.getExportedObjects();

    // Expose Guice bean attributes to JMX.
    // Manager.manage("Neutron_Guice", injector);
  }

  protected void initializeCommandCenter() {
    if (this.settings.isExposeJmx()) {
      exposeJMX();
    }
    if (this.settings.isExposeRest()) {
      exposeREST();
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
      configureQuartz(injector); // MOVE this responsibility to another class.

      // NOTE: make last change window configurable.
      final DateFormat fmt =
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
      final Date now = settings.isInitialMode() ? fmt.parse("1917-10-31 10:11:12.000")
          : new DateTime().minusHours(NeutronSchedulerConstants.LAST_CHG_WINDOW_HOURS).toDate();

      configureInitialMode(now);

      // Prepare launch pads and flight plans.
      for (DefaultFlightSchedule sched : DefaultFlightSchedule.values()) {
        final Class<?> klass = sched.getRocketClass();
        final FlightPlan opts = new FlightPlan(startingOpts);
        handleTimeFile(opts, fmt, now, sched);

        final LaunchPad nj =
            new LaunchPad(launchScheduler.getScheduler(), sched, flightRecorder, opts);
        launchScheduler.getScheduleRegistry().put(klass, nj);
        launchScheduler.getFlightPlanManger().addFlightPlan(klass, opts);
      }

      // MOVE this responsibility to another class.
      if (startingOpts.isDropIndex()) {
        esDao.deleteIndex(esDao.getConfig().getElasticsearchAlias());
      }

      // Start rockets.
      for (LaunchPad j : launchScheduler.getScheduleRegistry().values()) {
        j.schedule();
      }

      // Cindy: "Let's light this candle!"
      if (!this.settings.isTestMode()) {
        LOGGER.warn("start scheduler ...");
        launchScheduler.getScheduler().start();
      }

      initializeCommandCenter();
    } catch (IOException | SchedulerException | ParseException e) {
      try {
        launchScheduler.getScheduler().shutdown(false);
      } catch (SchedulerException e2) {
        LOGGER.warn("FAILED TO STOP SCHEDULER! {}", e2.getMessage(), e2);
      }
      throw JobLogs.checked(LOGGER, e, "INIT ERROR: {}", e.getMessage());
    }
  }

  /**
   * Create a registered rocket.
   * 
   * @param klass batch job class
   * @param flightPlan command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createRocket(final Class<?> klass, final FlightPlan flightPlan)
      throws NeutronException {
    final BasePersonIndexerJob ret = launchScheduler.createJob(klass, flightPlan);
    ret.setFlightPlan(flightPlan);

    LOGGER.warn("CREATED ROCKET! {}", flightPlan.getLastRunLoc());
    return ret;
  }

  /**
   * Create a registered rocket.
   * 
   * @param rocketName batch job class
   * @param flightPlan command line arguments
   * @return the job
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonIndexerJob createRocket(final String rocketName, final FlightPlan flightPlan)
      throws NeutronException {
    try {
      return createRocket(Class.forName(rocketName), flightPlan);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "ROCKET CLASS NOT FOUND! {}", rocketName);
    }
  }

  @Override
  public FlightRecord launchScheduledFlight(final Class<?> klass, final FlightPlan flightPlan)
      throws NeutronException {
    return this.launchScheduler.launchScheduledFlight(klass, flightPlan);
  }

  @Override
  public FlightRecord launchScheduledFlight(final String jobName, final FlightPlan flightPlan)
      throws NeutronException {
    return this.launchScheduler.launchScheduledFlight(jobName, flightPlan);
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
  public boolean isLaunchVetoed(String className) throws NeutronException {
    return this.launchScheduler.isLaunchVetoed(className);
  }

  @Override
  public LaunchPad scheduleLaunch(Class<?> klazz, DefaultFlightSchedule sched, FlightPlan opts) {
    return this.launchScheduler.scheduleLaunch(klazz, sched, opts);
  }

  public AtomFlightRecorder getFlightRecorder() {
    return flightRecorder;
  }

  public void setFlightRecorder(FlightRecorder jobHistory) {
    this.flightRecorder = jobHistory;
  }

  public LaunchScheduler getNeutronScheduler() {
    return launchScheduler;
  }

  public void setLaunchScheduler(LaunchScheduler launchScheduler) {
    this.launchScheduler = launchScheduler;
  }

  public Scheduler getScheduler() {
    return launchScheduler.getScheduler();
  }

  public Map<Class<?>, LaunchPad> getScheduleRegistry() {
    return launchScheduler.getScheduleRegistry();
  }

  @Override
  public void trackInFlightRocket(TriggerKey key, NeutronRocket rocket) {
    launchScheduler.trackInFlightRocket(key, rocket);
  }

  public void removeInFlightRocket(TriggerKey key) {
    launchScheduler.removeExecutingJob(key);
  }

  public Map<TriggerKey, NeutronRocket> getInFlightRockets() {
    return launchScheduler.getRocketsInFlight();
  }

  /**
   * One scheduler to rule them all. And in the multi-threading ... bind them? :-)
   * 
   * @return evil singleton instance
   */
  public static LaunchCommand getInstance() {
    return instance;
  }

  protected static FlightPlan parseCommandLine(String... args) {
    FlightPlan standardFlightPlan;
    try {
      standardFlightPlan = FlightPlan.parseCommandLine(args);
    } catch (Exception e) {
      throw JobLogs.runtime(LOGGER, e, "CMD LINE ERROR! {}", e.getMessage());
    }

    return standardFlightPlan;
  }

  protected static LaunchCommand buildStandaloneInstance(final FlightPlan standardFlightPlan)
      throws NeutronException {
    Injector injector;
    try {
      injector = injectorMaker.apply(standardFlightPlan);
      instance = injector.getInstance(LaunchCommand.class);
      instance.startingOpts = standardFlightPlan;
      instance.settings.setContinuousMode(false);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "Rethrow", e.getMessage());
    }

    return instance;
  }

  /**
   * Single launch mode: close resources and exit JVM.
   */
  @Override
  public void close() throws Exception {
    //
    if (!isTestMode() && instance.exitCode != 0) {
      // Shutdown all remaining resources, even those not attached to this job.
      Runtime.getRuntime().exit(instance.exitCode); // NOSONAR
    }
  }

  protected static synchronized LaunchCommand startSchedulerMode(String[] args) {
    LOGGER.info("STARTING LAUNCH COMMAND ...");

    final FlightPlan standardFlightPlan = parseCommandLine(args);
    if (standardFlightPlan.isSimulateLaunch()) {
      return instance; // Test "main" methods
    }

    final Injector injector = injectorMaker.apply(standardFlightPlan);
    try (final LaunchCommand launchCommand = buildStandaloneInstance(standardFlightPlan)) {
      instance = injector.getInstance(LaunchCommand.class);
      instance.startingOpts = standardFlightPlan;

      instance.settings.setContinuousMode(true);
      instance.settings.setInitialMode(!instance.startingOpts.isLastRunMode());
      instance.initScheduler(injector);
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      instance.exitCode = -1;
      throw JobLogs.runtime(LOGGER, e, "LAUNCH COMMAND FAILED TO START!: {}", e.getMessage());
    }

    LOGGER.info("LAUNCH COMMAND STARTED!");
    return instance;
  }

  /**
   * Entry point for expendable, one shot rockets, typically for initial load. Not used in
   * continuous mode.
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
    final FlightPlan standardFlightPlan = parseCommandLine(args);
    if (standardFlightPlan.isSimulateLaunch()) {
      return; // Test "main" methods
    }

    instance.exitCode = -1;
    try (final LaunchCommand launchCommand = buildStandaloneInstance(standardFlightPlan);
        final T job = HyperCube.newRocket(klass, args)) {
      launchCommand.exitCode = -1;
      job.run();
      launchCommand.exitCode = 1;
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      instance.exitCode = -1;
      throw JobLogs.runtime(LOGGER, e, "STANDALONE ROCKET FAILED!: {}", e.getMessage());
    }
  }

  public static Function<FlightPlan, Injector> getInjectorMaker() {
    return injectorMaker;
  }

  public static void setInjectorMaker(Function<FlightPlan, Injector> makeLaunchCommand) {
    LaunchCommand.injectorMaker = makeLaunchCommand;
  }

  public Injector getInjector() {
    return injector;
  }

  public void setInjector(Injector injector) {
    this.injector = injector;
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
    startSchedulerMode(args);
  }

}
