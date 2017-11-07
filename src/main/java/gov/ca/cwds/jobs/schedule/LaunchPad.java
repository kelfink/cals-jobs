package gov.ca.cwds.jobs.schedule;

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

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.component.FlightLog;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.vox.jmx.VoxLaunchPadMBean;

public class LaunchPad implements ApiMarker, VoxLaunchPadMBean {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchPad.class);
  private transient Scheduler scheduler;
  private final DefaultFlightSchedule flightSchedule;
  private final FlightRecorder flightRecorder;
  private final String jobName;
  private final String triggerName;
  private FlightPlan flightPlan;
  private boolean vetoExecution;
  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  public LaunchPad(final Scheduler scheduler, DefaultFlightSchedule sched,
      final FlightRecorder jobHistory, final FlightPlan opts) {
    this.scheduler = scheduler;
    this.flightSchedule = sched;
    this.flightRecorder = jobHistory;
    this.jobName = flightSchedule.getShortName();
    this.triggerName = flightSchedule.getShortName();
    this.jobKey = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
    this.flightPlan = opts;
    jobHistory.addFlightLog(sched.getRocketClass(), new FlightLog());
  }

  @Override
  @Managed(description = "Run job now, show results immediately")
  public String run(String cmdLine) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", flightSchedule.getShortName());
      final FlightPlan runOnceOpts =
          FlightPlan.parseCommandLine(StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
      final FlightLog track = LaunchCommand.getInstance()
          .launchScheduledFlight(flightSchedule.getRocketClass(), runOnceOpts);
      return track.toString();
    } catch (Exception e) {
      LOGGER.error("FAILED TO RUN ON DEMAND! {}", e.getMessage(), e);
      return JobLogs.stackToString(e);
    }
  }

  @Override
  @Managed(description = "Schedule job on repeat")
  public void schedule() throws NeutronException {
    try {
      if (scheduler.checkExists(this.jobKey)) {
        LOGGER.warn("JOB ALREADY SCHEDULED! {}", jobName);
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

  @Override
  @Managed(description = "Unschedule job")
  public void unschedule() throws NeutronException {
    try {
      LOGGER.warn("unschedule job");
      final TriggerKey triggerKey =
          new TriggerKey(triggerName, NeutronSchedulerConstants.GRP_LST_CHG);
      scheduler.pauseTrigger(triggerKey);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO UNSCHEDULE LAUNCH! rocket: {}", jobName);
    }
  }

  @Override
  @Managed(description = "Show job status")
  public String status() {
    LOGGER.debug("Show job status");
    return flightRecorder.getLastFlightLog(this.flightSchedule.getRocketClass()).toString();
  }

  @Override
  @Managed(description = "Show job history")
  public String history() {
    StringBuilder buf = new StringBuilder();
    flightRecorder.getHistory(this.flightSchedule.getRocketClass()).stream().forEach(buf::append);
    return buf.toString();
  }

  @Override
  @Managed(description = "Stop running job")
  public void stop() throws NeutronException {
    try {
      LOGGER.warn("Stop running job");
      unschedule();
      final JobKey key = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
      scheduler.interrupt(key);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO STOP ROCKET! rocket: {}", jobName);
    }
  }

  public boolean isVetoExecution() {
    return vetoExecution;
  }

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
  public DefaultFlightSchedule getFlightSchedule() {
    return flightSchedule;
  }

  @Override
  public FlightRecorder getFlightRecorder() {
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

}
