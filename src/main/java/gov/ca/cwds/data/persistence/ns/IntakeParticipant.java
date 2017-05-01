package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.Date;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Represents an Intake Participant or Person.
 * 
 * @author CWDS API Team
 */
public class IntakeParticipant implements PersistentObject, ApiPersonAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private String firstName;

  private String lastName;

  private Date birthDate;

  private String gender;

  private String ssn;

  @Override
  public Serializable getPrimaryKey() {
    return this.id;
  }

  @Override
  public Date getBirthDate() {
    return this.birthDate;
  }

  @Override
  public String getFirstName() {
    return this.firstName;
  }

  @Override
  public String getGender() {
    return this.gender;
  }

  @Override
  public String getLastName() {
    return this.lastName;
  }

  @Override
  public String getMiddleName() {
    return null;
  }

  @Override
  public String getNameSuffix() {
    return null;
  }

  @Override
  public String getSsn() {
    return this.ssn;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

}
