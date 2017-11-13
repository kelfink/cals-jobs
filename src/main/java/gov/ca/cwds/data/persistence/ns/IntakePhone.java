package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPhoneAware;

public class IntakePhone implements PersistentObject, ApiPhoneAware {

  private static final long serialVersionUID = 1L;

  private String id;

  private String phoneNumber;

  private ApiPhoneAware.PhoneType phoneType;

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  @Override
  public String getPhoneId() {
    return id;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public String getPhoneNumberExtension() {
    return null;
  }

  @Override
  public PhoneType getPhoneType() {
    return phoneType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setPhoneType(ApiPhoneAware.PhoneType phoneType) {
    this.phoneType = phoneType;
  }

}
