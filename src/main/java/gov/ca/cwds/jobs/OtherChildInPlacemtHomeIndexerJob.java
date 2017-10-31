package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherChildInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.schedule.LaunchDirector;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;

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
   * @param dao OtherChildInPlacemtHomeDao DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   */
  @Inject
  public OtherChildInPlacemtHomeIndexerJob(final ReplicatedOtherChildInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
      FlightRecorder jobHistory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, null);
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
    LaunchDirector.runStandalone(OtherChildInPlacemtHomeIndexerJob.class, args);
  }

}
