package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.common.inject.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class CwsFacilityJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new CwsFacilityJobModule(args));
  }
}
