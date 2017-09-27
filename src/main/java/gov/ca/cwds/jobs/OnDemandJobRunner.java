package gov.ca.cwds.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.inject.JobRunner;

public class OnDemandJobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnDemandJobRunner.class);

  public static void main(String[] args) {
    try {
      LOGGER.info("START CONTINUOUS JOBS");

      JobRunner.registerContinuousJob(EducationProviderContactIndexerJob.class, args);

    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
  }

}
