package gov.ca.cwds.generic.data.persistence.cms;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity bean for view VW_LST_CASE_HIST for focus child person cases.
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_CASE_HIST")
@NamedNativeQueries({@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfter",
    query = "SELECT c.* FROM {h-schema}VW_LST_CASE_HIST c WHERE c.CASE_ID IN ("
        + " SELECT c1.CASE_ID FROM {h-schema}VW_LST_CASE_HIST c1 " + "WHERE c1.LAST_CHG > :after "
        + ") ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
    resultClass = EsChildPersonCase.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfterWithUnlimitedAccess",
        query = "SELECT c.* FROM {h-schema}VW_LST_CASE_HIST c WHERE c.CASE_ID IN ("
            + " SELECT c1.CASE_ID FROM {h-schema}VW_LST_CASE_HIST c1 "
            + "WHERE c1.LAST_CHG > :after "
            + ") AND c.LIMITED_ACCESS_CODE = 'N' ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
        resultClass = EsChildPersonCase.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.EsChildPersonCase.findAllUpdatedAfterWithLimitedAccess",
        query = "SELECT c.* FROM {h-schema}VW_LST_CASE_HIST c WHERE c.CASE_ID IN ("
            + " SELECT c1.CASE_ID FROM {h-schema}VW_LST_CASE_HIST c1 "
            + "WHERE c1.LAST_CHG > :after "
            + ") AND c.LIMITED_ACCESS_CODE != 'N' ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID FOR READ ONLY WITH UR ",
        resultClass = EsChildPersonCase.class, readOnly = true)})
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
