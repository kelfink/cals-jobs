package gov.ca.cwds.jobs.cals;

import com.google.inject.Inject;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.rest.api.ApiException;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

/**
 * @author CWDS TPT-2
 */
public abstract class BaseIncrementalLoadDateStrategy implements IncrementalLoadDateStrategy {

  private static final String DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss.SSS";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern(DATE_FORMAT);

  @Inject
  private JobOptions jobOptions;

  protected abstract String getDateFileName();

  private String getDateFileLocation(String timeFilesDir) {
    return FilenameUtils.concat(timeFilesDir, getDateFileName());
  }

  protected String getDateFileLocation() {
    return getDateFileLocation(jobOptions.getLastRunLoc());
  }

  public final boolean reset(String timeFilesDir) throws IOException {
    return Files.deleteIfExists(Paths.get(getDateFileLocation(timeFilesDir)));
  }

  protected DateTimeFormatter getDateTimeFormatter() {
    return DATE_TIME_FORMATTER;
  }

  protected LocalDateTime getDateTimeForInitialLoad() {
    return LocalDateTime.now().minusYears(100);
  }

  @Override
  public LocalDateTime calculateLocalDateTime() {
    try {
      LocalDateTime now = LocalDateTime.now();
      Path runningFile = Paths.get(getDateFileLocation());

      LocalDateTime result = runningFile.toFile().exists() ? readLastRunDateTime(runningFile)
          : getDateTimeForInitialLoad();

      writeRunDateTime(runningFile, now);

      return result;

    } catch (Exception e) {
      throw new ApiException("Failed to calculate date", e);
    }
  }

  @Override
  public Date calculateDate() {
    LocalDateTime result = calculateLocalDateTime();
    return result == null ? null : Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
  }

  private String readLastRunDateTimeString(Path runningFile) throws IOException {
    try (Stream<String> stream = Files.lines(runningFile)) {
      Optional<String> firstLine = stream.findFirst();
      if (!firstLine.isPresent()) {
        throw new ApiException("Corrupted date file: " + runningFile);
      }
      return firstLine.get();
    }
  }

  protected LocalDateTime readLastRunDateTime(Path runningFile) throws IOException {
    return LocalDateTime.parse(readLastRunDateTimeString(runningFile), getDateTimeFormatter());
  }

  protected LocalDate readLastRunDate(Path runningFile) throws IOException {
    return LocalDate.parse(readLastRunDateTimeString(runningFile), getDateTimeFormatter());
  }

  protected void writeRunDateTime(Path runningFile, LocalDateTime dateTime) throws IOException {
    writeRunDateTime(runningFile, dateTime.format(getDateTimeFormatter()));
  }

  protected void writeRunDateTime(Path runningFile, String dateTime) throws IOException {
    Files.write(runningFile, dateTime.getBytes(), WRITE,
        runningFile.toFile().exists() ? TRUNCATE_EXISTING : CREATE_NEW);
  }
}
