package gov.ca.cwds.data.persistence.cms;

public class EsCaseRelatedPerson extends EsParentPersonCase {

  private static final long serialVersionUID = 1L;

  private int relatedPersonId;

  public int getRelatedPersonId() {
    return relatedPersonId;
  }

  public void setRelatedPersonId(int relatedPersonId) {
    this.relatedPersonId = relatedPersonId;
  }

}
