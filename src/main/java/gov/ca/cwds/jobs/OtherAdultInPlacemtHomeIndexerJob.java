package gov.ca.cwds.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.data.cms.OtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.OtherAdultInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Other Adult In Placement Home from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherAdultInPlacemtHomeIndexerJob
    extends BasePersonIndexerJob<OtherAdultInPlacemtHome> {

  private static final Logger LOGGER =
      LogManager.getLogger(OtherAdultInPlacemtHomeIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao OtherAdultInPlacemtHome DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherAdultInPlacemtHomeIndexerJob(final OtherAdultInPlacemtHomeDao mainDao,
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
    LOGGER.info("Run Other Adult In Placement Home indexer job");
    try {
      runJob(OtherAdultInPlacemtHomeIndexerJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}
