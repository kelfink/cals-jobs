package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedOtherAdultInPlacemtHome}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedOtherAdultInPlacemtHomeDao
    extends BaseDaoImpl<ReplicatedOtherAdultInPlacemtHome>
    implements BatchBucketDao<ReplicatedOtherAdultInPlacemtHome> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedOtherAdultInPlacemtHomeDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
