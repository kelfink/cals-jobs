package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAka;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Pseudo-normalized container for CMS legacy "other client names" by legacy client id.
 * 
 * @author CWDS API Team
 */
public class ReplicatedAkas extends ApiObjectIdentity implements PersistentObject, ApiPersonAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Legacy person id.
   */
  private String id;

  // @JsonIgnore
  private List<ElasticSearchPersonAka> akas = new ArrayList<>();

  /**
   * Default constructor.
   */
  public ReplicatedAkas() {
    // Default, no-op.
  }

  /**
   * Preferred constructor.
   * 
   * @param id legacy id
   */
  public ReplicatedAkas(String id) {
    this.id = id;
  }

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ElasticSearchPersonAka> getAkas() {
    return akas;
  }

  public void setRelations(List<ElasticSearchPersonAka> akas) {
    this.akas = akas;
  }

  public void addRelation(ElasticSearchPersonAka aka) {
    this.akas.add(aka);
  }

  @Override
  public Date getBirthDate() {
    return null;
  }

  @Override
  public String getFirstName() {
    return null;
  }

  @Override
  public String getGender() {
    return null;
  }

  @Override
  public String getLastName() {
    return null;
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
    return null;
  }

}
