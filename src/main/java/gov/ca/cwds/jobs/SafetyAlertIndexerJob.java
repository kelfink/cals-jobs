package gov.ca.cwds.jobs;

import static gov.ca.cwds.jobs.util.transform.JobTransformUtils.ifNull;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class SafetyAlertIndexerJob
    extends BasePersonIndexerJob<ReplicatedSafetyAlerts, EsSafetyAlert>
    implements JobResultSetAware<EsSafetyAlert> {

  /**
   * Default serialization.
   */
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
   */
  @Inject
  public SafetyAlertIndexerJob(ReplicatedSafetyAlertsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsSafetyAlert.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_LST_SAFETY_ALERT";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY CLIENT_ID ";
  }

  @Override
  public String getLegacySourceTable() {
    return "SAF_ALRT";
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
      ReplicatedSafetyAlerts safetyAlerts) throws IOException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"safety_alerts\":[");

    final List<ElasticSearchSafetyAlert> esSafetyAlerts = safetyAlerts.getSafetyAlerts();
    esp.setSafetyAlerts(esSafetyAlerts);

    if (esSafetyAlerts != null && !esSafetyAlerts.isEmpty()) {
      try {
        buf.append(esSafetyAlerts.stream().map(ElasticTransformer::jsonify)
            .sorted(String::compareTo).collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.error("ERROR SERIALIZING SAFETY ALERTS", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String updateJson = buf.toString();
    final String insertJson = mapper.writeValueAsString(esp);

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
  public boolean providesInitialKeyRanges() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>(16);

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "B3bMRWu8NV"));
      ret.add(Pair.of("B3bMRWu8NV", "DW5GzxJ30A"));
      ret.add(Pair.of("DW5GzxJ30A", "FNOBbaG6qq"));
      ret.add(Pair.of("FNOBbaG6qq", "HJf1EJe25X"));
      ret.add(Pair.of("HJf1EJe25X", "JCoyq0Iz36"));
      ret.add(Pair.of("JCoyq0Iz36", "LvijYcj01S"));
      ret.add(Pair.of("LvijYcj01S", "Npf4LcB3Lr"));
      ret.add(Pair.of("Npf4LcB3Lr", "PiJ6a0H49S"));
      ret.add(Pair.of("PiJ6a0H49S", "RbL4aAL34A"));
      ret.add(Pair.of("RbL4aAL34A", "S3qiIdg0BN"));
      ret.add(Pair.of("S3qiIdg0BN", "0Ltok9y5Co"));
      ret.add(Pair.of("0Ltok9y5Co", "2CFeyJd49S"));
      ret.add(Pair.of("2CFeyJd49S", "4w3QDw136B"));
      ret.add(Pair.of("4w3QDw136B", "6p9XaHC10S"));
      ret.add(Pair.of("6p9XaHC10S", "8jw5J580MQ"));
      ret.add(Pair.of("8jw5J580MQ", "9999999999"));

      ret = limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
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
    JobRunner.runStandalone(SafetyAlertIndexerJob.class, args);
  }

}
