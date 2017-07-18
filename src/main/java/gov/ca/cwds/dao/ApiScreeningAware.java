package gov.ca.cwds.dao;

import java.io.Serializable;

import gov.ca.cwds.data.es.ElasticSearchPersonScreening;

/**
 * Indicates that this class can produce multiple screening records, suitable for nesting in
 * Elasticsearch person documents.
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface ApiScreeningAware extends Serializable {

  /**
   * @return array of screening objects
   */
  ElasticSearchPersonScreening[] getEsScreenings();

}
