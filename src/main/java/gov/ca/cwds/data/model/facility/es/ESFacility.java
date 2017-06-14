package gov.ca.cwds.data.model.facility.es;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.rest.api.domain.DomainChef;

/**
 * 
 * @author CWDS Elasticsearch Team
 */
public class ESFacility implements PersistentObject {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("name")
  private String name;

  @JsonProperty("licensee_name")
  private String licenseeName;

  @JsonProperty("assigned_worker")
  private String assignedWorker;

  @JsonProperty("district_office")
  private String districtOffice;

  @JsonProperty("license_number")
  private String licenseNumber;

  @JsonProperty("license_status")
  private String licenseStatus;

  @JsonProperty("capacity")
  private String capacity;

  @JsonProperty("license_effective_date")
  private String licenseEffectiveDate;

  @JsonProperty("original_application_received_date")
  private String originalApplicationReceivedDate;

  @JsonProperty("last_visit_date")
  private String lastVisitDate;

  @JsonProperty("last_visit_reason")
  private String lastVisitReason;

  @JsonProperty("county")
  private String county;

  @JsonProperty("primary_phone_number")
  private String primaryPhoneNumber;

  @JsonProperty("alt_phone_number")
  private String altPhoneNumber;

  @JsonProperty("address")
  private ESFacilityAddress address;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLicenseeName() {
    return licenseeName;
  }

  public void setLicenseeName(String licenseeName) {
    this.licenseeName = licenseeName;
  }

  public String getAssignedWorker() {
    return assignedWorker;
  }

  public void setAssignedWorker(String assignedWorker) {
    this.assignedWorker = assignedWorker;
  }

  public String getDistrictOffice() {
    return districtOffice;
  }

  public void setDistrictOffice(String districtOffice) {
    this.districtOffice = districtOffice;
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }

  public String getLicenseStatus() {
    return licenseStatus;
  }

  public void setLicenseStatus(String licenseStatus) {
    this.licenseStatus = licenseStatus;
  }

  public String getCapacity() {
    return capacity;
  }

  public void setCapacity(String capacity) {
    this.capacity = capacity;
  }

  public Date getLicenseEffectiveDate() {
    return DomainChef.uncookDateString(licenseEffectiveDate);
  }

  public void setLicenseEffectiveDate(Date licenseEffectiveDate) {
    this.licenseEffectiveDate = DomainChef.cookDate(licenseEffectiveDate);
  }

  public Date getOriginalApplicationReceivedDate() {
    return DomainChef.uncookDateString(originalApplicationReceivedDate);
  }

  public void setOriginalApplicationReceivedDate(Date originalApplicationReceivedDate) {
    this.originalApplicationReceivedDate = DomainChef.cookDate(originalApplicationReceivedDate);
  }

  public Date getLastVisitDate() {
    return DomainChef.uncookDateString(lastVisitDate);
  }

  public void setLastVisitDate(Date lastVisitDate) {
    this.lastVisitDate = DomainChef.cookDate(lastVisitDate);
  }

  public String getLastVisitReason() {
    return lastVisitReason;
  }

  public void setLastVisitReason(String lastVisitReason) {
    this.lastVisitReason = lastVisitReason;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getPrimaryPhoneNumber() {
    return primaryPhoneNumber;
  }

  public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
    this.primaryPhoneNumber = primaryPhoneNumber;
  }

  public String getAltPhoneNumber() {
    return altPhoneNumber;
  }

  public void setAltPhoneNumber(String altPhoneNumber) {
    this.altPhoneNumber = altPhoneNumber;
  }

  public ESFacilityAddress getAddress() {
    return address;
  }

  public void setAddress(ESFacilityAddress address) {
    this.address = address;
  }

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + ((altPhoneNumber == null) ? 0 : altPhoneNumber.hashCode());
    result = prime * result + ((assignedWorker == null) ? 0 : assignedWorker.hashCode());
    result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
    result = prime * result + ((county == null) ? 0 : county.hashCode());
    result = prime * result + ((districtOffice == null) ? 0 : districtOffice.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((lastVisitDate == null) ? 0 : lastVisitDate.hashCode());
    result = prime * result + ((lastVisitReason == null) ? 0 : lastVisitReason.hashCode());
    result =
        prime * result + ((licenseEffectiveDate == null) ? 0 : licenseEffectiveDate.hashCode());
    result = prime * result + ((licenseNumber == null) ? 0 : licenseNumber.hashCode());
    result = prime * result + ((licenseStatus == null) ? 0 : licenseStatus.hashCode());
    result = prime * result + ((licenseeName == null) ? 0 : licenseeName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((originalApplicationReceivedDate == null) ? 0
        : originalApplicationReceivedDate.hashCode());
    result = prime * result + ((primaryPhoneNumber == null) ? 0 : primaryPhoneNumber.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ESFacility other = (ESFacility) obj;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (altPhoneNumber == null) {
      if (other.altPhoneNumber != null)
        return false;
    } else if (!altPhoneNumber.equals(other.altPhoneNumber))
      return false;
    if (assignedWorker == null) {
      if (other.assignedWorker != null)
        return false;
    } else if (!assignedWorker.equals(other.assignedWorker))
      return false;
    if (capacity == null) {
      if (other.capacity != null)
        return false;
    } else if (!capacity.equals(other.capacity))
      return false;
    if (county == null) {
      if (other.county != null)
        return false;
    } else if (!county.equals(other.county))
      return false;
    if (districtOffice == null) {
      if (other.districtOffice != null)
        return false;
    } else if (!districtOffice.equals(other.districtOffice))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (lastVisitDate == null) {
      if (other.lastVisitDate != null)
        return false;
    } else if (!lastVisitDate.equals(other.lastVisitDate))
      return false;
    if (lastVisitReason == null) {
      if (other.lastVisitReason != null)
        return false;
    } else if (!lastVisitReason.equals(other.lastVisitReason))
      return false;
    if (licenseEffectiveDate == null) {
      if (other.licenseEffectiveDate != null)
        return false;
    } else if (!licenseEffectiveDate.equals(other.licenseEffectiveDate))
      return false;
    if (licenseNumber == null) {
      if (other.licenseNumber != null)
        return false;
    } else if (!licenseNumber.equals(other.licenseNumber))
      return false;
    if (licenseStatus == null) {
      if (other.licenseStatus != null)
        return false;
    } else if (!licenseStatus.equals(other.licenseStatus))
      return false;
    if (licenseeName == null) {
      if (other.licenseeName != null)
        return false;
    } else if (!licenseeName.equals(other.licenseeName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (originalApplicationReceivedDate == null) {
      if (other.originalApplicationReceivedDate != null)
        return false;
    } else if (!originalApplicationReceivedDate.equals(other.originalApplicationReceivedDate))
      return false;
    if (primaryPhoneNumber == null) {
      if (other.primaryPhoneNumber != null)
        return false;
    } else if (!primaryPhoneNumber.equals(other.primaryPhoneNumber))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
}
