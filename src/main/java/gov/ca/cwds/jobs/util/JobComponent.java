package gov.ca.cwds.jobs.util;

/**
 * @author CWDS Elasticsearch Team
 */
public interface JobComponent {

  default void init() throws Exception {}

  /**
   * Close and de-allocate exclusive resources,
   * 
   * @throws Exception on generic error
   */
  default void destroy() throws Exception {}

}
