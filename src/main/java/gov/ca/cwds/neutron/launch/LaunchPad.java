package gov.ca.cwds.neutron.launch;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchDirector;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.vox.jmx.VoxLaunchPadMBean;

public class LaunchPad implements VoxLaunchPadMBean {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchPad.class);

  private transient Scheduler scheduler;

  private transient AtomLaunchDirector launchDirector;
  private final AtomFlightRecorder flightRecorder;

  private final StandardFlightSchedule flightSchedule;
  private FlightPlan flightPlan;

  private final String rocketName;
  private final String triggerName;

  private final TriggerKey triggerKey;
  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  private boolean vetoExecution;

  @Inject
  public LaunchPad(final AtomLaunchDirector director, StandardFlightSchedule sched,
      final FlightPlan flightPlan) {
    this.launchDirector = director;
    this.scheduler = director.getScheduler();
    this.flightRecorder = director.getFlightRecorder();

    this.flightSchedule = sched;
    this.flightPlan = flightPlan;

    this.rocketName = flightSchedule.getRocketName();
    this.jobKey = new JobKey(rocketName, NeutronSchedulerConstants.GRP_LST_CHG);
    this.triggerName = flightSchedule.getRocketName();
    triggerKey = new TriggerKey(triggerName, NeutronSchedulerConstants.GRP_LST_CHG);

    final FlightLog flightLog = new FlightLog();
    flightLog.setRocketName(sched.getRocketName());

    // Seed the flight log history.
    flightRecorder.logFlight(sched.getRocketClass(), flightLog);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Launch rocket now, show results immediately")
  public String run(String cmdLine) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", flightSchedule.getRocketName());
      final FlightPlan plan =
          FlightPlan.parseCommandLine(StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
      final FlightLog flightLog = this.launchDirector.launch(flightSchedule.getRocketClass(), plan);
      return flightLog.toString();
    } catch (Exception e) {
      LOGGER.error("FAILED TO RUN ON DEMAND! {}", e.getMessage(), e);
      return JobLogs.stackToString(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Schedule rocket launch")
  public void schedule() throws NeutronException {
    try {
      if (scheduler.checkExists(this.jobKey)) {
        LOGGER.warn("ROCKET ALREADY SCHEDULED! rocket: {}", rocketName);
        return;
      }

      final String rocketClass = flightSchedule.getRocketClass().getName();
      jd = newJob(NeutronRocket.class)
          .withIdentity(rocketName,
              LaunchCommand.isInitialMode() ? NeutronSchedulerConstants.GRP_FULL_LOAD
                  : NeutronSchedulerConstants.GRP_LST_CHG)
          .usingJobData(NeutronSchedulerConstants.ROCKET_CLASS, rocketClass).storeDurably().build();

      if (!LaunchCommand.isInitialMode()) {
        scheduler.scheduleJob(jd,
            newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_LST_CHG)
                .withPriority(flightSchedule.getLastRunPriority())
                .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(flightSchedule.getWaitPeriodSeconds()).repeatForever())
                .startAt(DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
                .build());
        LOGGER.info("Scheduled trigger {}", rocketName);
      } else {
        // HACK: cleaner way?
        if (flightSchedule.getInitialLoadOrder() == 1) {
          final Trigger trigger =
              newTrigger().withIdentity(rocketName, NeutronSchedulerConstants.GRP_FULL_LOAD)
                  .startAt(
                      DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
                  .build();
          scheduler.scheduleJob(jd, trigger);
        } else {
          scheduler.addJob(jd, false, false);
        }
      }

    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILURE TO LAUNCH! rocket: {}", rocketName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Unschedule rocket")
  public void unschedule() throws NeutronException {
    try {
      LOGGER.warn("unschedule launch");
      scheduler.unscheduleJob(triggerKey);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO UNSCHEDULE LAUNCH! rocket: {}", rocketName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Show rocket's last flight status")
  public String status() {
    LOGGER.warn("Show rocket status");
    return flightRecorder.getLastFlightLog(this.flightSchedule.getRocketClass()).toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Show rocket's flight history")
  public String history() {
    LOGGER.warn("Show rocket history");
    StringBuilder buf = new StringBuilder();
    flightRecorder.getHistory(this.flightSchedule.getRocketClass()).stream().forEach(buf::append);
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Show rocket log")
  public String logs() {
    StringBuilder buf = new StringBuilder();
    buf.append("log stuff");
    // IMPL ME!
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Abort flying rocket")
  public void stop() throws NeutronException {
    try {
      LOGGER.warn("Abort flying rocket {}", rocketName);
      unschedule();
      final JobKey key = new JobKey(rocketName, NeutronSchedulerConstants.GRP_LST_CHG);
      scheduler.interrupt(key);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO ABORT ROCKET! rocket: {}", rocketName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Pause rocket")
  public void pause() throws NeutronException {
    try {
      LOGGER.warn("Pause rocket {}", rocketName);
      scheduler.pauseTrigger(triggerKey);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO PAUSE ROCKET! rocket: {}", rocketName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Resume rocket")
  public void resume() throws NeutronException {
    try {
      LOGGER.warn("Resume rocket {}", rocketName);
      scheduler.resumeTrigger(triggerKey);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO PAUSE ROCKET! rocket: {}", rocketName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Shutdown command center")
  public void shutdown() throws NeutronException {
    LOGGER.warn("Shutdown command center");
    LaunchCommand.getInstance().shutdown();
  }

  @Override
  public boolean isVetoExecution() {
    return vetoExecution;
  }

  @Override
  public void setVetoExecution(boolean vetoExecution) {
    this.vetoExecution = vetoExecution;
  }

  @Override
  public JobDetail getJd() {
    return jd;
  }

  @Override
  public FlightPlan getFlightPlan() {
    return flightPlan;
  }

  @Override
  public void setFlightPlan(FlightPlan opts) {
    this.flightPlan = opts;
  }

  @Override
  public StandardFlightSchedule getFlightSchedule() {
    return flightSchedule;
  }

  @Override
  public AtomFlightRecorder getFlightRecorder() {
    return flightRecorder;
  }

  @Override
  public String getRocketName() {
    return rocketName;
  }

  @Override
  public String getTriggerName() {
    return triggerName;
  }

  @Override
  public JobKey getJobKey() {
    return jobKey;
  }

  public AtomLaunchDirector getLaunchDirector() {
    return launchDirector;
  }

  public TriggerKey getTriggerKey() {
    return triggerKey;
  }

}
