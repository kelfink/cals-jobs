package gov.ca.cwds.jobs.schedule;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.Managed;

import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

public class NeutronJobMgtFacade implements Serializable {

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJobMgtFacade.class);

  private transient Scheduler scheduler;

  private final NeutronDefaultJobSchedule defaultSchedule;
  private final String jobName;
  private final String triggerName;
  private boolean vetoExecution;

  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  public NeutronJobMgtFacade(final Scheduler scheduler, NeutronDefaultJobSchedule sched) {
    this.scheduler = scheduler;
    this.defaultSchedule = sched;
    this.jobName = defaultSchedule.getName();
    this.triggerName = defaultSchedule.getName();
    this.jobKey = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
  }

  @Managed(description = "Run job now, show results immediately")
  public String run(String cmdLine) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", defaultSchedule.getName());
      final JobProgressTrack track =
          JobRunner.getInstance().runScheduledJob(defaultSchedule.getKlazz(),
              StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"));
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
    }

    jd = newJob(NeutronInterruptableJob.class)
        .withIdentity(jobName, NeutronSchedulerConstants.GRP_LST_CHG)
        .usingJobData("job_class", defaultSchedule.getKlazz().getName()).build();

    // Initial mode: run only once.
    final Trigger trg = !JobRunner.isInitialMode()
        ? newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_LST_CHG)
            .withPriority(defaultSchedule.getPriority())
            .withSchedule(simpleSchedule().withIntervalInSeconds(defaultSchedule.getPeriodSeconds())
                .repeatForever())
            .startAt(DateTime.now().plusSeconds(defaultSchedule.getStartDelaySeconds()).toDate())
            .build()
        : newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_FULL_LOAD)
            .withPriority(defaultSchedule.getPriority())
            .startAt(DateTime.now().plusSeconds(defaultSchedule.getStartDelaySeconds()).toDate())
            .build();

    scheduler.scheduleJob(jd, trg);
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
    String ret;

    try {
      for (JobExecutionContext ctx : scheduler.getCurrentlyExecutingJobs()) {
        if (ctx.getJobDetail().getKey().equals(jobKey)) {
          LOGGER.info("job exec context: {}", ctx);
        }
      }

      final JobProgressTrack track = (JobProgressTrack) jd.getJobDataMap().get("track");
      ret = track != null ? track.toString() : "NO TRACK?";
    } catch (Exception e) {
      ret = JobLogs.stackToString(e);
    }

    return ret;
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

}
