package gov.ca.cwds.generic.dao;

import gov.ca.cwds.data.std.ApiPersonAware;
import java.io.Serializable;

/**
 * Indicates that this class can produce multiple person records, suitable as Elasticsearch
 * documents.
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface ApiMultiplePersonAware extends Serializable {

  /**
   * @return array of person objects
   */
  ApiPersonAware[] getPersons();

}
