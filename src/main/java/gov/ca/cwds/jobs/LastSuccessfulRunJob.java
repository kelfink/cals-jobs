package gov.ca.cwds.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.ca.cwds.jobs.util.JobLogUtils;

/**
 * Abstract base class for all batch jobs based on last successful run time.
 * 
 * @author CWDS API Team
 */
public abstract class LastSuccessfulRunJob implements Job {

  private static final Logger LOGGER = LogManager.getLogger(LastSuccessfulRunJob.class);

  /**
   * Last run file date format. NOT thread-safe!
   */
  protected DateFormat jobDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Completion flag for fatal errors.
   */
  protected volatile boolean fatalError = false;

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

    if (!fatalError) {
      writeLastSuccessfulRunTime(curentTimeRunTime);
    }

    finish(); // Close resources, notify listeners, or even close JVM.
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
        ret = jobDateFormat.parse(br.readLine().trim());
      } catch (FileNotFoundException e) {
        fatalError = true;
        JobLogUtils.raiseError(LOGGER, e, "Caught FileNotFoundException: {}", e.getMessage());
      } catch (IOException e) {
        fatalError = true;
        JobLogUtils.raiseError(LOGGER, e, "Caught IOException: {}", e.getMessage());
      } catch (ParseException e) {
        fatalError = true;
        JobLogUtils.raiseError(LOGGER, e, "Caught ParseException: {}", e.getMessage());
      }
    }

    return ret;
  }

  private void writeLastSuccessfulRunTime(Date datetime) {
    if (datetime != null && !StringUtils.isBlank(this.lastRunTimeFilename)) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(lastRunTimeFilename))) {
        w.write(jobDateFormat.format(datetime));
      } catch (IOException e) {
        fatalError = true;
        JobLogUtils.raiseError(LOGGER, e, "Failed to write timestamp file: {}", e.getMessage());
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
   * @return last time the job ran successfully, in format {@link #jobDateFormat}
   */
  public String getLastJobRunTimeFilename() {
    return lastRunTimeFilename;
  }

}
