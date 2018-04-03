package gov.ca.cwds.jobs.common;

import java.time.format.DateTimeFormatter;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public final class Constants {

  private Constants() {
  }

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd-HH:mm:ss.SSSSSSSSS";
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern(DATE_TIME_FORMAT);

}
