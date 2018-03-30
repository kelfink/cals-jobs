package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.common.job.impl.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class RFA1aFormJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new RFA1aJobModule(args));
  }

}
