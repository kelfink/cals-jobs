package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Represents an Intake Participant or Person.
 * 
 * @author CWDS API Team
 */
public class ReplicatedRelationship implements PersistentObject, ApiLegacyAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private String legacyId;

  private String firstName;

  private String lastName;

  private Date birthDate;

  private String gender;

  private String ssn;

  /**
   * Update section JSON is the participant's screenings.
   * 
   * @return JSON to update document only
   */
  public String buildUpdateJson() {
    return "";
  }

  @Override
  public Serializable getPrimaryKey() {
    return StringUtils.isNotBlank(legacyId) ? legacyId : id;
  }


  @Override
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

  @Override
  public String getLegacyId() {
    return legacyId;
  }

  public void setLegacyId(String legacyId) {
    this.legacyId = legacyId;
  }

  public ElasticSearchPersonRelationship getEsRelationship() {
    ElasticSearchPersonRelationship ret = new ElasticSearchPersonRelationship();

    return ret;
  }

}
