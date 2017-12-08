package gov.ca.cwds.data.persistence.cms;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

}
