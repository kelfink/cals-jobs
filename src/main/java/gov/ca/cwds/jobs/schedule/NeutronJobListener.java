package gov.ca.cwds.jobs.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronJobListener implements JobListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJobListener.class);

  @Override
  public String getName() {
    return "neutron_job_listener";
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    LOGGER.info("job to be executed");
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {
    LOGGER.info("job execution vetoed");
  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    LOGGER.info("job was executed");
  }

}
