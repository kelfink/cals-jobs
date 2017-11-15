package gov.ca.cwds.neutron.rocket;

import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.launch.LaunchDirector;
import gov.ca.cwds.neutron.launch.StandardFlightSchedule;

/**
 * Exits the initial load job cycle.
 * 
 * @author CWDS API Team
 */
public class ExitInitialLoadRocket
    extends BasePersonRocket<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(ExitInitialLoadRocket.class);

  private final LaunchDirector launchDirector;

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
      final ElasticsearchDao esDao, final ObjectMapper mapper, LaunchDirector launchDirector,
      FlightPlan flightPlan) {
    super(dao, esDao, flightPlan.getLastRunLoc(), mapper, dao.getSessionFactory(), flightPlan);
    this.launchDirector = launchDirector;
  }

  @Override
  public Date executeJob(Date lastRunDate) {
    if (LaunchCommand.isInitialMode()) {
      LOGGER.warn("EXIT INITIAL LOAD!");
      final AtomFlightRecorder flightRecorder = launchDirector.getFlightRecorder();

      try {
        for (StandardFlightSchedule sched : StandardFlightSchedule.getInitialLoadRockets()) {
          LOGGER.warn("Rocket summary: {}", flightRecorder.getFlightSummary(sched));
        }

        LaunchCommand.getInstance().shutdown();
      } catch (Exception e) {
        JobLogs.checked(LOGGER, e, "ES INDEX MANAGEMENT ERROR! {}", e.getMessage());
      }
    }

    return lastRunDate;
  }

}
