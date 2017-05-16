package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.Date;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Represents an Intake Participant or Person.
 * 
 * @author CWDS API Team
 */
public class ReplicatedRelationship implements PersistentObject {

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
    // return StringUtils.isNotBlank(legacyId) ? legacyId : id;
    return null;
  }

  public ElasticSearchPersonRelationship getEsRelationship() {
    ElasticSearchPersonRelationship ret = new ElasticSearchPersonRelationship();

    return ret;
  }

}
