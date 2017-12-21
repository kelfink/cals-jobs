package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedOtherClientName;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedOtherClientName}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedOtherClientNameDao extends BaseDaoImpl<ReplicatedOtherClientName> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedOtherClientNameDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
