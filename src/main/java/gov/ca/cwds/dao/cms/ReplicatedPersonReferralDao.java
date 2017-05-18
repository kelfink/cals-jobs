package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferral;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link ReplicatedPersonReferral}.
 * 
 * @author CWDS API Team
 * @see CmsSessionFactory
 * @see SessionFactory
 */
public class ReplicatedPersonReferralDao extends BaseDaoImpl<ReplicatedPersonReferral>
		implements BatchBucketDao<ReplicatedPersonReferral> {

	/**
	 * Constructor
	 * 
	 * @param sessionFactory
	 *            The sessionFactory
	 */
	@Inject
	public ReplicatedPersonReferralDao(@CmsSessionFactory SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
