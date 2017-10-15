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

public final class NeutronJmxFacade implements Serializable {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJmxFacade.class);

  private transient Scheduler scheduler;
  private final NeutronDefaultJobSchedule jobSchedule;
  private final String scheduleJobName;
  private final String scheduleTriggerName;

  public NeutronJmxFacade(final Scheduler scheduler, NeutronDefaultJobSchedule sched) {
    this.scheduler = scheduler;
    this.jobSchedule = sched;
    this.scheduleJobName = "job_" + jobSchedule.getName();
    this.scheduleTriggerName = "trg_" + jobSchedule.getName();
  }

  @Managed(description = "Run job now, show results immediately")
  public String run(String cmdLineArgs) throws NeutronException {
    try {
      LOGGER.info("RUN JOB: {}", jobSchedule.getName());
      final JobProgressTrack track = JobRunner.runRegisteredJob(jobSchedule.getKlazz(),
          StringUtils.isBlank(cmdLineArgs) ? null : cmdLineArgs.split("\\s+"));
      return track.toString();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return "Job failed. Check the logs!";
    }
  }

  @Managed(description = "Schedule job on repeat")
  public void schedule() throws SchedulerException {
    if (scheduler.checkExists(new JobKey(scheduleJobName, JobRunner.GROUP_LAST_CHG))) {
      LOGGER.warn("JOB ALREADY SCHEDULED! {}", scheduleJobName);
    }

    final JobDetail jd =
        newJob(NeutronScheduledJob.class).withIdentity(scheduleJobName, JobRunner.GROUP_LAST_CHG)
            .usingJobData("job_class", jobSchedule.getKlazz().getName()).build();
    final Trigger t = newTrigger().withIdentity(scheduleTriggerName, JobRunner.GROUP_LAST_CHG)
        .withSchedule(
            simpleSchedule().withIntervalInSeconds(jobSchedule.getPeriodSeconds()).repeatForever())
        .startAt(DateTime.now().plusSeconds(jobSchedule.getStartDelaySeconds()).toDate()).build();

    scheduler.scheduleJob(jd, t);
  }

  @Managed(description = "Unschedule job")
  public void unschedule() throws SchedulerException {
    LOGGER.warn("unschedule job");
    final TriggerKey triggerKey = new TriggerKey(scheduleTriggerName, JobRunner.GROUP_LAST_CHG);
    scheduler.pauseTrigger(triggerKey);
  }

  @Managed(description = "Show job status")
  public String status() throws SchedulerException {
    LOGGER.debug("Show job status");
    final JobKey jobKey = new JobKey(scheduleJobName, JobRunner.GROUP_LAST_CHG);
    final JobDetail jd = scheduler.getJobDetail(jobKey);

    JobProgressTrack track = (JobProgressTrack) jd.getJobDataMap().get("track");
    return track.toString();
  }

  @Managed(description = "Stop running job")
  public void stop() throws SchedulerException {
    LOGGER.warn("Stop running job");
    unschedule();

    final JobKey key = new JobKey(scheduleJobName, JobRunner.GROUP_LAST_CHG);
    scheduler.interrupt(key);
  }

}
