package gov.ca.cwds.jobs.defaults;

public enum NeutronIntegerDefaults {

  WAIT_BULK_PROCESSOR(25),

  SLEEP_MILLIS(2000),

  POLL_MILLIS(1000),

  DEFAULT_BUCKETS(1),

  /**
   * To avoid missing changed records, look N minutes before the last successful run timestamp.
   * NOTE: make configurable.
   */
  LOOKBACK_MINUTES(-25),

  FETCH_SIZE(5000)

  ;

  private final int value;

  private NeutronIntegerDefaults(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
