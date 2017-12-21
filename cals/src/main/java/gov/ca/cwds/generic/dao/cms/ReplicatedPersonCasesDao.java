package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedPersonCases}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedPersonCasesDao extends BaseDaoImpl<ReplicatedPersonCases>
    implements BatchBucketDao<ReplicatedPersonCases> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedPersonCasesDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
