package gov.ca.cwds.generic.dao.cms;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedSubstituteCareProvider;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedSubstituteCareProvider}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedSubstituteCareProviderDao
    extends BaseDaoImpl<ReplicatedSubstituteCareProvider>
    implements BatchBucketDao<ReplicatedSubstituteCareProvider> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedSubstituteCareProviderDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
