package gov.ca.cwds.jobs.defaults;

public enum NeutronColumnDefaults {

  /**
   * Common "after" column in "last change" queries.
   */
  SQL_COLUMN_AFTER("after");

  private final String value;

  private NeutronColumnDefaults(String value) {
    this.value = value;
  }

  @SuppressWarnings("javadoc")
  public String getValue() {
    return value;
  }

}
