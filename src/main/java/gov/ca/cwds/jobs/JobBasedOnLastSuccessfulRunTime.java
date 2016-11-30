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

  @Override
  final public void run() {
    writeLastSuccessfulRunTime(_run(determineLastSuccessfulRunTime()));
  }

  private Date determineLastSuccessfulRunTime() {
    try {
      FileReader fileReader = new FileReader(getTimestampParamterFilename());
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
      try (BufferedWriter writedtparm =
          new BufferedWriter(new FileWriter(getTimestampParamterFilename()))) {
        writedtparm.write(DATE_FORMAT.format(datetime));
      } catch (IOException e) {
        throw new JobsException("Could not write the timestamp parameter file", e);
      }
    }
  }

  private String getTimestampParamterFilename() {
    return this.getClass().getSimpleName() + ".time";
  }

  /**
   * 
   * @param lastSuccessfulRunTime The last successful run
   * 
   * @return The time of the latest run if successful.
   */
  public abstract Date _run(Date lastSuccessfulRunTime);

}
