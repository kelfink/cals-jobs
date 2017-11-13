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

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.schedule.NeutronRocket;
import gov.ca.cwds.jobs.schedule.StandardFlightSchedule;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchScheduler;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.vox.jmx.VoxLaunchPadMBean;

public class LaunchPad implements VoxLaunchPadMBean {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchPad.class);

  private transient Scheduler scheduler;

  private transient AtomLaunchScheduler launchScheduler;
  private final StandardFlightSchedule flightSchedule;
  private final AtomFlightRecorder flightRecorder;
  private final String jobName;
  private final String triggerName;

  private FlightPlan flightPlan;
  private boolean vetoExecution;
  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  @Inject
  public LaunchPad(final AtomLaunchScheduler launchScheduler, StandardFlightSchedule sched,
      final FlightPlan flightPlan) {
    this.launchScheduler = launchScheduler;
    this.scheduler = launchScheduler.getScheduler();
    this.flightRecorder = launchScheduler.getFlightRecorder();

    this.flightSchedule = sched;
    this.flightPlan = flightPlan;

    this.jobName = flightSchedule.getShortName();
    this.jobKey = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
    this.triggerName = flightSchedule.getShortName();
    flightRecorder.addFlightLog(sched.getRocketClass(), new FlightLog());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Run rocket now, show results immediately")
  public String run(String cmdLine) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", flightSchedule.getShortName());
      final FlightPlan flightPlan =
          FlightPlan.parseCommandLine(StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
      final FlightLog flightLog =
          this.launchScheduler.launchScheduledFlight(flightSchedule.getRocketClass(), flightPlan);
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
  @Managed(description = "Schedule rocket on repeat")
  public void schedule() throws NeutronException {
    try {
      if (scheduler.checkExists(this.jobKey)) {
        LOGGER.warn("ROCKET ALREADY SCHEDULED! {}", jobName);
        return;
      }

      jd = newJob(NeutronRocket.class).withIdentity(jobName, NeutronSchedulerConstants.GRP_LST_CHG)
          .usingJobData(NeutronSchedulerConstants.ROCKET_CLASS,
              flightSchedule.getRocketClass().getName())
          .build();
      // Initial mode: run only **once**.
      final Trigger trg = !LaunchCommand.isInitialMode()
          ? newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_LST_CHG)
              .withPriority(flightSchedule.getLastRunPriority())
              .withSchedule(simpleSchedule()
                  .withIntervalInSeconds(flightSchedule.getWaitPeriodSeconds()).repeatForever())
              .startAt(DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
              .build()
          : newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_FULL_LOAD)
              .withPriority(flightSchedule.getLastRunPriority())
              .startAt(DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
              .build();
      scheduler.scheduleJob(jd, trg);
      LOGGER.info("Scheduled trigger {}", jobName);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SCHEDULE LAUNCH! rocket: {}", jobName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Unschedule rocket")
  public void unschedule() throws NeutronException {
    try {
      LOGGER.warn("unschedule rocket");
      final TriggerKey triggerKey =
          new TriggerKey(triggerName, NeutronSchedulerConstants.GRP_LST_CHG);
      scheduler.pauseTrigger(triggerKey);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO UNSCHEDULE LAUNCH! rocket: {}", jobName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Show rocket status")
  public String status() {
    LOGGER.debug("Show job status");
    return flightRecorder.getLastFlightLog(this.flightSchedule.getRocketClass()).toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Show rocket history")
  public String history() {
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
    flightRecorder.getHistory(this.flightSchedule.getRocketClass()).stream().forEach(buf::append);
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Managed(description = "Stop flying rocket")
  public void stop() throws NeutronException {
    try {
      LOGGER.warn("Stop flying rocket");
      unschedule();
      final JobKey key = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
      scheduler.interrupt(key);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO STOP ROCKET! rocket: {}", jobName);
    }
  }

  @Override
  public void shutdown() throws NeutronException {
    LOGGER.warn("Shutdown command center");
    launchScheduler.stopScheduler(false);
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
  public String getJobName() {
    return jobName;
  }

  @Override
  public String getTriggerName() {
    return triggerName;
  }

  @Override
  public JobKey getJobKey() {
    return jobKey;
  }

  public AtomLaunchScheduler getLaunchScheduler() {
    return launchScheduler;
  }

}
