package gov.ca.cwds.neutron.launch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.atom.AtomFlightPlanManager;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchDirector;
import gov.ca.cwds.neutron.atom.AtomLaunchPad;
import gov.ca.cwds.neutron.atom.AtomRocketFactory;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.util.shrinkray.NeutronClassFinder;

@Singleton
public class LaunchDirector implements AtomLaunchDirector {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchDirector.class);

  /**
   * Quartz scheduler. Could hide this implementation behind an interface.
   */
  private Scheduler scheduler;

  private final AtomFlightRecorder flightRecorder;

  private final AtomRocketFactory rocketFactory;

  private final AtomFlightPlanManager flightPlanManger;

  private FlightPlan flightPlan;

  /**
   * Schedule launch pads.
   */
  private final Map<Class<?>, AtomLaunchPad> launchPads = new ConcurrentHashMap<>();

  /**
   * Might not be necessary. Listeners and running jobs should handle this, but we still need a
   * single place to track rockets in flight.
   * 
   * <p>
   * OPTION: Quartz scheduler can track this too. Obsolete implementation?
   * </p>
   */
  private final Map<TriggerKey, NeutronRocket> rocketsInFlight = new ConcurrentHashMap<>();

  @Inject
  public LaunchDirector(final AtomFlightRecorder flightRecorder,
      final AtomRocketFactory rocketFactory, final AtomFlightPlanManager flightPlanManager) {
    this.flightRecorder = flightRecorder;
    this.rocketFactory = rocketFactory;
    this.flightPlanManger = flightPlanManager;
  }

  /**
   * Prepare a registered rocket.
   * 
   * @param klass rocket class
   * @param flightPlan command line arguments
   * @return a fueled rocket
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonRocket fuelRocket(final Class<?> klass, final FlightPlan flightPlan)
      throws NeutronException {
    return this.rocketFactory.fuelRocket(klass, flightPlan);
  }

  /**
   * Create a registered rocket.
   * 
   * @param rocketName batch job class
   * @param flightPlan command line arguments
   * @return a fueled rocket
   * @throws NeutronException unexpected runtime error
   */
  @SuppressWarnings("rawtypes")
  public BasePersonRocket fuelRocket(final String rocketName, final FlightPlan flightPlan)
      throws NeutronException {
    return this.rocketFactory.fuelRocket(rocketName, flightPlan);
  }

  @Override
  public void prepareLaunchPads() {
    // TODO Auto-generated method stub
  }

  @Override
  public FlightLog launch(Class<?> klass, final FlightPlan flightPlan) throws NeutronException {
    try {
      LOGGER.info("LAUNCH SCHEDULED ROCKET! {}", klass.getName());
      final BasePersonRocket<?, ?> rocket = fuelRocket(klass, flightPlan);
      rocket.run();
      return rocket.getFlightLog();
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "SCHEDULED LAUNCH FAILED!: {}", e.getMessage());
    }
  }

  @Override
  public FlightLog launch(String rocketName, final FlightPlan flightPlan) throws NeutronException {
    return launch(NeutronClassFinder.classForName(rocketName), flightPlan);
  }

  @Override
  public AtomLaunchPad scheduleLaunch(StandardFlightSchedule sched, FlightPlan flightPlan)
      throws NeutronException {
    final LaunchPad pad = new LaunchPad(this, sched, flightPlan);
    final Class<?> klass = sched.getRocketClass();
    launchPads.put(klass, pad);
    flightPlanManger.addFlightPlan(klass, flightPlan);
    pad.schedule();

    return pad;
  }

  @Override
  public void stopScheduler(boolean waitForJobsToComplete) throws NeutronException {
    LOGGER.warn("STOP SCHEDULER! wait for jobs to complete: {}", waitForJobsToComplete);
    try {
      this.getScheduler().shutdown(waitForJobsToComplete);
    } catch (SchedulerException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO STOP SCHEDULER! {}", e.getMessage());
    }
  }

  @Override
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
  public void markRocketAsInFlight(final TriggerKey key, NeutronRocket rocket) {
    rocketsInFlight.put(key, rocket);
  }

  /**
   * QUESTION: is this needed?
   * 
   * @param key trigger key
   */
  public void removeExecutingJob(final TriggerKey key) {
    if (rocketsInFlight.containsKey(key)) {
      rocketsInFlight.remove(key);
    }
  }

  public Map<TriggerKey, NeutronRocket> getRocketsInFlight() {
    return rocketsInFlight;
  }

  public AtomRocketFactory getRocketFactory() {
    return rocketFactory;
  }

  public FlightPlan getFlightPlan() {
    return flightPlan;
  }

  public void setFlightPlan(FlightPlan flightPlan) {
    this.flightPlan = flightPlan;
  }

  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Map<Class<?>, AtomLaunchPad> getLaunchPads() {
    return launchPads;
  }

  @Override
  public boolean isLaunchVetoed(String className) throws NeutronException {
    return this.getLaunchPads().get(NeutronClassFinder.classForName(className)).isVetoExecution();
  }

  @Override
  public AtomFlightPlanManager getFlightPlanManger() {
    return flightPlanManger;
  }

  @Override
  public AtomFlightRecorder getFlightRecorder() {
    return flightRecorder;
  }

}
