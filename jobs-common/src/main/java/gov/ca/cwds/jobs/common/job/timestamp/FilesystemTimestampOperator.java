package gov.ca.cwds.jobs.common.job.timestamp;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.Constants;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.rest.api.ApiException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public class FilesystemTimestampOperator implements TimestampOperator {

  private static final Logger LOG = LoggerFactory.getLogger(FilesystemTimestampOperator.class);

  private static final String TIMESTAMP_FILENAME = "LastJobRun.time";
  private String outputDir;

  @Inject
  public FilesystemTimestampOperator(@LastRunDir String outputDir) {
    this.outputDir = outputDir;
  }

  @Override
  public boolean timeStampExists() {
    return getRunningFile().toFile().exists();
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

  @Override
  public LocalDateTime readTimestamp() {
    try {
      return LocalDateTime
          .parse(readLastRunDateTimeString(getRunningFile()), Constants.DATE_TIME_FORMATTER);
    } catch (IOException e) {
      throw new ApiException("Can't parse timestamp from string representation", e);
    }
  }

  private Path getRunningFile() {
    Path path = Paths.get(outputDir, TIMESTAMP_FILENAME).normalize().toAbsolutePath();
    LOG.info("Path to the timestamp file: {}", path.toString());
    return path;
  }

  @Override
  public void writeTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      LOG.info("Timestamp is empty for the batch and will not be recorded");
      return;
    }
    String stringTimestamp = timestamp.format(Constants.DATE_TIME_FORMATTER);
    try {
      if (timeStampExists()) {
        FileUtils.forceDelete(getRunningFile().toFile());
      }
      if (getRunningFile().toFile().createNewFile()) {
        FileUtils.writeStringToFile(getRunningFile().toFile(), stringTimestamp, "UTF-8");
      } else {
        throw new ApiException("Can't create the file " + getRunningFile().normalize().toString());
      }
    } catch (IOException e) {
      throw new ApiException("Can't write timestamp ", e);
    }
  }
}
