package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedEducationProviderContactDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedEducationProviderContact;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Job to load Education Provider Contact from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class EducationProviderContactIndexerJob extends
    BasePersonIndexerJob<ReplicatedEducationProviderContact, ReplicatedEducationProviderContact> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Education Provider DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public EducationProviderContactIndexerJob(final ReplicatedEducationProviderContactDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public int getJobTotalBuckets() {
    return 12;
  }

  /**
   * @deprecated method scheduled for deletion
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "EDPRVCNT";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(EducationProviderContactIndexerJob.class, args);
  }

}
