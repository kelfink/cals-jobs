package gov.ca.cwds.jobs.schedule;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronSchedulerListener implements SchedulerListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronSchedulerListener.class);

  @Override
  public void jobScheduled(Trigger trigger) {
    LOGGER.info("job scheduled");
  }

  @Override
  public void jobUnscheduled(TriggerKey triggerKey) {
    LOGGER.info("job unscheduled");
  }

  @Override
  public void triggerFinalized(Trigger trigger) {
    LOGGER.info("trigger finalized");
  }

  @Override
  public void triggerPaused(TriggerKey triggerKey) {
    LOGGER.info("trigger paused");
  }

  @Override
  public void triggersPaused(String triggerGroup) {
    LOGGER.info("triggers paused");
  }

  @Override
  public void triggerResumed(TriggerKey triggerKey) {
    LOGGER.info("trigger resumed");
  }

  @Override
  public void triggersResumed(String triggerGroup) {
    LOGGER.info("triggers resumed");
  }

  @Override
  public void jobAdded(JobDetail jobDetail) {
    LOGGER.info("job added");
  }

  @Override
  public void jobDeleted(JobKey jobKey) {
    LOGGER.info("job deleted");
  }

  @Override
  public void jobPaused(JobKey jobKey) {
    LOGGER.info("job paused");
  }

  @Override
  public void jobsPaused(String jobGroup) {
    LOGGER.info("jobs paused");
  }

  @Override
  public void jobResumed(JobKey jobKey) {
    LOGGER.info("job resumed");
  }

  @Override
  public void jobsResumed(String jobGroup) {
    LOGGER.info("jobs resumed");
  }

  @Override
  public void schedulerError(String msg, SchedulerException cause) {
    LOGGER.info("scheduler error");
  }

  @Override
  public void schedulerInStandbyMode() {
    LOGGER.info("scheduler in standby mode");
  }

  @Override
  public void schedulerStarted() {
    LOGGER.info("scheduler started");
  }

  @Override
  public void schedulerStarting() {
    LOGGER.info("scheduler starting");
  }

  @Override
  public void schedulerShutdown() {
    LOGGER.info("scheduler shutdown");
  }

  @Override
  public void schedulerShuttingdown() {
    LOGGER.info("scheduler shuttingdown");
  }

  @Override
  public void schedulingDataCleared() {
    LOGGER.info("scheduling data cleared");
  }

}
