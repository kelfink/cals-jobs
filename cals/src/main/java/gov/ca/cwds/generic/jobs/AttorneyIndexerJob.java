package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedAttorney;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Job to load attorneys from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class AttorneyIndexerJob
    extends BasePersonIndexerJob<ReplicatedAttorney, ReplicatedAttorney> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao ReplicatedAttorney DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public AttorneyIndexerJob(final ReplicatedAttorneyDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  /**
   * @deprecated older attribute will be removed
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "ATTRNY_T";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(AttorneyIndexerJob.class, args);
  }

}
