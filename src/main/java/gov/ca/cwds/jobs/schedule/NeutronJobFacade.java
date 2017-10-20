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

import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

public class NeutronJobFacade implements Serializable {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJobFacade.class);

  private transient Scheduler scheduler;

  private final NeutronDefaultJobSchedule defaultSchedule;
  private final String jobName;
  private final String triggerName;

  private volatile JobKey jobKey;
  private volatile JobDetail jd;

  public NeutronJobFacade(final Scheduler scheduler, NeutronDefaultJobSchedule sched) {
    this.scheduler = scheduler;
    this.defaultSchedule = sched;
    this.jobName = defaultSchedule.getName();
    this.triggerName = defaultSchedule.getName();
    this.jobKey = new JobKey(jobName, NeutronSchedulerConstants.GRP_LST_CHG);
  }

  @Managed(description = "Run job now, show results immediately")
  public String run(String cmdLineArgs) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", defaultSchedule.getName());
      final JobProgressTrack track =
          JobRunner.getInstance().runScheduledJob(defaultSchedule.getKlazz(),
              StringUtils.isBlank(cmdLineArgs) ? null : cmdLineArgs.split("\\s+"));
      return track.toString();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return "Job failed. Check the logs!";
    }
  }

  @Managed(description = "Schedule job on repeat")
  public void schedule() throws SchedulerException {
    if (scheduler.checkExists(this.jobKey)) {
      LOGGER.warn("JOB ALREADY SCHEDULED! {}", jobName);
    }

    jd = newJob(NeutronScheduledJob.class)
        .withIdentity(jobName, NeutronSchedulerConstants.GRP_LST_CHG)
        .usingJobData("job_class", defaultSchedule.getKlazz().getName()).build();

    // NOTE: initial mode: run only once.

    final Trigger trg =
        newTrigger().withIdentity(triggerName, NeutronSchedulerConstants.GRP_LST_CHG)
            .withSchedule(simpleSchedule().withIntervalInSeconds(defaultSchedule.getPeriodSeconds())
                .repeatForever())
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

  @Managed(description = "Show job status")
  public String status() {
    LOGGER.debug("Show job status");
    String ret;

    try {
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

}
