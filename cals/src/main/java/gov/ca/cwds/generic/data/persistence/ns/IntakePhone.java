package gov.ca.cwds.generic.data.persistence.ns;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPhoneAware;
import java.io.Serializable;

public class IntakePhone implements PersistentObject, ApiPhoneAware {

  private String id;

  private String phoneNumber;

  private PhoneType phoneType;

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

  public void setPhoneType(PhoneType phoneType) {
    this.phoneType = phoneType;
  }

}
