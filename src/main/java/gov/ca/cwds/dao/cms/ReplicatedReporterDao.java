package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.persistence.cms.rep.ReplicatedReporter;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedReporter}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedReporterDao extends BatchDaoImpl<ReplicatedReporter>
    implements BatchBucketDao<ReplicatedReporter> {

  /**
   * Constructor
   * 
   * @param sessionFactory The sessionFactory
   */
  @Inject
  public ReplicatedReporterDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
