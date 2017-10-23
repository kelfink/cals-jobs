package gov.ca.cwds.jobs.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronTriggerListener implements TriggerListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronTriggerListener.class);

  @Override
  public String getName() {
    return "neutron_trigger_listener";
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {
    final TriggerKey key = trigger.getKey();
    LOGGER.info("trigger fired: key: {}", key);
    JobRunner.getInstance().getExecutingJobs().put(key,
        (NeutronInterruptableJob) context.getJobInstance());
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    final NeutronInterruptableJob job = (NeutronInterruptableJob) context.getJobInstance();
    final boolean answer = job.isVetoExecution();
    LOGGER.info("veto job execution: {}", answer);
    return answer;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    final TriggerKey key = trigger.getKey();
    LOGGER.error("TRIGGER MISFIRED! key: {}", key);
    JobRunner.getInstance().removeExecutingJob(key);
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
      CompletedExecutionInstruction triggerInstructionCode) {
    final TriggerKey key = trigger.getKey();
    LOGGER.info("trigger complete: key: {}", key);
    JobRunner.getInstance().removeExecutingJob(key);
  }

}
