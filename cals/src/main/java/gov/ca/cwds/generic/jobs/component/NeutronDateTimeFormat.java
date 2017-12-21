package gov.ca.cwds.generic.jobs.component;

public enum NeutronDateTimeFormat {

  /**
   * Date time format for last run date file.
   */
  LAST_RUN_DATE_FORMAT("yyyy-MM-dd HH:mm:ss"),

  /**
   * Common timestamp format for legacy DB.
   */
  LEGACY_TIMESTAMP_FORMAT("yyyy-MM-dd HH:mm:ss.SSS");

  private final String format;

  private NeutronDateTimeFormat(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

}
