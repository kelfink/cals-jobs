package gov.ca.cwds.jobs.component;

import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.defaults.NeutronIntegerDefaults;

/**
 * Common features of all Elasticsearch indexing jobs.
 * 
 * @author CWDS API Team
 */
public interface AtomShared extends ApiMarker {

  /**
   * @return job's progress tracker
   */
  FlightRecord getTrack();

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
  FlightPlan getOpts();

  /**
   * Common method sets the thread's name.
   * 
   * @param title title of thread
   */
  default void nameThread(final String title) {
    Thread.currentThread().setName(getClass().getSimpleName() + "_" + title);
  }

  default void catchYourBreath() {
    try {
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue()); // NOSONAR
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      getLogger().warn("SLEEP INTERRUPTED!");
    }
  }

}
