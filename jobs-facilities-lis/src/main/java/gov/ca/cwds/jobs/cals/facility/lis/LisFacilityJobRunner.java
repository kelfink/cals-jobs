package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.common.inject.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class LisFacilityJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new LisFacilityJobModule(args));
  }

}
