package gov.ca.cwds.data.persistence.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity bean for view VW_PARENT_CASE_HIST for parent person cases.
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_PARENT_CASE_HIST")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsParentPersonCase.findAllUpdatedAfter",
    query = "SELECT c.* FROM {h-schema}VW_PARENT_CASE_HIST c WHERE c.CASE_ID IN ("
        + " SELECT c1.CASE_ID FROM {h-schema}VW_PARENT_CASE_HIST c1 "
        + "WHERE c1.LAST_CHG > CAST(:after AS TIMESTAMP) "
        + ") ORDER BY PARENT_PERSON_ID, CASE_ID, PARENT_ID FOR READ ONLY ",
    resultClass = EsParentPersonCase.class, readOnly = true)
public class EsParentPersonCase extends EsPersonCase {

  private static final long serialVersionUID = -3139817453644311072L;

  @Id
  @Column(name = "PARENT_PERSON_ID")
  private String parentPersonId;

  /**
   * Default constructor.
   */
  public EsParentPersonCase() {
    super();
  }

  public String getParentPersonId() {
    return parentPersonId;
  }

  public void setParentPersonId(String parentPersonId) {
    this.parentPersonId = parentPersonId;
  }

  @Override
  public Object getNormalizationGroupKey() {
    return this.parentPersonId;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

}
