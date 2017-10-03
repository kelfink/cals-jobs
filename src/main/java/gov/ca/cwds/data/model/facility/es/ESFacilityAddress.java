package gov.ca.cwds.data.model.facility.es;

import java.io.Serializable;

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
    final int prime = 31;
    int result = 1;
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((county == null) ? 0 : county.hashCode());
    result = prime * result + ((stateCodeType == null) ? 0 : stateCodeType.hashCode());
    result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
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
    ESFacilityAddress other = (ESFacilityAddress) obj;
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
