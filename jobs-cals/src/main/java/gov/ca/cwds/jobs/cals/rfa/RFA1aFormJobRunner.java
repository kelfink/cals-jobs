package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.cals.rfa.inject.RFA1aJobModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class RFA1aFormJobRunner extends JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RFA1aFormJobRunner.class);

  public static void main(String[] args) {
    JobRunner.run(new RFA1aJobModule(args));
  }

}
