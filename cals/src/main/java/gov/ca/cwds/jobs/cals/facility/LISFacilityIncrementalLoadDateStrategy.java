package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.cals.BaseIncrementalLoadDateStrategy;
import gov.ca.cwds.rest.api.ApiException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author CWDS TPT-2
 */
public final class LISFacilityIncrementalLoadDateStrategy extends BaseIncrementalLoadDateStrategy {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

  private static final String RUNNING_FILE_NAME = "CALS_LIS_Facility_last_load_time";

  @Override
  protected String getDateFileName() {
    return RUNNING_FILE_NAME;
  }

  @Override
  protected DateTimeFormatter getDateTimeFormatter() {
    return DATE_FORMATTER;
  }

  @Override
  public Date calculateDate() {
    try {
      LocalDate date = LocalDate.now();
      Path runningFile = Paths.get(getDateFileLocation());
      String currentDate = date.format(DATE_FORMATTER);

      if (runningFile.toFile().exists()) {
        String lastRunDate = readLastRunDate(runningFile).format(DATE_FORMATTER);
        if (!currentDate.equals(lastRunDate)) {
          // first time for this day
          writeRunDateTime(runningFile, currentDate);
          date = date.minusDays(2);
        } else {
          // not first time for this day
          date = date.minusDays(1);
        }
      } else {
        // first time for this job
        writeRunDateTime(runningFile, currentDate);
        date = date.minusYears(100);
      }
      return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

    } catch (Exception e) {
      throw new ApiException("Failed to calculate date after", e);
    }
  }
}
