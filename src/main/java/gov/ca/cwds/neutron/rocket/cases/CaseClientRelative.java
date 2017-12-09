package gov.ca.cwds.neutron.rocket.cases;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Convenient carrier bean for client/case/relative keys.
 * 
 * @author CWDS API Team
 */
public class CaseClientRelative implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private String focusClientId;
  private String relatedClientId;
  private String caseId;
  private short relationCode;

  public CaseClientRelative(String caseId, String focusClientId, String clientId,
      short relationCode) {
    this.relatedClientId = clientId;
    this.caseId = caseId;
    this.focusClientId = focusClientId;
    this.relationCode = relationCode;
  }

  public static CaseClientRelative extract(final ResultSet rs) throws SQLException {
    final CaseClientRelative ret = new CaseClientRelative(rs.getString("CASE_ID"),
        rs.getString("FOCUS_CHILD_ID"), rs.getString("THIS_CLIENT_ID"), rs.getShort("RELATION"));
    return ret;
  }

  public boolean hasRelation() {
    return StringUtils.isNotBlank(relatedClientId) && !"0".equals(relatedClientId)
        && !relatedClientId.equals(focusClientId);
  }

  public boolean hasNoRelation() {
    return !hasRelation();
  }

  public String getRelatedClientId() {
    return relatedClientId;
  }

  public void setRelatedClientId(String clientId) {
    this.relatedClientId = clientId;
  }

  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String referralId) {
    this.caseId = referralId;
  }

  public String getFocusClientId() {
    return focusClientId;
  }

  public void setFocusClientId(String focusClientId) {
    this.focusClientId = focusClientId;
  }

  public short getRelationCode() {
    return relationCode;
  }

  public void setRelationCode(short relationCode) {
    this.relationCode = relationCode;
  }

  public boolean isParentRelation() {
    return hasRelation() && ((relationCode >= 187 && relationCode <= 214)
        || (relationCode >= 245 && relationCode <= 254)
        || (relationCode >= 282 && relationCode <= 294) || relationCode == 272
        || relationCode == 273 || relationCode == 5620 || relationCode == 6360
        || relationCode == 6361);
  }

  public SystemCode translateRelationship() {
    return SystemCodeCache.global().getSystemCode(this.relationCode);
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
