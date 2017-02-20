package gov.ca.cwds.data.persistence.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedAttorney}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedAttorneyDao extends BaseDaoImpl<ReplicatedAttorney>
    implements BatchBucketDao<ReplicatedAttorney> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedAttorneyDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
