package gov.ca.cwds.generic.jobs;

import static gov.ca.cwds.generic.jobs.util.transform.JobTransformUtils.ifNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.generic.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.generic.data.persistence.cms.EsParentPersonCase;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to load case history from CMS into ElasticSearch for 'parent' person.
 * 
 * @author CWDS API Team
 */
public class ParentCaseHistoryIndexerJob extends CaseHistoryIndexerJob {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(
      ParentCaseHistoryIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Case history view DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ParentCaseHistoryIndexerJob(final ReplicatedPersonCasesDao dao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public EsParentPersonCase extract(ResultSet rs) throws SQLException {
    String parentPersonId = rs.getString("PARENT_PERSON_ID");
    final String caseId = rs.getString("CASE_ID");

    if (parentPersonId == null) {
      LOGGER.warn("PARENT_PERSON_ID is null for CASE_ID: {}", caseId.trim()); // NOSONAR
      return null;
    }

    final EsParentPersonCase ret = new EsParentPersonCase();
    ret.setParentPersonId(parentPersonId);

    //
    // Case:
    //
    ret.setCaseId(caseId);
    ret.setStartDate(rs.getDate("START_DATE"));
    ret.setEndDate(rs.getDate("END_DATE"));
    ret.setCaseLastUpdated(rs.getDate("CASE_LAST_UPDATED"));
    ret.setCounty(rs.getInt("COUNTY"));
    ret.setServiceComponent(rs.getInt("SERVICE_COMP"));

    //
    // Child (client):
    //
    ret.setFocusChildId(rs.getString("FOCUS_CHILD_ID"));
    ret.setFocusChildFirstName(ifNull(rs.getString("FOCUS_CHLD_FIRST_NM")));
    ret.setFocusChildLastName(ifNull(rs.getString("FOCUS_CHLD_LAST_NM")));
    ret.setFocusChildLastUpdated(rs.getDate("FOCUS_CHILD_LAST_UPDATED"));

    //
    // Parent:
    //
    ret.setParentId(ifNull(rs.getString("PARENT_ID")));
    ret.setParentFirstName(ifNull(rs.getString("PARENT_FIRST_NM")));
    ret.setParentLastName(ifNull(rs.getString("PARENT_LAST_NM")));
    ret.setParentRelationship(rs.getInt("PARENT_RELATIONSHIP"));
    ret.setParentLastUpdated(rs.getDate("PARENT_LAST_UPDATED"));
    ret.setParentSourceTable(rs.getString("PARENT_SOURCE_TABLE"));

    //
    // Worker (staff):
    //
    ret.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    ret.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    ret.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    ret.setWorkerLastUpdated(rs.getDate("WORKER_LAST_UPDATED"));

    //
    // Access Limitation:
    //
    ret.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    ret.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    ret.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return ret;
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
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
    JobRunner.runStandalone(ParentCaseHistoryIndexerJob.class, args);
  }
}
