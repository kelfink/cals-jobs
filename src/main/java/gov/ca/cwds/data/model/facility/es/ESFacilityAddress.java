package gov.ca.cwds.data.model.facility.es;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dmitry.rudenko on 5/2/2017.
 */
public class ESFacilityAddress {
    @JsonProperty("street_number")
    private String streetNumber;
    @JsonProperty("street_name")
    private String streetName;
    @JsonProperty("state_code_type")
    private String stateCodeType;
    @JsonProperty("zip_code")
    private String zipCode;
    @JsonProperty("zip_suffix_code")
    private String zipSuffixCode;

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
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
}
