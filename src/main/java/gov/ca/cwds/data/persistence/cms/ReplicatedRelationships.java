package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Pseudo-normalized container for CMS legacy relationships by legacy person id.
 * 
 * @author CWDS API Team
 */
public class ReplicatedRelationships extends ApiObjectIdentity
    implements PersistentObject, ApiPersonAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Legacy person id.
   */
  private String id;

  // @JsonIgnore
  private List<ElasticSearchPersonRelationship> relations = new ArrayList<>();

  /**
   * Default constructor.
   */
  public ReplicatedRelationships() {
    // Default, no-op.
  }

  /**
   * Preferred constructor.
   * 
   * @param id legacy id
   */
  public ReplicatedRelationships(String id) {
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

  public List<ElasticSearchPersonRelationship> getRelations() {
    return relations;
  }

  public void setRelations(List<ElasticSearchPersonRelationship> relations) {
    this.relations = relations;
  }

  public void addRelation(ElasticSearchPersonRelationship relation) {
    this.relations.add(relation);
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
