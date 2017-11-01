package gov.ca.cwds.jobs.schedule;

import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.JobOptions;

/**
 * Wrapper for scheduled jobs.
 * 
 * @author CWDS API Team
 * @see LaunchDirector
 */
@DisallowConcurrentExecution
public class NeutronInterruptableJob implements InterruptableJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronInterruptableJob.class);

  private static final AtomicInteger instanceCounter = new AtomicInteger(0);

  private String className;
  private String cmdLine;
  private JobOptions opts;
  private volatile FlightRecord track;

  /**
   * Constructor.
   */
  public NeutronInterruptableJob() {
    // No-op.
  }

  /**
   * QUESTION: does Quartz allow injection? Is it safe to pass objects via the job data map??
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    final JobDataMap map = context.getJobDetail().getJobDataMap();
    className = map.getString(NeutronSchedulerConstants.ROCKET_CLASS);
    final String jobName = context.getTrigger().getJobKey().getName();

    LOGGER.info("Execute {}, instance # ", className, instanceCounter.incrementAndGet());
    try (final BasePersonIndexerJob job = LaunchDirector.getInstance().createJob(className, opts)) {
      track = new FlightRecord(); // fresh progress track
      track.setJobName(jobName);
      job.setTrack(track);

      map.put("opts", opts);
      map.put("track", track);
      context.setResult(track);

      job.setOpts(opts);
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

  public FlightRecord getTrack() {
    return track;
  }

  public void setTrack(FlightRecord track) {
    this.track = track;
  }

  public JobOptions getOpts() {
    return opts;
  }

  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

}
