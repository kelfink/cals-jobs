package gov.ca.cwds.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.data.cms.OtherClientNameDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.OtherClientName;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Other Client Name from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherClientNameIndexerJob extends BasePersonIndexerJob<OtherClientName> {

  private static final Logger LOGGER = LogManager.getLogger(OtherClientNameIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao OtherClientName DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherClientNameIndexerJob(final OtherClientNameDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Run Other Client Name indexer job");
    try {
      runJob(OtherClientNameIndexerJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}
