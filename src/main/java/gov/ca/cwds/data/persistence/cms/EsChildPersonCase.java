package gov.ca.cwds.data.persistence.cms;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity bean for view VW_LST_CASE_HIST for focus child person cases.
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_CASE_HIST")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfter",
    query = "SELECT x.* FROM (" + CaseSQLResource.BASE_VIEW_SELECT + ") x "
        + " WHERE current timestamp > :after "
        + " ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
    resultClass = EsChildPersonCase.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfterWithUnlimitedAccess",
    query = "SELECT x.* FROM (" + CaseSQLResource.BASE_VIEW_SELECT + ") x "
        + " WHERE current timestamp > :after AND x.LIMITED_ACCESS_CODE = 'N' "
        + "ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
    resultClass = EsChildPersonCase.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfterWithLimitedAccess",
    query = "SELECT x.* FROM (" + CaseSQLResource.BASE_VIEW_SELECT + ") x "
        + " WHERE current timestamp > :after AND x.LIMITED_ACCESS_CODE != 'N' "
        + "ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
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
  public String getNormalizationGroupKey() {
    return this.getFocusChildId();
  }

}
