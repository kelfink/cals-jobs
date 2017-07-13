package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral>
    implements JobResultSetAware<EsPersonReferral> {

  private static final Logger LOGGER = LogManager.getLogger(ReferralHistoryIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ReferralHistoryIndexerJob(ReplicatedPersonReferralsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsPersonReferral.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "MQT_REFERRAL_HIST";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY CLIENT_ID ";
  }

  @Override
  protected String getLegacySourceTable() {
    return "REFERL_T";
  }

  @Override
  protected ReplicatedPersonReferrals normalizeSingle(List<EsPersonReferral> recs) {
    return normalize(recs).get(0);
  }

  @Override
  protected List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    List<ElasticSearchPersonReferral> esPersonReferrals = referrals.geReferrals();
    esp.setReferrals(esPersonReferrals);

    if (esPersonReferrals != null && !esPersonReferrals.isEmpty()) {
      try {
        buf.append(esPersonReferrals.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.fatal("ERROR SERIALIZING REFERRALS", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String updateJson = buf.toString();
    final String insertJson = mapper.writeValueAsString(esp);
    LOGGER.trace("insertJson: {}", () -> insertJson);
    LOGGER.trace("updateJson: {}", () -> updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  public EsPersonReferral extract(ResultSet rs) throws SQLException {
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(ifNull(rs.getString("REFERRAL_ID")));
    referral.setClientId(ifNull(rs.getString("CLIENT_ID")));

    referral.setStartDate(rs.getDate("START_DATE"));
    referral.setEndDate(rs.getDate("END_DATE"));
    referral.setLastChange(rs.getDate("LAST_CHG"));
    referral.setCounty(rs.getInt("REFERRAL_COUNTY"));
    referral.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    referral.setReferralLastChanged(rs.getDate("RFL_IBMSNAP_LOGMARKER"));

    referral.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    referral.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    referral.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    referral.setAllegationLastChanged(rs.getDate("ALG_IBMSNAP_LOGMARKER"));

    referral.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    referral.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    referral.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    referral.setPerpetratorLastChanged(rs.getDate("CLP_IBMSNAP_LOGMARKER"));

    referral.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    referral.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    referral.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    referral.setReporterLastChanged(rs.getDate("RPT_IBMSNAP_LOGMARKER"));

    referral.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    referral.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    referral.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    referral.setVictimLastChanged(rs.getDate("CLV_IBMSNAP_LOGMARKER"));

    referral.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    referral.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    referral.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    referral.setWorkerLastChanged(rs.getDate("STP_IBMSNAP_LOGMARKER"));

    referral.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    referral.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    referral.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    referral.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return referral;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReferralHistoryIndexerJob.class, args);
  }
}
