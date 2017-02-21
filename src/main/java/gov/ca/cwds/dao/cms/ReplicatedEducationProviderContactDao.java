package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.cms.ReplicatedEducationProviderContact;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedEducationProviderContact}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedEducationProviderContactDao
    extends BaseDaoImpl<ReplicatedEducationProviderContact>
    implements BatchBucketDao<ReplicatedEducationProviderContact> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedEducationProviderContactDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
