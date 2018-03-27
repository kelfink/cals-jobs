package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.cals.facility.inject.FacilityJobModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class FacilityJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new FacilityJobModule(args));
  }

}
