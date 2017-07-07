package gov.ca.cwds.jobs.util;

/**
 * @author CWDS Elasticsearch Team
 */
public interface JobComponent {

  /**
   * Optionally initialize resources. Default is no-op.
   */
  default void init() {}

  /**
   * Close and de-allocate exclusive resources. Default is no-op.
   */
  default void destroy() {}

}
