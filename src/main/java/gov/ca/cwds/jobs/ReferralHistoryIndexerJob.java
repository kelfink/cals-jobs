package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

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
  public String getViewName() {
    return "ES_REFERRAL_HIST";
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
  protected ReplicatedPersonReferrals reduceSingle(List<EsPersonReferral> recs) {
    return reduce(recs).get(0);
  }

  @Override
  protected List<ReplicatedPersonReferrals> reduce(List<EsPersonReferral> recs) {
    final int len = (int) (recs.size() * 1.25);
    Map<Object, ReplicatedPersonReferrals> map = new LinkedHashMap<>(len);
    for (EsPersonReferral rec : recs) {
      rec.reduce(map);
    }

    return map.values().stream().collect(Collectors.toList());
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    List<ElasticSearchPersonReferral> esPersonReferrals =
        referrals.geElasticSearchPersonReferrals();
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
    LOGGER.info("insertJson: {}", insertJson);
    LOGGER.info("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  public EsPersonReferral extractFromResultSet(ResultSet rs) throws SQLException {
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(rs.getString("REFERRAL_ID"));
    referral.setClientId(rs.getString("CLIENT_ID"));

    referral.setStartDate(rs.getDate("START_DATE"));
    referral.setEndDate(rs.getDate("END_DATE"));
    referral.setLastChange(rs.getDate("LAST_CHG"));
    referral.setCounty(rs.getShort("REFERRAL_COUNTY"));
    referral.setReferralResponseType(rs.getShort("REFERRAL_RESPONSE_TYPE"));

    referral.setAllegationId(rs.getString("ALLEGATION_ID"));
    referral.setAllegationType(rs.getShort("ALLEGATION_TYPE"));
    referral.setAllegationDisposition(rs.getShort("ALLEGATION_DISPOSITION"));

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

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Run ReferralHistoryIndexerJob");
    try {
      runJob(ReferralHistoryIndexerJob.class, args);
    } catch (Exception e) {
      LOGGER.fatal("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }
}
