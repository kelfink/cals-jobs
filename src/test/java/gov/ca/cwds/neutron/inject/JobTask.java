package gov.ca.cwds.neutron.inject;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;

public class JobTask<N extends PersistentObject, D extends ApiGroupNormalizer<?>>
    extends ApiObjectIdentity {

  /**
   * Serialization.
   */
  private static final long serialVersionUID = -1062851921726903377L;

  private static final AtomicInteger taskCounter = new AtomicInteger(0);

  private transient Thread thread;

  private transient BasePersonRocket<N, D> job;

  private final FlightPlan opts;

  private final boolean continuousMode;

  private final Date createTime = new Date();

  private final String description;

  private Date startTime;

  private Date endTime;

  public JobTask(BasePersonRocket<N, D> job, boolean continuousMode, FlightPlan opts,
      String description) {
    this.job = job;
    this.continuousMode = continuousMode;
    this.opts = opts;
    this.description = description;
  }

}
