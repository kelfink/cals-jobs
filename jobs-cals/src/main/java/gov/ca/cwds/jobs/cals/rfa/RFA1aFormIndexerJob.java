package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.cals.inject.RFA1aJobModule;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.config.JobOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class RFA1aFormIndexerJob extends BaseIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(RFA1aFormIndexerJob.class);

  public static void main(String[] args) {
    RFA1aFormIndexerJob job = new RFA1aFormIndexerJob();
    job.run(args);
  }

  @Override
  protected AbstractModule getJobModule(JobOptions jobOptions) {
    return new RFA1aJobModule(jobOptions);
  }
}
