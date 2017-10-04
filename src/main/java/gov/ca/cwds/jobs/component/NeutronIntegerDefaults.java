package gov.ca.cwds.jobs.component;

import gov.ca.cwds.dao.cms.BatchDaoImpl;

public enum NeutronIntegerDefaults {

  DEFAULT_BATCH_WAIT(25),

  SLEEP_MILLIS(2500),

  POLL_MILLIS(1000),

  DEFAULT_FETCH_SIZE(BatchDaoImpl.DEFAULT_FETCH_SIZE);

  private final int value;

  private NeutronIntegerDefaults(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
