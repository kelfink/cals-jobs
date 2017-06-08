package gov.ca.cwds.data.persistence.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity bean for Materialized Query Table (MQT), ES_CASE_HIST for parent person cases.
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "ES_CASE_HIST")
@NamedNativeQueries({@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsParentPersonCase.findAllUpdatedAfter",
    query = "SELECT c.* FROM {h-schema}ES_CASE_HIST c WHERE c.CASE_ID IN ("
        + " SELECT c1.CASE_ID FROM {h-schema}ES_CASE_HIST c1 "
        + "WHERE c1.LAST_CHG > CAST(:after AS TIMESTAMP) "
        + ") ORDER BY PARENT_ID, CASE_ID FOR READ ONLY ",
    resultClass = EsChildPersonCase.class, readOnly = true)})
public class EsParentPersonCase extends EsPersonCase {

  private static final long serialVersionUID = -2850078939765010059L;

  @Id
  @Column(name = "CASE_ID")
  private String caseId;

  @Id
  @Column(name = "FOCUS_CHILD_ID")
  private String focusChildId;

  @Id
  @Column(name = "PARENT_ID")
  private String parentId;

  @Override
  public String getCaseId() {
    return caseId;
  }

  @Override
  public void setCaseId(String caseId) {
    this.caseId = caseId;
  }

  @Override
  public String getFocusChildId() {
    return focusChildId;
  }

  @Override
  public String getParentId() {
    return parentId;
  }

  @Override
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @Override
  public void setFocusChildId(String focusChildId) {
    this.focusChildId = focusChildId;
  }

  @Override
  public Object getNormalizationGroupKey() {
    return getParentId();
  }
}
