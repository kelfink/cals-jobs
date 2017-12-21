package gov.ca.cwds.generic.jobs.component;

public enum NeutronIntegerDefaults {

  DEFAULT_BATCH_WAIT(25),

  SLEEP_MILLIS(2000),

  POLL_MILLIS(1000),

  DEFAULT_BUCKETS(1),

  DEFAULT_FETCH_SIZE(5000);

  private final int value;

  private NeutronIntegerDefaults(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
