package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

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
