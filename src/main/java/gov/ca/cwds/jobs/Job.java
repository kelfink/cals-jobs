package gov.ca.cwds.jobs;

/**
 * Marker interface for batch Jobs.
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface Job {

  /**
   * Run the job.
   */
  public void run();

}
