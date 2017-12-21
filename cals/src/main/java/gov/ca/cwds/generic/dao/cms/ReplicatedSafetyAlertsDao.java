package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedSafetyAlerts;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 safety alert
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedSafetyAlertsDao extends BaseDaoImpl<ReplicatedSafetyAlerts>
    implements BatchBucketDao<ReplicatedSafetyAlerts> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedSafetyAlertsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}
