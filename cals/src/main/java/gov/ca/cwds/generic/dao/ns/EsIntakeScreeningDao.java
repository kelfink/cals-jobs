package gov.ca.cwds.generic.dao.ns;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.NsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link EsIntakeScreening}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class EsIntakeScreeningDao extends BaseDaoImpl<EsIntakeScreening>
    implements BatchBucketDao<EsIntakeScreening> {

  /**
   * Constructor
   * 
   * @param sessionFactory The PostgreSQL sessionFactory
   */
  @Inject
  public EsIntakeScreeningDao(@NsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
