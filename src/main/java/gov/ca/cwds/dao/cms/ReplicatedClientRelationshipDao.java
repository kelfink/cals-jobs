package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClientRelationship;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedClientRelationship}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedClientRelationshipDao extends BaseDaoImpl<ReplicatedClientRelationship>
    implements BatchBucketDao<ReplicatedClientRelationship> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedClientRelationshipDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
