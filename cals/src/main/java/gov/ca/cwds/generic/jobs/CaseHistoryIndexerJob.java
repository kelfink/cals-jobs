package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.generic.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.generic.jobs.util.JobLogs;
import gov.ca.cwds.generic.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.generic.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.generic.jobs.util.transform.EntityNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CaseHistoryIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)" + "\nSELECT CAS.IDENTIFIER "
          + "\nFROM #SCHEMA#.CASE_T CAS " + "\nWHERE CAS.IBMSNAP_LOGMARKER > ##TIMESTAMP## "
          + "\nUNION " + "\nSELECT CAS.IDENTIFIER " + "\nFROM #SCHEMA#.CASE_T CAS "
          + "\nLEFT JOIN #SCHEMA#.CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
          + "\nLEFT JOIN #SCHEMA#.CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
          + "\nWHERE CCL.IBMSNAP_LOGMARKER > ##TIMESTAMP## " + "\nUNION "
          + "\nSELECT CAS.IDENTIFIER " + "\nFROM #SCHEMA#.CASE_T CAS "
          + "\nLEFT JOIN #SCHEMA#.CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
          + "\nLEFT JOIN #SCHEMA#.CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
          + "\nWHERE CLC.IBMSNAP_LOGMARKER > ##TIMESTAMP## " + "\nUNION "
          + "\nSELECT CAS.IDENTIFIER " + "\nFROM #SCHEMA#.CASE_T CAS "
          + "\nLEFT JOIN #SCHEMA#.CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
          + "\nLEFT JOIN #SCHEMA#.CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
          + "\nJOIN #SCHEMA#.CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
          + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
          + "\nWHERE CLR.IBMSNAP_LOGMARKER > ##TIMESTAMP## " + "\nUNION "
          + "\nSELECT CAS.IDENTIFIER " + "\nFROM #SCHEMA#.CASE_T CAS "
          + "\nLEFT JOIN #SCHEMA#.CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
          + "\nLEFT JOIN #SCHEMA#.CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
          + "\nJOIN #SCHEMA#.CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
          + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
          + "\nJOIN #SCHEMA#.CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0 "
          + "\nWHERE CLP.IBMSNAP_LOGMARKER > ##TIMESTAMP## ";

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao Case history view DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public CaseHistoryIndexerJob(final ReplicatedPersonCasesDao clientDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();

    buf.append("SELECT x.* FROM ").append(dbSchemaName).append(".").append(getInitialLoadViewName())
        .append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.LIMITED_ACCESS_CODE = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR");
    return buf.toString();
  }

  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    /**
     * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
     * sensitive flag must be deleted.
     */
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonCases cases)
      throws IOException {

    final StringBuilder buf = new StringBuilder();
    buf.append("{\"cases\":[");

    List<ElasticSearchPersonCase> esPersonCases = cases.getCases();
    esp.setCases(esPersonCases);

    if (!esPersonCases.isEmpty()) {
      try {
        buf.append(esPersonCases.stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogs.raiseError(LOGGER, e, "ERROR SERIALIZING CASES! {}", e.getMessage());
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();
    LOGGER.trace("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  public ReplicatedPersonCases normalizeSingle(final List<EsPersonCase> recs) {
    return recs != null && !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedPersonCases> normalize(final List<EsPersonCase> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsPersonCase>normalizeList(recs);
  }
}
