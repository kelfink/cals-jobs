package gov.ca.cwds.jobs.cals.facility.lisfas;

import gov.ca.cwds.jobs.cals.facility.lisfas.inject.LisFacilityJobModule;
import gov.ca.cwds.jobs.common.core.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class LisFacilityJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new LisFacilityJobModule(args));
  }

}
