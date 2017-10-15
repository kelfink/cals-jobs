package gov.ca.cwds.jobs.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronJobListener implements JobListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronSchedulerListener.class);

  @Override
  public String getName() {
    LOGGER.info("getName");
    return "neutron_job_listener";
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    LOGGER.info("jobToBeExecuted");
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {
    LOGGER.info("jobExecutionVetoed");
  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    LOGGER.info("jobWasExecuted");
  }

}
