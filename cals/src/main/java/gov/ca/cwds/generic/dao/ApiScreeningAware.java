package gov.ca.cwds.generic.dao;

import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import java.io.Serializable;

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
