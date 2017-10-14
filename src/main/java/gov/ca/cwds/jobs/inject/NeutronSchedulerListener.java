package gov.ca.cwds.jobs.inject;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronSchedulerListener implements SchedulerListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  @Override
  public void jobScheduled(Trigger trigger) {
    LOGGER.info("jobScheduled");
  }

  @Override
  public void jobUnscheduled(TriggerKey triggerKey) {
    LOGGER.info("jobUnscheduled");
  }

  @Override
  public void triggerFinalized(Trigger trigger) {
    LOGGER.info("triggerFinalized");
  }

  @Override
  public void triggerPaused(TriggerKey triggerKey) {
    LOGGER.info("triggerPaused");
  }

  @Override
  public void triggersPaused(String triggerGroup) {
    LOGGER.info("triggersPaused");
  }

  @Override
  public void triggerResumed(TriggerKey triggerKey) {
    LOGGER.info("triggerResumed");
  }

  @Override
  public void triggersResumed(String triggerGroup) {
    LOGGER.info("triggersResumed");
  }

  @Override
  public void jobAdded(JobDetail jobDetail) {
    LOGGER.info("jobAdded");
  }

  @Override
  public void jobDeleted(JobKey jobKey) {
    LOGGER.info("jobDeleted");
  }

  @Override
  public void jobPaused(JobKey jobKey) {
    LOGGER.info("jobPaused");
  }

  @Override
  public void jobsPaused(String jobGroup) {
    LOGGER.info("jobsPaused");
  }

  @Override
  public void jobResumed(JobKey jobKey) {
    LOGGER.info("jobResumed");
  }

  @Override
  public void jobsResumed(String jobGroup) {
    LOGGER.info("jobsResumed");
  }

  @Override
  public void schedulerError(String msg, SchedulerException cause) {
    LOGGER.info("schedulerError");
  }

  @Override
  public void schedulerInStandbyMode() {
    LOGGER.info("schedulerInStandbyMode");
  }

  @Override
  public void schedulerStarted() {
    LOGGER.info("schedulerStarted");
  }

  @Override
  public void schedulerStarting() {
    LOGGER.info("schedulerStarting");
  }

  @Override
  public void schedulerShutdown() {
    LOGGER.info("schedulerShutdown");
  }

  @Override
  public void schedulerShuttingdown() {
    LOGGER.info("schedulerShuttingdown");
  }

  @Override
  public void schedulingDataCleared() {
    LOGGER.info("schedulingDataCleared");
  }

}
