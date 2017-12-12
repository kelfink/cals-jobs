package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedReporter;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.util.jdbc.NeutronJdbcUtils;

/**
 * Rocket to load reporters from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReporterIndexerJob extends BasePersonRocket<ReplicatedReporter, ReplicatedReporter> {

  private static final long serialVersionUID = 1L;

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public ReporterIndexerJob(final ReplicatedReporterDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastRunFile, final ObjectMapper mapper,
      FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
  }

  @Override
  public String getIdColumn() {
    return "FKREFERL_T";
  }

  @Override
  public List<Pair<String, String>> getPartitionRanges() throws NeutronException {
    return NeutronJdbcUtils.getCommonPartitionRanges64(this);
  }

  /**
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(ReporterIndexerJob.class, args);
  }

}
