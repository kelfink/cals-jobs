package gov.ca.cwds.jobs.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.JobProgressTrack;

@DisallowConcurrentExecution
public class NeutronInterruptableJob implements InterruptableJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronInterruptableJob.class);

  private String className;
  private String cmdLine;
  private volatile JobProgressTrack track;

  /**
   * Constructor.
   */
  public NeutronInterruptableJob() {
    // No-op.
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    final JobDataMap map = context.getJobDetail().getJobDataMap();
    className = map.getString("job_class");
    cmdLine = map.getString("cmd_line");

    final String jobName = context.getTrigger().getJobKey().getName();

    LOGGER.info("Execute {}", className);
    try (final BasePersonIndexerJob job = JobRunner.getInstance().createJob(className,
        StringUtils.isBlank(cmdLine) ? null : cmdLine.split("\\s+"))) {
      track = new JobProgressTrack();
      track.setJobName(jobName);
      job.setTrack(track);

      map.put("track", track);
      context.setResult(track);

      job.run();
    } catch (Exception e) {
      LOGGER.error("SCHEDULED JOB FAILED! {}", className, e);
      throw new JobExecutionException("SCHEDULED JOB FAILED!", e);
    }

    LOGGER.info("Executed {}", className);
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
