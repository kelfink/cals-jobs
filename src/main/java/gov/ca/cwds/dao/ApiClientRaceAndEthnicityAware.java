package gov.ca.cwds.dao;

import gov.ca.cwds.data.es.ElasticSearchRaceAndEthnicity;

/**
 * @author CWDS API Team
 */
@FunctionalInterface
public interface ApiClientRaceAndEthnicityAware {

  /**
   * Get client race.
   * 
   * @return The client race
   */
  ElasticSearchRaceAndEthnicity getRaceAndEthnicity();

}
