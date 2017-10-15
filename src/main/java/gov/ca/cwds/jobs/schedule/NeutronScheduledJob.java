package gov.ca.cwds.jobs.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.JobProgressTrack;

@DisallowConcurrentExecution
public class NeutronScheduledJob implements InterruptableJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronScheduledJob.class);

  private String className;
  private String cmdLine;
  private JobProgressTrack track;

  public NeutronScheduledJob() {}

  @SuppressWarnings("rawtypes")
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    className = context.getJobDetail().getJobDataMap().getString("job_class");
    cmdLine = context.getJobDetail().getJobDataMap().getString("cmd_line");

    LOGGER.info("Executing {}", className);
    try (final BasePersonIndexerJob neutronJob = JobRunner.createJob(className,
        StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"))) {
      track = neutronJob.getTrack();
      context.getJobDetail().getJobDataMap().put("track", track);
      context.setResult(track);
      neutronJob.run();
    } catch (Exception e) {
      throw new JobExecutionException("SCHEDULED JOB FAILED!", e);
    }
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    LOGGER.warn("INTERRUPT RUNNING JOB!");
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getCmdLine() {
    return cmdLine;
  }

  public void setCmdLine(String cmdLine) {
    this.cmdLine = cmdLine;
  }

  public JobProgressTrack getTrack() {
    return track;
  }

  public void setTrack(JobProgressTrack track) {
    this.track = track;
  }

}
