package gov.ca.cwds.generic.jobs.inject;

import com.google.inject.tools.jmx.Manager;
import gov.ca.cwds.generic.jobs.BasePersonIndexerJob;
import gov.ca.cwds.generic.jobs.EducationProviderContactIndexerJob;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import gov.ca.cwds.generic.jobs.exception.NeutronException;
import gov.ca.cwds.generic.jobs.util.JobLogs;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private static boolean testMode = false;

  private static boolean continuousMode = false;

  private static JobOptions startingOpts;

  private static final Map<Class<?>, BasePersonIndexerJob<?, ?>> jobs = new ConcurrentHashMap<>();

  private JobRunner() {
    // Default, no-op
  }

  /**
   * Entry point for standalone batch jobs, typically for initial load.
   * 
   * <p>
   * This method automatically closes the Hibernate session factory and ElasticSearch DAO and EXITs
   * the JVM.
   * </p>
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void runStandalone(final Class<T> klass,
      String... args) {
    int exitCode = 0;
    setContinuousMode(false);

    try (final T job = JobsGuiceInjector.newJob(klass, args)) {
      job.run();
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close orphaned resources forcibly, if necessary, by system exit.
      exitCode = 1;
      throw JobLogs.buildException(LOGGER, e, "STANDALONE JOB FAILED!: {}", e.getMessage());
    } finally {
      // WARNING: kills the JVM in testing but may be needed to shutdown resources.
      if (!isTestMode() && !isContinuousMode()) {
        // Shutdown all remaining resources, even those not attached to this job.
        Runtime.getRuntime().exit(exitCode); // NOSONAR
      }
    }
  }

  /**
   * Register a continuously running job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   * @throws NeutronException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void registerContinuousJob(
      final Class<T> klass, String... args) throws NeutronException {
    try (final T job = JobsGuiceInjector.newJob(klass, args);) {
      setContinuousMode(true);
      setStartingOpts(job.getOpts());
      jobs.put(klass, job);
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception, for ClassNotFound or the like.
      throw JobLogs.buildCheckedException(LOGGER, e, "JOB REGISTRATION FAILED!: {}",
          e.getMessage());
    }
  }

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @throws NeutronException unexpected runtime error
   */
  public static void runRegisteredJob(final Class<?> klass, String... args)
      throws NeutronException {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final BasePersonIndexerJob<?, ?> job =
          (BasePersonIndexerJob<?, ?>) JobsGuiceInjector.buildInjector(opts).getInstance(klass);
      job.setOpts(opts);
      job.run();
    } catch (Exception e) {
      throw JobLogs.buildCheckedException(LOGGER, e, "REGISTERED JOB RUN FAILED!: {}",
          e.getMessage());
    }
  }

  /**
   * Load all job definitions and continue running after a job completes.
   * 
   * @return true if running in continuous mode
   */
  public static boolean isContinuousMode() {
    return continuousMode;
  }

  protected static void setContinuousMode(boolean mode) {
    continuousMode = mode;
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @return whether in test mode
   */
  public static boolean isTestMode() {
    return testMode;
  }

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   * 
   * @param mode whether in test mode
   */
  public static void setTestMode(boolean mode) {
    testMode = mode;
  }

  public static synchronized JobOptions getStartingOpts() {
    return startingOpts;
  }

  protected static synchronized void setStartingOpts(JobOptions startingOpts) {
    if (JobRunner.startingOpts == null) {
      JobRunner.startingOpts = startingOpts;
    }
  }

  public static void main(String[] args) {
    LOGGER.info("START ON DEMAND JOBS");
    try {
      // OPTION: configure individual jobs, just like Rundeck.
      JobRunner
          .registerContinuousJob(EducationProviderContactIndexerJob.class, args);
      Manager.manage("Neutron", JobsGuiceInjector.getInjector());
    } catch (Exception e) {
      LOGGER.error("FATAL ERROR! {}", e.getMessage(), e);
    }
  }

}
