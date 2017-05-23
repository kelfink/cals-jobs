package gov.ca.cwds.data.persistence.cms.rep;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseClientAddress;

/**
 * {@link PersistentObject} representing a Client Address in the replicated schema.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CL_ADDRT")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ReplicatedClientAddress extends BaseClientAddress {

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "IDENTIFIER", referencedColumnName = "FKADDRS_T", insertable = false,
      updatable = false, unique = false)
  protected transient Set<ReplicatedAddress> addresses = new LinkedHashSet<>();

  /**
   * Default constructor
   * 
   * Required for Hibernate
   */
  public ReplicatedClientAddress() {
    super();
  }

  /**
   * Getter for addresses. Returns underlying member, not a deep copy.
   * 
   * @return Set of addresses
   */
  public Set<ReplicatedAddress> getAddresses() {
    return addresses;
  }

  /**
   * Setter for addresses.
   * 
   * @param addresses addresses to set
   */
  public void setAddresses(Set<ReplicatedAddress> addresses) {
    if (addresses != null) {
      this.addresses = addresses;
    } else {
      this.addresses = new LinkedHashSet<>();
    }
  }

  /**
   * Add an address.
   * 
   * @param address to add
   */
  public void addAddress(ReplicatedAddress address) {
    if (address != null) {
      addresses.add(address);
    }
  }

}
