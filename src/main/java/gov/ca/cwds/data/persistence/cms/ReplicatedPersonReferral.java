package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReferral;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Pseudo-normalized container for CMS person referral.
 * 
 * @author CWDS API Team
 */
public class ReplicatedPersonReferral extends ElasticSearchPersonReferral implements PersistentObject, ApiLegacyAware {

	private static final long serialVersionUID = -8746969311364544478L;

	@Override
	public String getLegacyId() {
		return getId();
	}

	@Override
	public Serializable getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
