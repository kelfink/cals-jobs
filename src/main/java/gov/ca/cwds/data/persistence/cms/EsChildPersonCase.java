package gov.ca.cwds.data.persistence.cms;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity bean for Materialized Query Table (MQT), ES_CASE_HIST, for focus child person cases.
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "ES_CASE_HIST")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfter",
    query = "SELECT c.* FROM {h-schema}ES_CASE_HIST c WHERE c.CASE_ID IN ("
        + " SELECT c1.CASE_ID FROM {h-schema}ES_CASE_HIST c1 "
        + "WHERE c1.LAST_CHG > CAST(:after AS TIMESTAMP) "
        + ") ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY ",
    resultClass = EsChildPersonCase.class, readOnly = true)
public class EsChildPersonCase extends EsPersonCase {

  private static final long serialVersionUID = 8157993904607079133L;

  /**
   * Default constructor.
   */
  public EsChildPersonCase() {
    super();
  }

  @Override
  public Object getNormalizationGroupKey() {
    return this.getFocusChildId();
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
