package gov.ca.cwds.data.persistence.cms;

import org.apache.commons.lang3.StringUtils;

public class EsCaseRelatedPerson extends EsParentPersonCase {

  private static final long serialVersionUID = 1L;

  private String relatedPersonId;

  public boolean hasRelatedPerson() {
    return StringUtils.isNotBlank(getRelatedPersonId());
  }

  public String getRelatedPersonId() {
    return relatedPersonId;
  }

  public void setRelatedPersonId(String relatedPersonId) {
    this.relatedPersonId = relatedPersonId;
  }

}
