package gov.ca.cwds.data.persistence.cms;

public class EsCaseRelatedPerson extends EsParentPersonCase {

  private static final long serialVersionUID = 1L;

  private String relatedPersonId;

  public String getRelatedPersonId() {
    return relatedPersonId;
  }

  public void setRelatedPersonId(String relatedPersonId) {
    this.relatedPersonId = relatedPersonId;
  }

}
