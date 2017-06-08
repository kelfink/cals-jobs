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

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load case history from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public abstract class CaseHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonCases, EsPersonCase>
    implements JobResultSetAware<EsPersonCase> {

  private static final Logger LOGGER = LogManager.getLogger(CaseHistoryIndexerJob.class);

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
  public String getViewName() {
    return "ES_CASE_HIST";
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonCases cases)
      throws IOException {

    StringBuilder buf = new StringBuilder();
    buf.append("{\"cases\":[");

    List<ElasticSearchPersonCase> esPersonCasess = cases.getCases();
    esp.setCases(esPersonCasess);

    if (!esPersonCasess.isEmpty()) {
      try {
        buf.append(esPersonCasess.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.error("ERROR SERIALIZING CASES", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();
    LOGGER.trace("updateJson: {}", () -> updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  protected ReplicatedPersonCases normalizeSingle(List<EsPersonCase> recs) {
    return normalize(recs).get(0);
  }

  @Override
  protected List<ReplicatedPersonCases> normalize(List<EsPersonCase> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsPersonCase>normalizeList(recs);
  }

  /**
   * Fills given EsPersonCase with data retrieved from given ResultSet
   * 
   * @param rs
   * @param personCase
   * @throws SQLException
   */
  protected void fillEsPersonCaseFromResultSet(ResultSet rs, EsPersonCase personCase)
      throws SQLException {

    //
    // Case
    //
    personCase.setCaseId(rs.getString("CASE_ID"));
    personCase.setStartDate(rs.getDate("START_DATE"));
    personCase.setEndDate(rs.getDate("END_DATE"));
    personCase.setCaseLastUpdated(rs.getDate("CASE_LAST_UPDATED"));
    personCase.setCounty(rs.getInt("COUNTY"));
    personCase.setServiceComponent(rs.getInt("SERVICE_COMP"));

    //
    // Child (client)
    //
    personCase.setFocusChildId(rs.getString("FOCUS_CHILD_ID"));
    personCase.setFocusChildFirstName(ifNull(rs.getString("FOCUS_CHLD_FIRST_NM")));
    personCase.setFocusChildLastName(ifNull(rs.getString("FOCUS_CHLD_LAST_NM")));
    personCase.setFocusChildLastUpdated(rs.getDate("FOCUS_CHILD_LAST_UPDATED"));

    //
    // Parent
    //
    personCase.setParentId(ifNull(rs.getString("PARENT_ID")));
    personCase.setParentFirstName(ifNull(rs.getString("PARENT_FIRST_NM")));
    personCase.setParentLastName(ifNull(rs.getString("PARENT_LAST_NM")));
    personCase.setParentRelationship(rs.getInt("PARENT_RELATIONSHIP"));
    personCase.setParentLastUpdated(rs.getDate("PARENT_LAST_UPDATED"));
    personCase.setParentSourceTable(rs.getString("PARENT_SOURCE_TABLE"));

    //
    // Worker (staff)
    //
    personCase.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    personCase.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    personCase.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    personCase.setWorkerLastUpdated(rs.getDate("WORKER_LAST_UPDATED"));
  }
}

