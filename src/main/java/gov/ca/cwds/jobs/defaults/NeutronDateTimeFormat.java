package gov.ca.cwds.jobs.defaults;

public enum NeutronDateTimeFormat {

  /**
   * Date time format for last run date file.
   */
  LAST_RUN_DATE_FORMAT("yyyy-MM-dd HH:mm:ss"),

  /**
   * Timestamp format for legacy DB2.
   */
  LEGACY_TIMESTAMP_FORMAT("yyyy-MM-dd HH:mm:ss.SSS");

  private final String format;

  private NeutronDateTimeFormat(String format) {
    this.format = format;
  }

  @SuppressWarnings("javadoc")
  public String getFormat() {
    return format;
  }

}
