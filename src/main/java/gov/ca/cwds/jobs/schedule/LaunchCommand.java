package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.MBeanExporter;
import org.weakref.jmx.Managed;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.tools.jmx.Manager;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchCommand;
import gov.ca.cwds.neutron.atom.AtomLaunchPad;
import gov.ca.cwds.neutron.atom.AtomLaunchScheduler;
import gov.ca.cwds.neutron.enums.NeutronDateTimeFormat;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.inject.HyperCube;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.launch.LaunchPad;
import gov.ca.cwds.neutron.manage.rest.NeutronRestServer;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.util.NeutronStringUtil;

/**
 * Run stand-alone rockets or serve up rockets with Quartz. The master of ceremonies, AKA, Jimmy
 * Neutron.
 * 
 * @author CWDS API Team
 */
public class LaunchCommand implements AutoCloseable, AtomLaunchCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchCommand.class);

  /**
   * Singleton instance. One director to rule them all.
   */
  private static LaunchCommand instance = new LaunchCommand();

  private static Function<FlightPlan, Injector> injectorMaker = HyperCube::buildInjectorFunctional;

  private static LaunchCommandSettings settings = new LaunchCommandSettings();

  private static FlightPlan standardFlightPlan;

  private FlightPlan commonFlightPlan;

  private Injector injector; // HACK: make an interface for DI?

  /**
   * Only used to drop and create indexes.
   * 
   * <p>
   * HACK: **move to another module**
   * </p>
   */
  private ElasticsearchDao esDao;

  /**
   * REST administration. Started if enabled in #.
   */
  private NeutronRestServer restServer = new NeutronRestServer();

  private FlightRecorder flightRecorder = new FlightRecorder();

  private AtomLaunchScheduler launchScheduler;

  private boolean fatalError;

  private LaunchCommand() {
    // no-op
  }

  @Inject
  public LaunchCommand(final FlightRecorder flightRecorder,
      final AtomLaunchScheduler launchScheduler, final ElasticsearchDao esDao) {
    this.flightRecorder = flightRecorder;
    this.launchScheduler = launchScheduler;
    this.esDao = esDao;
  }

  /**
   * <strong>MOVE</strong> this responsibility to another unit.
   * 
   * @param initialMode obvious
   * @param hoursInPast number of hours in past
   * @throws IOException on file read/write error
   */
  protected void resetTimestamps(boolean initialMode, int hoursInPast) throws IOException {
    final DateFormat fmt =
        new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
    final Date now = new DateTime().minusHours(initialMode ? 876000 : hoursInPast).toDate();

    for (StandardFlightSchedule sched : StandardFlightSchedule.values()) {
      final FlightPlan opts = new FlightPlan(commonFlightPlan);

      // Find the rocket's time file under the base directory:
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

  @Override
  @Managed(description = "Stop the scheduler")
  public void stopScheduler(boolean waitForJobsToComplete) throws NeutronException {
    this.launchScheduler.stopScheduler(waitForJobsToComplete);
  }

  @Override
  @Managed(description = "Start the scheduler")
  public void startScheduler() throws NeutronException {
    this.launchScheduler.startScheduler();
  }

  /**
   * Find the job's time file under the base directory.
   * 
   * @param flightPlan base options
   * @param fmt reusable date format
   * @param now current datetime
   * @param sched this schedule
   * @throws IOException on file error
   */
  protected void handleTimeFile(final FlightPlan flightPlan, final DateFormat fmt, final Date now,
      final StandardFlightSchedule sched) throws IOException {
    final StringBuilder buf = new StringBuilder();

    buf.append(flightPlan.getBaseDirectory()).append(File.separatorChar)
        .append(sched.getShortName()).append(".time");
    final String timeFileLoc =
        buf.toString().replaceAll(File.separator + File.separator, File.separator);
    flightPlan.setLastRunLoc(timeFileLoc);
    LOGGER.debug("base directory: {}, job name: {}, last run loc: {}",
        flightPlan.getBaseDirectory(), sched.getShortName(), flightPlan.getLastRunLoc());

    // If timestamp file doesn't exist, create it.
    final File f = new File(timeFileLoc); // NOSONAR
    final boolean fileExists = f.exists();
    final boolean overrideLastRunTime = flightPlan.getOverrideLastRunTime() != null;

    if (!fileExists || settings.isInitialMode()) {
      FileUtils.writeStringToFile(f,
          fmt.format(overrideLastRunTime ? flightPlan.getOverrideLastRunTime() : now));
    }
  }

  protected void configureInitialMode(final Date now) {
    if (settings.isInitialMode()) {
      commonFlightPlan.setOverrideLastRunTime(now);
      commonFlightPlan.setLastRunMode(false);
      LOGGER.warn("\n\n\n\n>>>>>>> FULL, INITIAL LOAD! <<<<<<<\n\n\n\n");
    }
  }

  protected void exposeREST() {
    // Jetty for REST administration.
    Thread jettyServer = new Thread(restServer::run);
    jettyServer.start();
  }

  protected void exposeJMX() {
    LOGGER.warn("\n>>>>>>> ENABLE JMX! <<<<<<<\n");
    final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());
    for (AtomLaunchPad pad : launchScheduler.getScheduleRegistry().values()) {
      exporter.export("Neutron:rocket=" + pad.getFlightSchedule().getShortName(), pad);
    }

    // Expose Command Center methods to JMX.
    exporter.export("Neutron:runner=Launch_Command", this);
    LOGGER.info("MBeans: {}", exporter.getExportedObjects());

    // Expose Guice bean attributes to JMX.
    Manager.manage("Neutron_Guice", injector);
  }

  protected void initializeManagementInterfaces() {
    if (LaunchCommand.settings.isExposeJmx()) {
      exposeJMX();
    }

    if (LaunchCommand.settings.isExposeRest()) {
      exposeREST();
    }
  }

  /**
   * Too many responsibilities: initialize Quartz, register jobs, expose operations to JMX, even
   * initialize HTTP ...
   * 
   * @throws NeutronException on initialization error
   */
  protected void initScheduler() throws NeutronException {
    try {
      // NOTE: make last change window configurable.
      final DateFormat fmt =
          new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat());
      final Date now = settings.isInitialMode() ? fmt.parse("1917-10-31 10:11:12.000")
          : new DateTime().minusHours(NeutronSchedulerConstants.LAST_CHG_WINDOW_HOURS).toDate();

      configureInitialMode(now);

      // Prepare launch pads and flight plans.
      for (StandardFlightSchedule sched : StandardFlightSchedule.values()) {
        final Class<?> klass = sched.getRocketClass();
        final FlightPlan flightPlan = new FlightPlan(commonFlightPlan);
        handleTimeFile(flightPlan, fmt, now, sched);

        final LaunchPad pad = new LaunchPad(launchScheduler, sched, flightRecorder, flightPlan);
        launchScheduler.getScheduleRegistry().put(klass, pad);
        launchScheduler.getFlightPlanManger().addFlightPlan(klass, flightPlan);
      }

      // **MOVE** this responsibility to another class.
      if (commonFlightPlan.isDropIndex()) {
        esDao.deleteIndex(esDao.getConfig().getElasticsearchAlias());
      }

      // Start rockets.
      for (AtomLaunchPad pad : launchScheduler.getScheduleRegistry().values()) {
        pad.schedule();
      }

      // Cindy: "Let's light this candle!"
      if (!LaunchCommand.settings.isTestMode()) {
        LOGGER.warn("start scheduler ...");
        launchScheduler.getScheduler().start();
      }

      initializeManagementInterfaces();
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
   * Load all job definitions and continue running after a job completes.
   * 
   * @return true if running in continuous mode
   */
  public static boolean isSchedulerMode() {
    return LaunchCommand.settings.isSchedulerMode();
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @return whether in test mode
   */
  public static boolean isTestMode() {
    return LaunchCommand.settings.isTestMode();
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @param mode whether in test mode
   */
  public static void setTestMode(boolean mode) {
    LaunchCommand.settings.setTestMode(mode);
  }

  public static boolean isInitialMode() {
    return LaunchCommand.settings.isInitialMode();
  }

  public ElasticsearchDao getEsDao() {
    return esDao;
  }

  public void setEsDao(ElasticsearchDao esDao) {
    if (this.esDao == null) {
      this.esDao = esDao;
    }
  }

  public FlightPlan getCommonFlightPlan() {
    return commonFlightPlan;
  }

  public void setCommonFlightPlan(FlightPlan startingOpts) {
    this.commonFlightPlan = startingOpts;
  }

  public AtomFlightRecorder getFlightRecorder() {
    return flightRecorder;
  }

  public void setFlightRecorder(FlightRecorder jobHistory) {
    this.flightRecorder = jobHistory;
  }

  public AtomLaunchScheduler getNeutronScheduler() {
    return launchScheduler;
  }

  public void setLaunchScheduler(LaunchScheduler launchScheduler) {
    this.launchScheduler = launchScheduler;
  }

  public Scheduler getScheduler() {
    return launchScheduler.getScheduler();
  }

  public Map<Class<?>, AtomLaunchPad> getScheduleRegistry() {
    return launchScheduler.getScheduleRegistry();
  }

  public void trackInFlightRocket(TriggerKey key, NeutronRocket rocket) {
    launchScheduler.trackInFlightRocket(key, rocket);
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
    FlightPlan ret;
    try {
      ret = FlightPlan.parseCommandLine(args);
    } catch (Exception e) {
      throw JobLogs.runtime(LOGGER, e, "CMD LINE ERROR! {}", e.getMessage());
    }

    return ret;
  }

  protected static LaunchCommand buildCommandCenter(final FlightPlan standardFlightPlan)
      throws NeutronException {
    Injector injector;
    try {
      injector = injectorMaker.apply(standardFlightPlan);
      instance = injector.getInstance(LaunchCommand.class);
      instance.commonFlightPlan = standardFlightPlan;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "COMMAND CENTER FAILURE! {}", e.getMessage());
    }

    return instance;
  }

  /**
   * <p>
   * Single launch mode: close resources and exit JVM.
   * </p>
   * <p>
   * Continuous launch mode: close resources and exit JVM only on fatal startup error.
   * </p>
   */
  @Override
  public void close() throws Exception {
    if (!isTestMode() && (!isSchedulerMode() || instance.fatalError)) {
      // Shutdown all remaining resources, even those not attached to this job.
      final int exitCode = this.fatalError ? -1 : 0;
      LOGGER.info("Process exit code: {}", exitCode);
      Runtime.getRuntime().exit(exitCode); // NOSONAR
    }
  }

  /**
   * Run continuous, scheduler mode.
   * 
   * @return launch command instance with dependencies injected
   */
  protected static synchronized LaunchCommand startSchedulerMode() {
    LOGGER.info("STARTING LAUNCH COMMAND ...");

    if (standardFlightPlan.isSimulateLaunch()) {
      return instance; // Test "main" methods
    }

    LaunchCommand.settings.setSchedulerMode(true);
    LaunchCommand.settings.setInitialMode(!standardFlightPlan.isLastRunMode());
    final Injector injector = injectorMaker.apply(standardFlightPlan);

    try (final LaunchCommand launchCommand = buildCommandCenter(standardFlightPlan)) {
      instance = injector.getInstance(LaunchCommand.class);
      instance.commonFlightPlan = new FlightPlan(standardFlightPlan);
      instance.injector = injector;

      instance.initScheduler();
      instance.fatalError = false; // Good to go
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      instance.fatalError = true;
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
  public static <T extends BasePersonRocket<?, ?>> void runStandalone(final Class<T> klass,
      String... args) {
    standardFlightPlan = parseCommandLine(args);
    System.setProperty("LAUNCH_DIR",
        NeutronStringUtil.filePath(standardFlightPlan.getLastRunLoc()));

    LaunchCommand.settings.setSchedulerMode(false);
    LaunchCommand.settings.setInitialMode(!standardFlightPlan.isLastRunMode());
    instance.fatalError = true; // Murphy was an optimist.

    if (standardFlightPlan.isSimulateLaunch()) {
      return; // Test "main" methods
    }

    try (final LaunchCommand launchCommand = buildCommandCenter(standardFlightPlan);
        final T rocket = HyperCube.newRocket(klass, args)) {
      rocket.setFlightPlan(standardFlightPlan);
      rocket.run();
      launchCommand.fatalError = false; // We made it. Almost.
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      instance.fatalError = true;
      throw JobLogs.runtime(LOGGER, e, "STANDALONE ROCKET FAILED!: {}", e.getMessage());
    }
  }

  public static Function<FlightPlan, Injector> getInjectorMaker() {
    return LaunchCommand.injectorMaker;
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

  public static LaunchCommandSettings getSettings() {
    return LaunchCommand.settings;
  }

  public static void setSettings(LaunchCommandSettings settings) {
    LaunchCommand.settings = settings;
  }

  public static FlightPlan getStandardFlightPlan() {
    return standardFlightPlan;
  }

  public static void setStandardFlightPlan(FlightPlan standardFlightPlan) {
    LaunchCommand.standardFlightPlan = standardFlightPlan;
  }

  /**
   * OPTION: configure individual rockets, like Rundeck.
   * <p>
   * Perhaps load a configuration file with settings per rockets.
   * </p>
   * 
   * @param args command line
   */
  public static void main(String[] args) {
    standardFlightPlan = parseCommandLine(args);
    LaunchCommand.settings.setBaseDirectory(standardFlightPlan.getBaseDirectory());
    System.setProperty("LAUNCH_DIR", LaunchCommand.settings.getBaseDirectory());
    startSchedulerMode();
  }

}
