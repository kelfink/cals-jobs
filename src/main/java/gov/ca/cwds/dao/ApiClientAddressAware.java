package gov.ca.cwds.dao;

import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.std.ApiAddressAware;

public interface ApiClientAddressAware {


  /**
   * Getter for client address start date.
   * 
   * @return client address start date
   */
  Date getClientAddressEffectiveStartDate();

  /**
   * Getter for client address end date.
   * 
   * @return client address end date
   */
  Date getClientAddressEffectiveEndDate();

  /**
   * Get client address type
   * 
   * @return Client address type
   */
  Short getClientAddressType();

  /**
   * Get list of addresses
   * 
   * @return List of addresses
   */
  List<ApiAddressAware> getAddresses();

}
