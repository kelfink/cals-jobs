package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedEducationProviderContactDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedEducationProviderContact;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.schedule.JobDirector;
import gov.ca.cwds.jobs.schedule.NeutronJobProgressHistory;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;

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
   * @param jobHistory job history
   */
  @Inject
  public EducationProviderContactIndexerJob(final ReplicatedEducationProviderContactDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
      NeutronJobProgressHistory jobHistory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return JobJdbcUtils.getCommonPartitionRanges4(this);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobDirector.runStandalone(EducationProviderContactIndexerJob.class, args);
  }

}
