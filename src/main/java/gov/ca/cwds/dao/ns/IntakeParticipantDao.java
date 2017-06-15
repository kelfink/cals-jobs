package gov.ca.cwds.dao.ns;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.ns.IntakeParticipant;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.NsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link IntakeParticipant}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class IntakeParticipantDao extends BaseDaoImpl<IntakeParticipant>
    implements BatchBucketDao<IntakeParticipant> {

  /**
   * Constructor
   * 
   * @param sessionFactory The PostgreSQL sessionFactory
   */
  @Inject
  public IntakeParticipantDao(@NsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
