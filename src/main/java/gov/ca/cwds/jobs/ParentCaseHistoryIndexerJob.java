package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

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

  private static final Logger LOGGER = LogManager.getLogger(ParentCaseHistoryIndexerJob.class);

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
    String parentId = rs.getString("PARENT_ID");
    String caseId = rs.getString("CASE_ID");

    if (parentId == null) {
      LOGGER.warn("PARENT_ID is null for CASE_ID: {}", caseId);
      return null;
    }

    EsParentPersonCase personCase = new EsParentPersonCase();
    super.fillEsPersonCaseFromResultSet(rs, personCase);
    return personCase;
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsParentPersonCase.class;
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY PARENT_ID, CASE_ID ";
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

