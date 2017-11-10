package gov.ca.cwds.neutron.atom;

import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.util.jdbc.NeutronThreadUtil;
import gov.ca.cwds.neutron.flight.FlightLog;

/**
 * Common features of all Elasticsearch indexing jobs.
 * 
 * @author CWDS API Team
 */
public interface AtomShared extends ApiMarker {

  /**
   * @return the rocket's progress tracker
   */
  FlightLog getFlightLog();

  /**
   * @return Elasticsearch DAO
   */
  ElasticsearchDao getEsDao();

  /**
   * Make logger available to interfaces.
   * 
   * @return SLF4J logger
   */
  Logger getLogger();

  /**
   * Getter for the job's command line options.
   * 
   * @return this job's options
   */
  FlightPlan getFlightPlan();

  /**
   * Common method sets the thread's name.
   * 
   * @param title title of thread
   */
  default void nameThread(final String title) {
    NeutronThreadUtil.nameThread(title, this);
  }

  default void catchYourBreath() {
    NeutronThreadUtil.catchYourBreath();
  }

}
