package gov.ca.cwds.jobs;

import gov.ca.cwds.jobs.schedule.JobDirector;

/**
 * Original Attorney job now in class {@link OrigAttorneyIndexerJob}.
 * 
 * <p>
 * This job is a temporary facade to test {@link JobDirector}, until DevOps adds a new job.
 * </p>
 * 
 * @author CWDS API Team
 */
public class AttorneyIndexerJob {

  private AttorneyIndexerJob() {
    // no-op
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobDirector.main(args);
  }

}
