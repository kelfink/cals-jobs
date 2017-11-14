package gov.ca.cwds.neutron.launch.listener;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.launch.LaunchDirector;
import gov.ca.cwds.neutron.launch.NeutronRocket;

public class NeutronTriggerListener implements TriggerListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronTriggerListener.class);

  private final LaunchDirector neutronScheduler;

  @Inject
  public NeutronTriggerListener(final LaunchDirector neutronScheduler) {
    this.neutronScheduler = neutronScheduler;
  }

  @Override
  public String getName() {
    return "neutron_trigger_listener";
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {
    final TriggerKey key = trigger.getKey();
    LOGGER.debug("trigger fired: key: {}", key);
    neutronScheduler.getRocketsInFlight().put(key, (NeutronRocket) context.getJobInstance());
  }

  /**
   * Job instance type is {@link NeutronRocket}.
   */
  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    final JobDataMap map = context.getJobDetail().getJobDataMap();
    final String className = map.getString("job_class");
    boolean answer = true;

    try {
      answer = neutronScheduler.isLaunchVetoed(className);
    } catch (Exception e) {
      throw JobLogs.runtime(LOGGER, e, "ERROR FINDING JOB FACADE! job class: {}", className, e);
    }

    LOGGER.info("veto job execution: {}", answer);
    return answer;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {
    final TriggerKey key = trigger.getKey();
    LOGGER.warn("TRIGGER MISFIRED! key: {}", key);
    neutronScheduler.removeExecutingJob(key);
  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context,
      CompletedExecutionInstruction triggerInstructionCode) {
    final TriggerKey key = trigger.getKey();
    LOGGER.debug("trigger complete: key: {}", key);
    neutronScheduler.removeExecutingJob(key);
  }

}
