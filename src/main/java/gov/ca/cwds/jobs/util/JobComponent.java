package gov.ca.cwds.jobs.util;

import gov.ca.cwds.jobs.exception.NeutronException;

/**
 * @author CWDS Elasticsearch Team
 */
public interface JobComponent {

  /**
   * Optionally initialize resources. Default is no-op.
   * 
   * @throws NeutronException checked exception
   */
  default void init() throws NeutronException {}

  /**
   * Close and de-allocate exclusive resources. Default is no-op.
   */
  default void destroy() {}

}
