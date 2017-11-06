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

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.FlightRecord;

/**
 * Wrapper for scheduled jobs.
 * 
 * @author CWDS API Team
 * @see LaunchCommand
 */
@DisallowConcurrentExecution
public class NeutronRocket implements InterruptableJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronRocket.class);

  private static final AtomicInteger instanceCounter = new AtomicInteger(0);

  @SuppressWarnings("rawtypes")
  private final BasePersonIndexerJob rocket;

  private volatile FlightRecord track;

  /**
   * Constructor.
   * 
   * @param <T> ES replicated Person persistence class
   * @param <M> MQT entity class, if any, or T
   * @param rocket launch me!
   */
  public <T extends PersistentObject, M extends ApiGroupNormalizer<?>> NeutronRocket(
      final BasePersonIndexerJob<T, M> rocket) {
    this.rocket = rocket;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    final JobDataMap map = context.getJobDetail().getJobDataMap();
    final String jobName = context.getTrigger().getJobKey().getName();

    LOGGER.info("Execute {}, instance # {}", rocket.getClass().getName(),
        instanceCounter.incrementAndGet());

    try (final BasePersonIndexerJob job = rocket) {
      track = new FlightRecord(); // fresh progress track
      track.setJobName(jobName);
      job.setTrack(track);

      map.put("opts", job.getFlightPlan());
      map.put("track", track);
      context.setResult(track);

      job.run();
    } catch (Exception e) {
      LOGGER.error("FAILED TO LAUNCH! {}", e);
      throw new JobExecutionException("FAILED TO LAUNCH! {}", e);
    }

    LOGGER.info("Executed {}", rocket.getClass().getName());
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    LOGGER.warn("INTERRUPT RUNNING JOB!");
  }

  public FlightRecord getTrack() {
    return track;
  }

  public void setTrack(FlightRecord track) {
    this.track = track;
  }

  public BasePersonIndexerJob getRocket() {
    return rocket;
  }

}
