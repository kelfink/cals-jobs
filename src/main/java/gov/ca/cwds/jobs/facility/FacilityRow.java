package gov.ca.cwds.jobs.facility;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }
}
