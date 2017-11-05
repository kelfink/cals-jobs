package gov.ca.cwds.jobs.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;

public class NeutronThreadUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronThreadUtils.class);

  private NeutronThreadUtils() {
    // static methods only
  }

  /**
   * Calculate the number of reader threads to run from incoming job options and available
   * processors.
   * 
   * @param opts job options
   * @return number of reader threads to run
   */
  public static int calcReaderThreads(final FlightPlan opts) {
    final int ret = opts.getThreadCount() != 0L ? (int) opts.getThreadCount()
        : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
    LOGGER.info(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", ret);
    return ret;
  }

  /**
   * Common method sets the thread's name.
   * 
   * @param title title of thread
   * @param obj calling object
   */
  public static void nameThread(final String title, final Object obj) {
    Thread.currentThread().setName(obj.getClass().getSimpleName() + "_" + title);
  }

  public static void catchYourBreath() {
    try {
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue()); // NOSONAR
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
