package gov.ca.cwds.jobs.inject;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.JobOptions;

public class JobTask<N extends PersistentObject, D extends ApiGroupNormalizer<?>>
    extends ApiObjectIdentity {

  /**
   * Serialization.
   */
  private static final long serialVersionUID = -1062851921726903377L;

  private static final AtomicInteger taskCounter = new AtomicInteger(0);

  private transient Thread thread;

  private transient BasePersonIndexerJob<N, D> job;

  private final JobOptions opts;

  private final boolean continuousMode;

  private final Date createTime = new Date();

  private final String description;

  private Date startTime;

  private Date endTime;

  public JobTask(BasePersonIndexerJob<N, D> job, boolean continuousMode, JobOptions opts,
      String description) {
    this.job = job;
    this.continuousMode = continuousMode;
    this.opts = opts;
    this.description = description;
  }

}
