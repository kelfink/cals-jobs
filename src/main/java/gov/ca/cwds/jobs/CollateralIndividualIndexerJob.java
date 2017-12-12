package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;
import gov.ca.cwds.neutron.util.jdbc.NeutronJdbcUtils;

/**
 * Rocket to load collateral individuals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public final class CollateralIndividualIndexerJob
    extends BasePersonRocket<ReplicatedCollateralIndividual, ReplicatedCollateralIndividual> {

  private static final long serialVersionUID = 1L;

  /**
   * Construct rocket with required dependencies.
   * 
   * @param dao collateral individual DAO
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public CollateralIndividualIndexerJob(final ReplicatedCollateralIndividualDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastRunFile,
      final ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
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
    LaunchCommand.launchOneWayTrip(CollateralIndividualIndexerJob.class, args);
  }

}
