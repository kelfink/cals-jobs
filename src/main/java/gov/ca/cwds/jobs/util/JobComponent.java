package gov.ca.cwds.jobs.util;

/**
 * @author CWDS Elasticsearch Team
 */
public interface JobComponent {

  default void init() throws Exception {}

  default void destroy() throws Exception {}

}
