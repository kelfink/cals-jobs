package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtils;

/**
 * Job to load collateral individuals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public final class CollateralIndividualIndexerJob
    extends BasePersonIndexerJob<ReplicatedCollateralIndividual, ReplicatedCollateralIndividual> {

  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao collateral individual DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line options
   */
  @Inject
  public CollateralIndividualIndexerJob(final ReplicatedCollateralIndividualDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
      FlightRecorder jobHistory, FlightPlan opts) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, opts);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtils.getCommonPartitionRanges64(this);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(CollateralIndividualIndexerJob.class, args);
  }

}
