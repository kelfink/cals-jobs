package gov.ca.cwds.jobs.util;

import java.util.Date;

public class JobDateUtil {

  private JobDateUtil() {
    // no-op
  }

  public static Date freshDate(Date incoming) {
    return incoming != null ? new Date(incoming.getTime()) : null;
  }

}
