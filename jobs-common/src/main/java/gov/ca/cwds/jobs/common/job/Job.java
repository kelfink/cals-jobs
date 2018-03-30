package gov.ca.cwds.jobs.common.job;

/**
 * @author CWDS API Team
 */
public interface Job {

  /**
   * Run the job.
   */
  void run();

  default void close() {
  }

}
