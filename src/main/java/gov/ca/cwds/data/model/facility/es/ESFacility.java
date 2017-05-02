package gov.ca.cwds.data.model.facility.es;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.data.persistence.PersistentObject;

import java.io.Serializable;

/**
 * Created by dmitry.rudenko on 5/2/2017.
 */

public class ESFacility implements PersistentObject{
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

    public String getLicenseEffectiveDate() {
        return licenseEffectiveDate;
    }

    public void setLicenseEffectiveDate(String licenseEffectiveDate) {
        this.licenseEffectiveDate = licenseEffectiveDate;
    }

    public String getOriginalApplicationReceivedDate() {
        return originalApplicationReceivedDate;
    }

    public void setOriginalApplicationReceivedDate(String originalApplicationReceivedDate) {
        this.originalApplicationReceivedDate = originalApplicationReceivedDate;
    }

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
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
}
