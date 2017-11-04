package gov.ca.cwds.jobs.util.jdbc;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.defaults.NeutronIntegerDefaults;

public class NeutronThreadUtils {

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
    JobJdbcUtils.LOGGER.info(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", ret);
    return ret;
  }

  /**
   * Common method sets the thread's name.
   * 
   * @param title title of thread
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
