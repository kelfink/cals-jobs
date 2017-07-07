package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedPersonReferrals}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedPersonReferralsDao extends BaseDaoImpl<ReplicatedPersonReferrals>
    implements BatchBucketDao<ReplicatedPersonReferrals> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedPersonReferralsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}
