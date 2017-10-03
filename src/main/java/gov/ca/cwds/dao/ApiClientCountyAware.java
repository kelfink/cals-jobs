package gov.ca.cwds.dao;

import java.io.Serializable;

/**
 * @author CWDS API Team
 */
public interface ApiClientCountyAware extends Serializable {

  /**
   * Get the unique identifier of this record.
   * 
   * @return primary key
   */
  Short getClientCounty();
}
