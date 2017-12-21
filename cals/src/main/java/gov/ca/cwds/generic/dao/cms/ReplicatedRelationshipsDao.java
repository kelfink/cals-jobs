package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedRelationship;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedRelationships;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedRelationship}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedRelationshipsDao extends BaseDaoImpl<ReplicatedRelationships>
    implements BatchBucketDao<ReplicatedRelationships> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedRelationshipsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
