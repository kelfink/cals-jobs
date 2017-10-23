package gov.ca.cwds.jobs.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
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
    LOGGER.info("trigger fired: ");
    JobRunner.getInstance().getExecutingJobs().put(trigger.getKey(),
        (NeutronInterruptableJob) context.getJobInstance());
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    LOGGER.info("veto Job Execution");
    return false;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    LOGGER.info("trigger misfired");
    JobRunner.getInstance().getExecutingJobs().remove(trigger.getKey());
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
      CompletedExecutionInstruction triggerInstructionCode) {
    LOGGER.info("triggerComplete");
  }

}
