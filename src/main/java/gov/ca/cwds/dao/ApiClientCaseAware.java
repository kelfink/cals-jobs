package gov.ca.cwds.dao;

/**
 * @author CWDS API Team
 */
@FunctionalInterface
public interface ApiClientCaseAware {

  /**
   * Get client's open case id
   * 
   * @return The open case id
   */
  String getOpenCaseId();

}
