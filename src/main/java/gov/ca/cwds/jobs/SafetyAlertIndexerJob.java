package gov.ca.cwds.jobs;

import static gov.ca.cwds.neutron.util.transform.JobTransformUtils.ifNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedSafetyAlertsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsSafetyAlert;
import gov.ca.cwds.data.persistence.cms.ReplicatedSafetyAlerts;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.rocket.InitialLoadJdbcRocket;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class SafetyAlertIndexerJob
    extends InitialLoadJdbcRocket<ReplicatedSafetyAlerts, EsSafetyAlert>
    implements NeutronRowMapper<EsSafetyAlert> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SafetyAlertIndexerJob.class);

  /**
   * Construct the object
   * 
   * @param clientDao DAO
   * @param esDao ES SAO
   * @param lastJobRunTimeFilename Last runtime file
   * @param mapper Object mapper
   * @param sessionFactory Session factory
   * @param jobHistory job history
   * @param opts command line opts
   */
  @Inject
  public SafetyAlertIndexerJob(ReplicatedSafetyAlertsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory,
      FlightPlan opts) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, opts);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsSafetyAlert.class;
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY CLIENT_ID ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.CLIENT_SENSITIVITY_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_LST_SAFETY_ALERT";
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  public ReplicatedSafetyAlerts normalizeSingle(List<EsSafetyAlert> recs) {
    return recs != null && !recs.isEmpty() ? normalize(recs).get(0) : new ReplicatedSafetyAlerts();
  }

  @Override
  public List<ReplicatedSafetyAlerts> normalize(List<EsSafetyAlert> recs) {
    return EntityNormalizer.<ReplicatedSafetyAlerts, EsSafetyAlert>normalizeList(recs);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedSafetyAlerts safetyAlerts) throws NeutronException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"safety_alerts\":[");

    final List<ElasticSearchSafetyAlert> esSafetyAlerts = safetyAlerts.getSafetyAlerts();
    esp.setSafetyAlerts(esSafetyAlerts);

    if (esSafetyAlerts != null && !esSafetyAlerts.isEmpty()) {
      try {
        buf.append(esSafetyAlerts.stream().map(ElasticTransformer::jsonify)
            .sorted(String::compareTo).collect(Collectors.joining(",")));
      } catch (Exception e) {
        throw JobLogs.runtime(LOGGER, e, "ERROR SERIALIZING SAFETY ALERTS: {}", e.getMessage());
      }
    }

    buf.append("]}");
    final String updateJson = buf.toString();

    String insertJson;
    try {
      insertJson = mapper.writeValueAsString(esp);
    } catch (JsonProcessingException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO WRITE OBJECT TO JSON! {}", e.getMessage());
    }

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  public EsSafetyAlert extract(final ResultSet rs) throws SQLException {
    final EsSafetyAlert ret = new EsSafetyAlert();

    ret.setClientId(ifNull(rs.getString("CLIENT_ID")));
    ret.setAlertId(ifNull(rs.getString("ALERT_ID")));
    ret.setActivationCountyCode(rs.getInt("ACTIVATION_COUNTY_CD"));
    ret.setActivationDate(rs.getDate("ACTIVATION_DATE"));
    ret.setActivationExplanation(ifNull(rs.getString("ACTIVATION_EXPLANATION")));
    ret.setActivationReasonCode(rs.getInt("ACTIVATION_REASON_CD"));
    ret.setDeactivationCountyCode(rs.getInt("DEACTIVATION_COUNTY_CD"));
    ret.setDeactivationDate(rs.getDate("DEACTIVATION_DATE"));
    ret.setDeactivationExplanation(ifNull(rs.getString("DEACTIVATION_EXPLANATION")));
    ret.setLastUpdatedId(ifNull(rs.getString("LAST_UPDATED_ID")));
    ret.setLastUpdatedTimestamp(rs.getTimestamp("LAST_UPDATED_TS"));
    ret.setLastUpdatedOperation(ifNull(rs.getString("ALERT_IBMSNAP_OPERATION")));
    ret.setReplicationTimestamp(rs.getTimestamp("ALERT_IBMSNAP_LOGMARKER"));
    ret.setLastChanged(rs.getTimestamp("LAST_CHANGED"));

    return ret;
  }

  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtils.getCommonPartitionRanges16(this);
  }

  @Override
  public String getOptionalElementName() {
    return "safety_alerts";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(SafetyAlertIndexerJob.class, args);
  }

}
