package gov.ca.cwds.jobs.util;

/**
 * @author CWDS Elasticsearch Team
 */
public interface JobComponent {

  /**
   * Optionally initialize resources. Default is no-op.
   * 
   * @throws Exception on generic error
   */
  default void init() throws Exception {}

  /**
   * Close and de-allocate exclusive resources. Default is no-op.
   * 
   * @throws Exception on generic error
   */
  default void destroy() throws Exception {}

}
