package gov.ca.cwds.dao;

import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;

/**
 * @author CWDS API Team
 */
@FunctionalInterface
public interface ApiClientSafetyAlertsAware {

  /**
   * Get client safety alerts.
   * 
   * @return The client safety alerts
   */
  List<ElasticSearchSafetyAlert> getClientSafetyAlerts();

}
