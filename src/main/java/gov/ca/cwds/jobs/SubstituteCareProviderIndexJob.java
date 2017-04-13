package gov.ca.cwds.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedSubstituteCareProviderDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Substitute Care Providers from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class SubstituteCareProviderIndexJob extends
    BasePersonIndexerJob<ReplicatedSubstituteCareProvider, ReplicatedSubstituteCareProvider> {

  private static final Logger LOGGER = LogManager.getLogger(SubstituteCareProviderIndexJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param substituteCareProviderDao Client DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public SubstituteCareProviderIndexJob(
      final ReplicatedSubstituteCareProviderDao substituteCareProviderDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(substituteCareProviderDao, elasticsearchDao, lastJobRunTimeFilename, mapper,
        sessionFactory);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Run Substitute Care Provider indexer job");
    try {
      runJob(SubstituteCareProviderIndexJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}
