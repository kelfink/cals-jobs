package gov.ca.cwds.jobs;

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

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load case history from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public abstract class CaseHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonCases, EsPersonCase>
    implements JobResultSetAware<EsPersonCase> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CaseHistoryIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Case history view DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   */
  @Inject
  public CaseHistoryIndexerJob(final ReplicatedPersonCasesDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory);
  }

  @Override
  public String getPrepLastChangeSQL() {
    return "INSERT INTO GT_ID (IDENTIFIER)\nSELECT CAS.IDENTIFIER\nFROM CASE_T CAS "
        + "\nWHERE CAS.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS\nLEFT JOIN CHLD_CLT CCL1 ON CCL1.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC1 ON CLC1.IDENTIFIER = CCL1.FKCLIENT_T "
        + "\nWHERE CCL1.IBMSNAP_LOGMARKER > ? \nUNION\nSELECT CAS.IDENTIFIER \nFROM CASE_T CAS "
        + "\nLEFT JOIN CHLD_CLT CCL2 ON CCL2.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC2 ON CLC2.IDENTIFIER = CCL2.FKCLIENT_T "
        + "\nWHERE CLC2.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS " + "\nLEFT JOIN CHLD_CLT CCL3 ON CCL3.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC3 ON CLC3.IDENTIFIER = CCL3.FKCLIENT_T "
        + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL3.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
        + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
        + "\nWHERE CLR.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS " + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
        + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
        + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
        + "\nJOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0 "
        + "\nWHERE CLP.IBMSNAP_LOGMARKER > ? ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.LIMITED_ACCESS_CODE = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR");
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
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonCases cases)
      throws NeutronException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"cases\":[");

    List<ElasticSearchPersonCase> esPersonCases = cases.getCases();
    esp.setCases(esPersonCases);

    if (!esPersonCases.isEmpty()) {
      try {
        buf.append(esPersonCases.stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        throw JobLogs.buildRuntimeException(LOGGER, e, "ERROR SERIALIZING CASES! {}",
            e.getMessage());
      }
    }

    buf.append("]}");

    String insertJson;
    try {
      insertJson = mapper.writeValueAsString(esp);
    } catch (JsonProcessingException e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "FAILED TO WRITE OBJECT TO JSON! {}",
          e.getMessage());
    }

    final String updateJson = buf.toString();
    LOGGER.trace("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  public ReplicatedPersonCases normalizeSingle(final List<EsPersonCase> recs) {
    return recs != null && !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedPersonCases> normalize(final List<EsPersonCase> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsPersonCase>normalizeList(recs);
  }

  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return JobJdbcUtils.getCommonPartitionRanges64(this);
  }

}
