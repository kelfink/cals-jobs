package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReferral;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Pseudo-normalized container for CMS person referral.
 * 
 * @author CWDS API Team
 */
public class ReplicatedPersonReferrals implements PersistentObject, ApiPersonAware {

	private static final long serialVersionUID = -8746969311364544478L;

	private String clientId;
	private List<ElasticSearchPersonReferral> esPersonReferrals = new ArrayList<ElasticSearchPersonReferral>();
	
	public ReplicatedPersonReferrals(String clientId) {
		this.clientId = clientId;
	}
	
	public List<ElasticSearchPersonReferral> geElasticSearchPersonReferrals() {
		return esPersonReferrals;
	}

	public void setElasticSearchPersonReferrals(List<ElasticSearchPersonReferral> esPersonReferrals) {
		this.esPersonReferrals = esPersonReferrals;
	}

	public void addElasticSearchPersonReferral(ElasticSearchPersonReferral referral) {
		esPersonReferrals.add(referral);
	}
	
	@Override
	public Serializable getPrimaryKey() {
		return this.clientId;
	}

	@Override
	public Date getBirthDate() {
		return null;
	}

	@Override
	public String getFirstName() {
		return null;
	}

	@Override
	public String getGender() {
		return null;
	}

	@Override
	public String getLastName() {
		return null;
	}

	@Override
	public String getMiddleName() {
		return null;
	}

	@Override
	public String getNameSuffix() {
		return null;
	}

	@Override
	public String getSsn() {
		return null;
	}
	
	
}
