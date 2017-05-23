package gov.ca.cwds.data.persistence.cms.rep;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.data.persistence.cms.BaseAddress;
import gov.ca.cwds.data.persistence.cms.CmsPersistentObject;

/**
 * {@link CmsPersistentObject} representing an Address in the replicated schema.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "ADDRS_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ReplicatedAddress extends BaseAddress {

  /**
   * Default constructor.
   */
  public ReplicatedAddress() {
    super();
  }

}
