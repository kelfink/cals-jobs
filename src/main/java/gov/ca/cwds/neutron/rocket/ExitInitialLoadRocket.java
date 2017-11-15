package gov.ca.cwds.neutron.rocket;

import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;

/**
 * Exits the initial load job cycle.
 * 
 * @author CWDS API Team
 */
public class ExitInitialLoadRocket
    extends BasePersonRocket<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(ExitInitialLoadRocket.class);

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao random DAO for parent class
   * @param esDao ElasticSearch DAO
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public ExitInitialLoadRocket(final ReplicatedOtherAdultInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, final ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, flightPlan.getLastRunLoc(), mapper, dao.getSessionFactory(), flightPlan);
  }

  @Override
  public Date executeJob(Date lastRunDate) {
    LOGGER.info("EXIT INITIAL LOAD!");
    try {
      LaunchCommand.getInstance().shutdown();
    } catch (Exception e) {
      JobLogs.checked(LOGGER, e, "ES INDEX MANAGEMENT ERROR! {}", e.getMessage());
    }
    return lastRunDate;
  }

}
