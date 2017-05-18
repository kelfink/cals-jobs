package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferral;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob extends BasePersonIndexerJob<ReplicatedPersonReferral, EsPersonReferral>
		implements JobResultSetAware<EsPersonReferral> {

	private static final Logger LOGGER = LogManager.getLogger(ReferralHistoryIndexerJob.class);
	
	/**
	 * Construct batch job instance with all required dependencies.
	 * @param clientDao
	 * @param esDao
	 * @param lastJobRunTimeFilename
	 * @param mapper
	 * @param sessionFactory
	 */
	@Inject
	public ReferralHistoryIndexerJob(ReplicatedPersonReferralDao clientDao, ElasticsearchDao esDao,
			String lastJobRunTimeFilename, ObjectMapper mapper, SessionFactory sessionFactory) {
		super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
	}

	@Override
	public String getViewName() {
		return "ES_REFERRAL_HIST";
	}

	@Override
	public String getJdbcOrderBy() {
		return " ORDER BY CLIENT_ID ";
	}

	@Override
	public EsPersonReferral extractFromResultSet(ResultSet rs) throws SQLException {
		EsPersonReferral referral = new EsPersonReferral();
		
		referral.setReferralId(rs.getString("REFERRAL_ID"));
		referral.setClientId(rs.getString("CLIENT_ID"));
		
		referral.setStartDate(rs.getDate("START_DATE"));
		referral.setEndDate(rs.getDate("END_DATE"));
		referral.setLastChange(rs.getDate("LAST_CHG"));
		referral.setCounty(rs.getString("REFERRAL_COUNTY"));
		
		// TODO
//		referral.setResponseTime(responseTime);
		
		referral.setAllegationId(rs.getString("ALLEGATION_ID"));
		referral.setAllegationType(rs.getString("ALLEGATION_TYPE"));		
		referral.setAllegationDisposition(rs.getString("ALLEGATION_DISPOSITION"));
		
		referral.setPerpetratorId(rs.getString("PERPETRATOR_ID"));
		referral.setPerpetratorFirstName(rs.getString("PERPETRATOR_FIRST_NM"));
		referral.setPerpetratorLastName(rs.getString("PERPETRATOR_LAST_NM"));
		
		referral.setReporterId(rs.getString("REPORTER_ID"));
		referral.setReporterFirstName(rs.getString("REPORTER_FIRST_NM"));
		referral.setReporterLastName(rs.getString("REPORTER_LAST_NM"));
		
		referral.setVictimId(rs.getString("VICTIM_ID"));
		referral.setVictimFirstName(rs.getString("VICTIM_FIRST_NM"));
		referral.setVictimLastName(rs.getString("VICTIM_LAST_NM"));
		
		referral.setWorkerId(rs.getString("WORKER_ID"));
		referral.setWorkerFirstName(rs.getString("WORKER_FIRST_NM"));
		referral.setWorkerLastName(rs.getString("WORKER_LAST_NM"));
		
	    return referral;
	}
}
