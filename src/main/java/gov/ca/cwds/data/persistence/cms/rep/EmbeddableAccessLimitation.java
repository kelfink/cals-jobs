package gov.ca.cwds.data.persistence.cms.rep;

import static gov.ca.cwds.neutron.util.transform.JobTransformUtils.ifNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.neutron.util.NeutronDateUtils;

@Embeddable
public class EmbeddableAccessLimitation implements ApiMarker {

  private static final long serialVersionUID = 1L;

  @Column(name = "LIMITED_ACCESS_CODE")
  private String limitedAccessCode;

  @Column(name = "LIMITED_ACCESS_DATE")
  @Type(type = "date")
  private Date limitedAccessDate;
  @Column(name = "LIMITED_ACCESS_DESCRIPTION")

  private String limitedAccessDescription;
  @Column(name = "LIMITED_ACCESS_GOVERNMENT_ENT")
  @Type(type = "integer")
  private Integer limitedAccessGovernmentEntityId;

  public void accept(final ResultSet rs) throws SQLException {
    this.limitedAccessCode = ifNull(rs.getString("LIMITED_ACCESS_CODE"));
    this.limitedAccessDate = rs.getDate("LIMITED_ACCESS_DATE");
    this.limitedAccessDescription = ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION"));
    this.limitedAccessGovernmentEntityId = rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT");
  }

  public String getLimitedAccessCode() {
    return limitedAccessCode;
  }

  public void setLimitedAccessCode(String limitedAccessCode) {
    this.limitedAccessCode = limitedAccessCode;
  }

  public Date getLimitedAccessDate() {
    return NeutronDateUtils.freshDate(limitedAccessDate);
  }

  public void setLimitedAccessDate(Date limitedAccessDate) {
    this.limitedAccessDate = NeutronDateUtils.freshDate(limitedAccessDate);
  }

  public String getLimitedAccessDescription() {
    return limitedAccessDescription;
  }

  public void setLimitedAccessDescription(String limitedAccessDescription) {
    this.limitedAccessDescription = limitedAccessDescription;
  }

  public Integer getLimitedAccessGovernmentEntityId() {
    return limitedAccessGovernmentEntityId;
  }

  public void setLimitedAccessGovernmentEntityId(Integer limitedAccessGovernmentEntityId) {
    this.limitedAccessGovernmentEntityId = limitedAccessGovernmentEntityId;
  }

}
