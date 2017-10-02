package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherChildInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Other Child In Placement Home from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherChildInPlacemtHomeIndexerJob extends
    BasePersonIndexerJob<ReplicatedOtherChildInPlacemtHome, ReplicatedOtherChildInPlacemtHome> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao OtherChildInPlacemtHomeDao DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherChildInPlacemtHomeIndexerJob(final ReplicatedOtherChildInPlacemtHomeDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "OTH_KIDT";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(OtherChildInPlacemtHomeIndexerJob.class, args);
  }

}
