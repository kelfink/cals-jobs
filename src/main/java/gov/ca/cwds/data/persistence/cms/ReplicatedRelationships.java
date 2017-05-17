package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.PersistentObject;

public class ReplicatedRelationships implements PersistentObject {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  @JsonIgnore
  private List<ElasticSearchPersonRelationship> relations = new ArrayList<>();

  public ReplicatedRelationships() {
    // Default, no-op.
  }

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

}
