package gov.ca.cwds.jobs.test;

import gov.ca.cwds.data.std.ApiAddressAware;

public final class SimpleAddress implements ApiAddressAware {

  private String city;
  private String county;
  private String state;
  private String streetAddress;
  private String zip;

  public SimpleAddress(String city, String county, String state, String address, String zip) {
    this.city = city;
    this.county = county;
    this.state = state;
    this.streetAddress = address;
    this.zip = zip;
  }

  @Override
  public String getCity() {
    return this.city;
  }

  @Override
  public String getCounty() {
    return this.county;
  }

  @Override
  public String getState() {
    return this.state;
  }

  @Override
  public String getStreetAddress() {
    return this.streetAddress;
  }

  @Override
  public String getZip() {
    return this.zip;
  }

  @Override
  public String getAddressId() {
    return null;
  }

  @Override
  public Short getStateCd() {
    return null;
  }

}
