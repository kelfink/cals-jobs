package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedServiceProvider;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtils;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;

/**
 * Job to load Service Provider from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ServiceProviderIndexerJob
    extends BasePersonIndexerJob<ReplicatedServiceProvider, ReplicatedServiceProvider> {

  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao ServiceProvider DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line opts
   */
  @Inject
  public ServiceProviderIndexerJob(final ReplicatedServiceProviderDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
      FlightRecorder jobHistory, FlightPlan opts) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, opts);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtils.getCommonPartitionRanges16(this);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(ServiceProviderIndexerJob.class, args);
  }

}
