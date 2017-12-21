package gov.ca.cwds.generic.jobs;

import gov.ca.cwds.generic.jobs.component.AtomJobControl;
import gov.ca.cwds.generic.jobs.component.AtomShared;
import gov.ca.cwds.generic.jobs.component.NeutronDateTimeFormat;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.generic.jobs.util.JobLogs;
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

/**
 * Abstract base class for all batch jobs based on last successful run time.
 * 
 * @author CWDS API Team
 */
public abstract class LastSuccessfulRunJob implements Job, AtomShared, AtomJobControl {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      LastSuccessfulRunJob.class);

  private static final int LOOKBACK_MINUTES = -25;

  /**
   * Completion flag for fatal errors.
   * <p>
   * Volatile guarantees that changes to this flag become visible other threads immediately. In
   * other words, threads don't cache a copy of this variable in their local memory for performance.
   * </p>
   */
  private volatile boolean fatalError = false;

  /**
   * Completion flag for data retrieval.
   */
  private volatile boolean doneRetrieve = false;

  /**
   * Completion flag for normalization/transformation.
   */
  private volatile boolean doneTransform = false;

  /**
   * Completion flag for document indexing.
   */
  private volatile boolean doneIndex = false;

  /**
   * Completion flag for whole job.
   */
  private volatile boolean doneJob = false;

  /**
   * Official start time.
   */
  protected final long startTime = System.currentTimeMillis();

  /**
   * Command line options for this job.
   */
  protected JobOptions opts;

  private String lastRunTimeFilename;

  /**
   * Default constructor.
   * 
   * @param lastJobRunTimeFilename location of last run time file
   */
  public LastSuccessfulRunJob(String lastJobRunTimeFilename) {
    this.lastRunTimeFilename = lastJobRunTimeFilename;
  }

  @Override
  public final void run() {
    final Date lastRunTime = determineLastSuccessfulRunTime();
    final Date curentTimeRunTime = _run(lastRunTime);

    if (!isFailed()) {
      writeLastSuccessfulRunTime(curentTimeRunTime);
    }

    finish(); // Close resources, notify listeners, or even close JVM in standalone mode.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markJobDone() {
    this.doneRetrieve = true;
    this.doneIndex = true;
    this.doneTransform = true;
    this.doneJob = true;
  }

  public void reset() {
    this.doneRetrieve = false;
    this.doneIndex = false;
    this.doneTransform = false;
    this.doneJob = false;
    this.fatalError = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markFailed() {
    this.fatalError = true;
    this.doneJob = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markRetrieveDone() {
    this.doneRetrieve = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markTransformDone() {
    this.doneTransform = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void markIndexDone() {
    this.doneIndex = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRunning() {
    return !doneJob;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFailed() {
    return fatalError;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRetrieveDone() {
    return doneRetrieve;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTransformDone() {
    return doneTransform;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isIndexDone() {
    return doneIndex;
  }

  /**
   * If last run time is provide in options then use it, otherwise use provided
   * lastSuccessfulRunTime.
   * 
   * @param lastSuccessfulRunTime last successful run
   * @param opts command line job options
   * @return appropriate date to detect changes
   */
  protected Date calcLastRunDate(final Date lastSuccessfulRunTime, final JobOptions opts) {
    Date ret;
    final Date lastSuccessfulRunTimeOverride = opts.getLastRunTime();

    if (lastSuccessfulRunTimeOverride != null) {
      ret = lastSuccessfulRunTimeOverride;
    } else {
      final Calendar cal = Calendar.getInstance();
      cal.setTime(lastSuccessfulRunTime);
      cal.add(Calendar.MINUTE, LOOKBACK_MINUTES);
      ret = cal.getTime();
    }

    return ret;
  }

  /**
   * Calculate last successful run date/time, per
   * {@link LastSuccessfulRunJob#calcLastRunDate(Date, JobOptions)}.
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
   */
  protected Date determineLastSuccessfulRunTime() {
    Date ret = null;

    if (!StringUtils.isBlank(this.lastRunTimeFilename)) {
      try (BufferedReader br = new BufferedReader(new FileReader(lastRunTimeFilename))) {
        ret = new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
            .parse(br.readLine().trim());
      } catch (IOException e) {
        markFailed();
        JobLogs.raiseError(LOGGER, e, "Caught IOException: {}", e.getMessage());
      } catch (ParseException e) {
        markFailed();
        JobLogs.raiseError(LOGGER, e, "Caught ParseException: {}", e.getMessage());
      }
    }

    return ret;
  }

  /**
   * Write the time stamp IF the job succeeded.
   * 
   * @param datetime date and time to store
   */
  protected void writeLastSuccessfulRunTime(Date datetime) {
    if (datetime != null && !StringUtils.isBlank(this.lastRunTimeFilename) && !isFailed()) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(lastRunTimeFilename))) {
        w.write(new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
            .format(datetime));
      } catch (IOException e) {
        markFailed();
        JobLogs.raiseError(LOGGER, e, "Failed to write timestamp file: {}", e.getMessage());
      }
    }
  }

  /**
   * Execute the batch job. Child classes must provide an implementation.
   * 
   * @param lastSuccessfulRunTime The last successful run
   * @return The time of the latest run if successful.
   */
  public abstract Date _run(Date lastSuccessfulRunTime);

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
  public JobOptions getOpts() {
    return opts;
  }

  /**
   * Setter for this job's options.
   * 
   * @param opts this job's options
   */
  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

}
