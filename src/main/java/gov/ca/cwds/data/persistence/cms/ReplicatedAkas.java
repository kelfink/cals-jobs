package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPersonAka;
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
   * Legacy person id, the client id.
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

  @SuppressWarnings("javadoc")
  public String getId() {
    return id;
  }

  @SuppressWarnings("javadoc")
  public void setId(String id) {
    this.id = id;
  }

  @SuppressWarnings("javadoc")
  public List<ElasticSearchPersonAka> getAkas() {
    return akas;
  }

  @SuppressWarnings("javadoc")
  public void setAkas(List<ElasticSearchPersonAka> akas) {
    this.akas = akas;
  }

  @SuppressWarnings("javadoc")
  public void addAka(ElasticSearchPersonAka aka) {
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
