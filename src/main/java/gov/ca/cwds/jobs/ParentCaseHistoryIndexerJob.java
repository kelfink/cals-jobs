package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsParentPersonCase;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load case history from CMS into ElasticSearch for 'parent' person.
 * 
 * @author CWDS API Team
 */
public class ParentCaseHistoryIndexerJob extends CaseHistoryIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParentCaseHistoryIndexerJob.class);

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
  public ParentCaseHistoryIndexerJob(final ReplicatedPersonCasesDao clientDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public EsParentPersonCase extract(ResultSet rs) throws SQLException {
    String parentPersonId = rs.getString("PARENT_PERSON_ID");
    String caseId = rs.getString("CASE_ID");

    if (parentPersonId == null) {
      LOGGER.warn("PARENT_PERSON_ID is null for CASE_ID: {}", caseId);
      return null;
    }

    EsParentPersonCase personCase = new EsParentPersonCase();
    personCase.setParentPersonId(parentPersonId);

    //
    // Case
    //
    personCase.setCaseId(caseId);
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

    //
    // Access Limitation
    //
    personCase.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    personCase.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    personCase.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    personCase.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return personCase;
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsParentPersonCase.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_PARENT_CASE_HIST";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY PARENT_PERSON_ID, CASE_ID, PARENT_ID ";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ParentCaseHistoryIndexerJob.class, args);
  }
}

