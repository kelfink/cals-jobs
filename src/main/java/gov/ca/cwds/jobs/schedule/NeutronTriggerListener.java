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
    LOGGER.info("triggerFired");
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    LOGGER.info("vetoJobExecution");
    return false;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    LOGGER.info("triggerMisfired");
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
      CompletedExecutionInstruction triggerInstructionCode) {
    LOGGER.info("triggerComplete");
  }

}
