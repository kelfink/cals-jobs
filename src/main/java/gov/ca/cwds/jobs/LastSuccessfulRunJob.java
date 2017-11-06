package gov.ca.cwds.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.component.AtomJobControl;
import gov.ca.cwds.jobs.component.AtomShared;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.component.Rocket;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.neutron.enums.NeutronDateTimeFormat;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;
import gov.ca.cwds.neutron.log.JobLogs;

/**
 * Abstract base class for all Neutron jobs based on last successful run time.
 * 
 * @author CWDS API Team
 */
public abstract class LastSuccessfulRunJob implements Rocket, AtomShared, AtomJobControl {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LastSuccessfulRunJob.class);

  /**
   * Command line options for this job.
   */
  protected FlightPlan opts;

  private String lastRunTimeFilename;

  private final FlightRecorder jobHistory;

  /**
   * Construct from last successful run date-time.
   * 
   * @param lastJobRunTimeFilename location of last run time file
   * @param jobHistory injected job history
   * @param opts job options
   */
  public LastSuccessfulRunJob(String lastJobRunTimeFilename, final FlightRecorder jobHistory,
      final FlightPlan opts) {
    this.lastRunTimeFilename =
        StringUtils.isBlank(lastJobRunTimeFilename) ? opts.getLastRunLoc() : lastJobRunTimeFilename;
    this.jobHistory = jobHistory;
    this.opts = opts;
  }

  /**
   * HACK for dependency injection issue. (re-?) initialize the job.
   * 
   * @param lastJobRunTimeFilename last run file location
   * @param opts launch options
   */
  public void init(String lastJobRunTimeFilename, final FlightPlan opts) {
    this.lastRunTimeFilename =
        StringUtils.isBlank(lastJobRunTimeFilename) ? opts.getLastRunLoc() : lastJobRunTimeFilename;
    this.opts = opts;
  }

  @Override
  public final void run() {
    LOGGER.warn("LastSuccessfulRunJob.run(): lastRunTimeFilename: {}", lastRunTimeFilename);
    final FlightRecord track = getTrack();
    track.start();

    try {
      final Date lastRunTime = determineLastSuccessfulRunTime();
      track.setLastChangeSince(lastRunTime);

      writeLastSuccessfulRunTime(executeJob(lastRunTime));
    } catch (Exception e) {
      fail();
      LOGGER.error("FAIL JOB!", e);
    }

    finish(); // Close resources, notify listeners, or even close JVM in standalone mode.
    track.done();

    // SLF4J does not yet support conditional invocation.
    if (LOGGER.isWarnEnabled()) {
      LOGGER.warn(track.toString());
    }

    // Only applies in continuous mode.
    jobHistory.addTrack(getClass(), track);
  }

  /**
   * If last run time is provide in options then use it, otherwise use provided
   * lastSuccessfulRunTime.
   * 
   * <p>
   * NOTE: make the look-back period configurable.
   * </p>
   * 
   * @param lastSuccessfulRunTime last successful run
   * @param opts command line job options
   * @return appropriate date to detect changes
   */
  protected Date calcLastRunDate(final Date lastSuccessfulRunTime, final FlightPlan opts) {
    Date ret;
    final Date lastSuccessfulRunTimeOverride = opts.getOverrideLastRunTime();

    if (lastSuccessfulRunTimeOverride != null) {
      ret = lastSuccessfulRunTimeOverride;
    } else {
      final Calendar cal = Calendar.getInstance();
      cal.setTime(lastSuccessfulRunTime);
      cal.add(Calendar.MINUTE, NeutronIntegerDefaults.LOOKBACK_MINUTES.getValue());
      ret = cal.getTime();
    }

    return ret;
  }

  /**
   * Calculate last successful run date/time, per
   * {@link LastSuccessfulRunJob#calcLastRunDate(Date, FlightPlan)}.
   * 
   * @param lastSuccessfulRunTime last successful run
   * @return appropriate date to detect changes
   */
  protected Date calcLastRunDate(final Date lastSuccessfulRunTime) {
    return calcLastRunDate(lastSuccessfulRunTime, getOpts());
  }

  /**
   * Reads the last run file and returns the last run date.
   * 
   * @return last successful run date/time as a Java Date.
   * @throws NeutronException I/O or parse error
   */
  protected Date determineLastSuccessfulRunTime() throws NeutronException {
    Date ret = null;
    try (BufferedReader br = new BufferedReader(new FileReader(lastRunTimeFilename))) { // NOSONAR
      ret = new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
          .parse(br.readLine().trim()); // NOSONAR
    } catch (IOException | ParseException e) {
      fail();
      throw JobLogs.checked(LOGGER, e, "ERROR FINDING LAST RUN TIME: {}", e.getMessage());
    }

    return ret;
  }

  /**
   * Write the time stamp <strong>IF</strong> the job succeeded.
   * 
   * @param datetime date and time to store
   * @throws NeutronException I/O or parse error
   */
  protected void writeLastSuccessfulRunTime(Date datetime) throws NeutronException {
    if (!isFailed()) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(lastRunTimeFilename))) { // NOSONAR
        w.write(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.formatter().format(datetime));
      } catch (IOException e) {
        fail();
        throw JobLogs.checked(LOGGER, e, "ERROR WRITING TIMESTAMP FILE: {}", e.getMessage());
      }
    }
  }

  /**
   * Execute the batch job. Child classes must provide an implementation.
   * 
   * @param lastSuccessfulRunTime The last successful run
   * @return The time of the latest run if successful.
   * @throws NeutronException if job fails
   */
  public abstract Date executeJob(Date lastSuccessfulRunTime) throws NeutronException;

  /**
   * Marks the job as completed. Close resources, notify listeners, or even close JVM.
   */
  protected abstract void finish();

  /**
   * Getter for last job run time.
   * 
   * @return last time the job ran successfully, in format
   *         {@link NeutronDateTimeFormat#LAST_RUN_DATE_FORMAT}
   */
  public String getLastJobRunTimeFilename() {
    return lastRunTimeFilename;
  }

  /**
   * Getter for this job's options.
   * 
   * @return this job's options
   */
  @Override
  public FlightPlan getOpts() {
    return opts;
  }

  /**
   * Setter for this job's options.
   * 
   * @param opts this job's options
   */
  public void setOpts(FlightPlan opts) {
    this.opts = opts;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void done() {
    this.getTrack().done();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fail() {
    this.getTrack().fail();
    this.getTrack().done();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneRetrieve() {
    getTrack().doneRetrieve();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneTransform() {
    getTrack().doneTransform();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneIndex() {
    getTrack().doneIndex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRunning() {
    return getTrack().isRunning();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFailed() {
    return getTrack().isFailed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRetrieveDone() {
    return getTrack().isRetrieveDone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTransformDone() {
    return getTrack().isTransformDone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isIndexDone() {
    return getTrack().isIndexDone();
  }

  public String getLastRunTimeFilename() {
    return lastRunTimeFilename;
  }

  public void setLastRunTimeFilename(String lastRunTimeFilename) {
    this.lastRunTimeFilename = lastRunTimeFilename;
  }

}
