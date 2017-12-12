package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAttorney;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.launch.FlightRecorder;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;

/**
 * Job to load attorneys from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OrigAttorneyIndexerJob
    extends BasePersonRocket<ReplicatedAttorney, ReplicatedAttorney> {

  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao ReplicatedAttorney DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line options
   */
  @Inject
  public OrigAttorneyIndexerJob(final ReplicatedAttorneyDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory,
      FlightPlan opts) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, opts);
  }

  /**
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(OrigAttorneyIndexerJob.class, args);
  }

}
