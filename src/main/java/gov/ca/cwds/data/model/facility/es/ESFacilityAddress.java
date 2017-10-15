package gov.ca.cwds.data.model.facility.es;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author CWDS Elasticsearch Team
 */
public class ESFacilityAddress implements Serializable {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  @JsonProperty("state_code_type")
  private String stateCodeType;

  @JsonProperty("zip_code")
  private String zipCode;

  @JsonProperty("zip_suffix_code")
  private String zipSuffixCode;

  @JsonProperty("street_address")
  private String streetAddress;

  @JsonProperty("city")
  private String city;

  @JsonProperty("county")
  private String county;

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
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }
}

