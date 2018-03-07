package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.cals.facility.inject.FacilityJobModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class FacilityJobRunner {

  private static final Logger LOG = LoggerFactory.getLogger(FacilityJobRunner.class);

  public static void main(String[] args) {
    JobRunner.run(new FacilityJobModule(args));
  }

}
