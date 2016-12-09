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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JobBasedOnLastSuccessfulRunTime implements Job {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LogManager.getLogger(JobBasedOnLastSuccessfulRunTime.class);

  protected DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String lastJobRunTimeFilename;

  public JobBasedOnLastSuccessfulRunTime(String lastJobRunTimeFilename) {
    this.lastJobRunTimeFilename = lastJobRunTimeFilename;
  }

  @Override
  final public void run() {
    Date lastRunTime = determineLastSuccessfulRunTime();
    Date curentTimeRunTime = _run(lastRunTime);
    writeLastSuccessfulRunTime(curentTimeRunTime);
  }

  private Date determineLastSuccessfulRunTime() {
    try {
      FileReader fileReader = new FileReader(lastJobRunTimeFilename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String timestring = bufferedReader.readLine();
      bufferedReader.close();
      return DATE_FORMAT.parse(timestring);
    } catch (FileNotFoundException e) {
      throw new JobsException(e);
    } catch (IOException e) {
      throw new JobsException(e);
    } catch (ParseException e) {
      throw new JobsException(e);
    }
  }

  private void writeLastSuccessfulRunTime(Date datetime) {
    if (datetime != null) {
      try (
          BufferedWriter writedtparm = new BufferedWriter(new FileWriter(lastJobRunTimeFilename))) {
        writedtparm.write(DATE_FORMAT.format(datetime));
      } catch (IOException e) {
        throw new JobsException("Could not write the timestamp parameter file", e);
      }
    }
  }

  /**
   * 
   * @param lastSuccessfulRunTime The last successful run
   * 
   * @return The time of the latest run if successful.
   */
  public abstract Date _run(Date lastSuccessfulRunTime);

}
