package gov.ca.cwds.generic.data.persistence.cms.rep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseClientAddress;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

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
public class ReplicatedClientAddress extends BaseClientAddress implements CmsReplicatedEntity {

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "IDENTIFIER", referencedColumnName = "FKADDRS_T", insertable = false,
      updatable = false, unique = false)
  protected transient Set<ReplicatedAddress> addresses = new LinkedHashSet<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

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

  @Override
  public String getLegacyId() {
    return this.getPrimaryKey();
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return null;
  }

  // =======================
  // CmsReplicatedEntity:
  // =======================

  @Override
  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  @Override
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  @Override
  public Date getReplicationDate() {
    return replicationDate;
  }

  @Override
  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = replicationDate;
  }

}
