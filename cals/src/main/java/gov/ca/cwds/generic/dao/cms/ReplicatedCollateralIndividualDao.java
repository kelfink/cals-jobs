package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedCollateralIndividual}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedCollateralIndividualDao extends BatchDaoImpl<ReplicatedCollateralIndividual>
    implements BatchBucketDao<ReplicatedCollateralIndividual> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedCollateralIndividualDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
