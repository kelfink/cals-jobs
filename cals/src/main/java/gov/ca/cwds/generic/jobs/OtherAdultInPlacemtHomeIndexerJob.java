package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

/**
 * Job to load Other Adult In Placement Home from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherAdultInPlacemtHomeIndexerJob extends
    BasePersonIndexerJob<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherAdultInPlacemtHomeIndexerJob(final ReplicatedOtherAdultInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  /**
   * @deprecated soon to be removed
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "OTH_ADLT";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(OtherAdultInPlacemtHomeIndexerJob.class, args);
  }

}
