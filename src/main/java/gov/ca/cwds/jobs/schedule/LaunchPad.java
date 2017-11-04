package gov.ca.cwds.jobs.schedule;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;

public class LaunchPad implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchPad.class);

  private transient Scheduler scheduler;

  private final DefaultFlightSchedule flightSchedule;

  private final FlightRecorder jobHistory;

  private final String jobName;
  private final String triggerName;

  private FlightPlan opts;

  private boolean vetoExecution;

  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  public LaunchPad(final Scheduler scheduler, DefaultFlightSchedule sched,
      final FlightRecorder jobHistory, final FlightPlan opts) {
    this.scheduler = scheduler;
    this.flightSchedule = sched;
    this.jobHistory = jobHistory;
    this.jobName = flightSchedule.getShortName();
    this.triggerName = flightSchedule.getShortName();
    this.jobKey = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
    this.opts = opts;

    jobHistory.addTrack(sched.getRocketClass(), new FlightRecord());
  }

  @Managed(description = "Run job now, show results immediately")
  public String run(String cmdLine) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", flightSchedule.getShortName());
      final FlightPlan runOnceOpts =
          FlightPlan.parseCommandLine(StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
      final FlightRecord track =
          LaunchCommand.getInstance().launchScheduledFlight(flightSchedule.getRocketClass(), runOnceOpts);
      return track.toString();
    } catch (Exception e) {
      LOGGER.error("FAILED TO RUN ON DEMAND! {}", e.getMessage(), e);
      return "Job failed. Check the logs!";
    }
  }

  @Managed(description = "Schedule job on repeat")
  public void schedule() throws SchedulerException {
    if (scheduler.checkExists(this.jobKey)) {
      LOGGER.warn("JOB ALREADY SCHEDULED! {}", jobName);
      return;
    }

    jd = newJob(NeutronRocket.class)
        .withIdentity(jobName, NeutronSchedulerConstants.GRP_LST_CHG)
        .usingJobData(NeutronSchedulerConstants.ROCKET_CLASS, flightSchedule.getRocketClass().getName())
        .build();

    // Initial mode: run only **once**.
    final Trigger trg = !LaunchCommand.isInitialMode()
        ? newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_LST_CHG)
            .withPriority(flightSchedule.getLastRunPriority())
            .withSchedule(simpleSchedule().withIntervalInSeconds(flightSchedule.getWaitPeriodSeconds())
                .repeatForever())
            .startAt(DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
            .build()
        : newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_FULL_LOAD)
            .withPriority(flightSchedule.getLastRunPriority())
            .startAt(DateTime.now().plusSeconds(flightSchedule.getStartDelaySeconds()).toDate())
            .build();

    scheduler.scheduleJob(jd, trg);
    LOGGER.info("Scheduled trigger {}", jobName);
  }

  @Managed(description = "Unschedule job")
  public void unschedule() throws SchedulerException {
    LOGGER.warn("unschedule job");
    final TriggerKey triggerKey =
        new TriggerKey(triggerName, NeutronSchedulerConstants.GRP_LST_CHG);
    scheduler.pauseTrigger(triggerKey);
  }

  @Managed(description = "Veto scheduled job execution")
  public void vetoScheduledJob() {
    LOGGER.debug("Veto job execution");
  }

  @Managed(description = "Show job status")
  public String status() {
    LOGGER.debug("Show job status");
    return jobHistory.getLastTrack(this.flightSchedule.getRocketClass()).toString();
  }

  @Managed(description = "Show job history")
  public String history() {
    StringBuilder buf = new StringBuilder();
    jobHistory.getHistory(this.flightSchedule.getRocketClass()).stream().forEach(buf::append);
    return buf.toString();
  }

  @Managed(description = "Stop running job")
  public void stop() throws SchedulerException {
    LOGGER.warn("Stop running job");
    unschedule();

    final JobKey key = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
    scheduler.interrupt(key);
  }

  public boolean isVetoExecution() {
    return vetoExecution;
  }

  public void setVetoExecution(boolean vetoExecution) {
    this.vetoExecution = vetoExecution;
  }

  public JobDetail getJd() {
    return jd;
  }

  public FlightPlan getOpts() {
    return opts;
  }

  public void setOpts(FlightPlan opts) {
    this.opts = opts;
  }

}
