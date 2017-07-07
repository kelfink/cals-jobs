package gov.ca.cwds.dao;

import java.io.Serializable;

import gov.ca.cwds.data.std.ApiPersonAware;

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
