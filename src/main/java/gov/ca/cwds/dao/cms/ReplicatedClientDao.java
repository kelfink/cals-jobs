package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.cms.ReplicatedClient;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedClient}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedClientDao extends BaseDaoImpl<ReplicatedClient>
    implements BatchBucketDao<ReplicatedClient> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedClientDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
