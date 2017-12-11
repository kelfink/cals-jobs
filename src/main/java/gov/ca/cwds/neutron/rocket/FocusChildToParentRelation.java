package gov.ca.cwds.neutron.rocket;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.ca.cwds.data.std.ApiMarker;

public class FocusChildToParentRelation implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private String focusChildId;
  private String parentId;
  private String relationship;

  public FocusChildToParentRelation() {
    // default ctor
  }

  public FocusChildToParentRelation(String focusChildId, String parentId, String relationship) {
    this.focusChildId = focusChildId;
    this.parentId = parentId;
    this.relationship = relationship;
  }

  public String getFocusChildId() {
    return focusChildId;
  }

  public void setFocusChildId(String focusChildId) {
    this.focusChildId = focusChildId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String focusToParentRelationship) {
    this.relationship = focusToParentRelationship;
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
