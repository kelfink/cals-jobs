package gov.ca.cwds.jobs.inject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.JobLogUtils;

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
   * @throws JobsException unexpected runtime error
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
      throw JobLogUtils.buildException(LOGGER, e, "STANDALONE JOB FAILED!: {}", e.getMessage());
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
   * @throws JobsException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void registerContinuousJob(
      final Class<T> klass, String... args) {
    try {
      setContinuousMode(true);
      final T job = JobsGuiceInjector.newJob(klass, args);
      setStartingOpts(job.getOpts());
      jobs.put(klass, job);
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception, for ClassNotFound or the like.
      throw JobLogUtils.buildException(LOGGER, e, "JOB REGISTRATION FAILED!: {}", e.getMessage());
    }
  }

  /**
   * Run a registered job.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @throws JobsException unexpected runtime error
   */
  public static void runRegisteredJob(final Class<?> klass, String... args) {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final BasePersonIndexerJob<?, ?> job =
          (BasePersonIndexerJob<?, ?>) JobsGuiceInjector.buildInjector(opts).getInstance(klass);
      job.setOpts(opts);
      job.run();
    } catch (Exception e) {
      throw JobLogUtils.buildException(LOGGER, e, "REGISTERED JOB RUN FAILED!: {}", e.getMessage());
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

  public static JobOptions getStartingOpts() {
    return startingOpts;
  }

  protected static synchronized void setStartingOpts(JobOptions startingOpts) {
    if (JobRunner.startingOpts == null) {
      JobRunner.startingOpts = startingOpts;
    }
  }

}
