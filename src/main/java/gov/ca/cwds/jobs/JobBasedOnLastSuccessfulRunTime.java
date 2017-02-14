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

/**
 * Abstract base class for all batch jobs based on last successful run time.
 * 
 * @author CWDS API Team
 */
public abstract class JobBasedOnLastSuccessfulRunTime implements Job {

  private static final Logger LOGGER = LogManager.getLogger(JobBasedOnLastSuccessfulRunTime.class);

  protected DateFormat jobDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private String lastJobRunTimeFilename;

  /**
   * Default constructor.
   * 
   * @param lastJobRunTimeFilename location of last run time file
   */
  public JobBasedOnLastSuccessfulRunTime(String lastJobRunTimeFilename) {
    this.lastJobRunTimeFilename = lastJobRunTimeFilename;
  }

  @Override
  public final void run() {
    final Date lastRunTime = determineLastSuccessfulRunTime();
    final Date curentTimeRunTime = _run(lastRunTime);
    writeLastSuccessfulRunTime(curentTimeRunTime);
  }

  /**
   * Reads the last run file and returns the last run date.
   * 
   * @return last successful run date/time as a Java Date.
   */
  protected Date determineLastSuccessfulRunTime() {
    Date ret = null;

    if (!StringUtils.isBlank(this.lastJobRunTimeFilename)) {
      try (BufferedReader bufferedReader =
          new BufferedReader(new FileReader(lastJobRunTimeFilename))) {
        ret = jobDateFormat.parse(bufferedReader.readLine().trim());
      } catch (FileNotFoundException e) {
        LOGGER.error("Caught FileNotFoundException: {}", e.getMessage(), e);
        throw new JobsException(e);
      } catch (IOException e) {
        LOGGER.error("Caught IOException: {}", e.getMessage(), e);
        throw new JobsException(e);
      } catch (ParseException e) {
        LOGGER.error("Caught ParseException: {}", e.getMessage(), e);
        throw new JobsException(e);
      }
    }

    return ret;
  }

  private void writeLastSuccessfulRunTime(Date datetime) {
    if (datetime != null && !StringUtils.isBlank(this.lastJobRunTimeFilename)) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(lastJobRunTimeFilename))) {
        w.write(jobDateFormat.format(datetime));
      } catch (IOException e) {
        throw new JobsException("Could not write the timestamp parameter file", e);
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
   * Getter for last job run time.
   * 
   * @return last time the job ran successfully, in format {@link #jobDateFormat}
   */
  public String getLastJobRunTimeFilename() {
    return lastJobRunTimeFilename;
  }

}
