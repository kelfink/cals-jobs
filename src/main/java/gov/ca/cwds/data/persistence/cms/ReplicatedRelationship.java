package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;

import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Represents an abstract relationship from this person to another.
 * 
 * @author CWDS API Team
 */
public class ReplicatedRelationship implements PersistentObject {

  private static final long serialVersionUID = 1L;

  private String thisLegacyTable;
  private String relatedLegacyTable;
  private String thisFirstName;
  private String thisLastName;
  private String relCode;
  private String relatedLegacyId;
  private String relatedFirstName;
  private String relatedLastName;

  public String getThisLegacyTable() {
    return thisLegacyTable;
  }

  public void setThisLegacyTable(String thisLegacyTable) {
    this.thisLegacyTable = thisLegacyTable;
  }

  public String getRelatedLegacyTable() {
    return relatedLegacyTable;
  }

  public void setRelatedLegacyTable(String relatedLegacyTable) {
    this.relatedLegacyTable = relatedLegacyTable;
  }

  public String getThisFirstName() {
    return thisFirstName;
  }

  public void setThisFirstName(String thisFirstName) {
    this.thisFirstName = thisFirstName;
  }

  public String getThisLastName() {
    return thisLastName;
  }

  public void setThisLastName(String thisLastName) {
    this.thisLastName = thisLastName;
  }

  public String getRelCode() {
    return relCode;
  }

  public void setRelCode(String relCode) {
    this.relCode = relCode;
  }

  public String getRelatedLegacyId() {
    return relatedLegacyId;
  }

  public void setRelatedLegacyId(String relatedLegacyId) {
    this.relatedLegacyId = relatedLegacyId;
  }

  public String getRelatedFirstName() {
    return relatedFirstName;
  }

  public void setRelatedFirstName(String relatedFirstName) {
    this.relatedFirstName = relatedFirstName;
  }

  public String getRelatedLastName() {
    return relatedLastName;
  }

  public void setRelatedLastName(String relatedLastName) {
    this.relatedLastName = relatedLastName;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

}
