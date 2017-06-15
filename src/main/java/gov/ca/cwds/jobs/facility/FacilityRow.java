package gov.ca.cwds.jobs.facility;

import java.io.Serializable;
import java.util.Date;

import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * @author CWDS Elasticsearch Team
 */
public class FacilityRow implements PersistentObject {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;
  private String type;
  private String name;
  private String licenseeName;
  private String assignedWorker;
  private String districtOffice;
  private String licenseNumber;
  private String licenseStatus;
  private String capacity;
  private Date licenseEffectiveDate;
  private Date originalApplicationReceivedDate;
  private Date lastVisitDate;
  private String lastVisitReason;
  private String primaryPhoneNumber;
  private String altPhoneNumber;
  private String stateCodeType;
  private String zipCode;
  private String zipSuffixCode;
  private String streetAddress;
  private String city;
  private String county;

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
    return licenseEffectiveDate;
  }

  public void setLicenseEffectiveDate(Date licenseEffectiveDate) {
    this.licenseEffectiveDate = licenseEffectiveDate;
  }

  public Date getOriginalApplicationReceivedDate() {
    return originalApplicationReceivedDate;
  }

  public void setOriginalApplicationReceivedDate(Date originalApplicationReceivedDate) {
    this.originalApplicationReceivedDate = originalApplicationReceivedDate;
  }

  public Date getLastVisitDate() {
    return lastVisitDate;
  }

  public void setLastVisitDate(Date lastVisitDate) {
    this.lastVisitDate = lastVisitDate;
  }

  public String getLastVisitReason() {
    return lastVisitReason;
  }

  public void setLastVisitReason(String lastVisitReason) {
    this.lastVisitReason = lastVisitReason;
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

  public String getStateCodeType() {
    return stateCodeType;
  }

  public void setStateCodeType(String stateCodeType) {
    this.stateCodeType = stateCodeType;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getZipSuffixCode() {
    return zipSuffixCode;
  }

  public void setZipSuffixCode(String zipSuffixCode) {
    this.zipSuffixCode = zipSuffixCode;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((altPhoneNumber == null) ? 0 : altPhoneNumber.hashCode());
    result = prime * result + ((assignedWorker == null) ? 0 : assignedWorker.hashCode());
    result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
    result = prime * result + ((city == null) ? 0 : city.hashCode());
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
    result = prime * result + ((stateCodeType == null) ? 0 : stateCodeType.hashCode());
    result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
    result = prime * result + ((zipSuffixCode == null) ? 0 : zipSuffixCode.hashCode());
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
    FacilityRow other = (FacilityRow) obj;
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
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
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
    if (stateCodeType == null) {
      if (other.stateCodeType != null)
        return false;
    } else if (!stateCodeType.equals(other.stateCodeType))
      return false;
    if (streetAddress == null) {
      if (other.streetAddress != null)
        return false;
    } else if (!streetAddress.equals(other.streetAddress))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (zipCode == null) {
      if (other.zipCode != null)
        return false;
    } else if (!zipCode.equals(other.zipCode))
      return false;
    if (zipSuffixCode == null) {
      if (other.zipSuffixCode != null)
        return false;
    } else if (!zipSuffixCode.equals(other.zipSuffixCode))
      return false;
    return true;
  }
}
