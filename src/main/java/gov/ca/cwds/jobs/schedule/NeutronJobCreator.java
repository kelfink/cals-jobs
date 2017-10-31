package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomJobCreator;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.util.JobLogs;

public class NeutronJobCreator implements AtomJobCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJobCreator.class);

  private final Injector injector;

  private final JobOptions opts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public NeutronJobCreator(final Injector injector, final JobOptions opts) {
    this.injector = injector;
    this.opts = opts;
  }

  @Override
  public BasePersonIndexerJob createJob(Class<?> klass, String... args) throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());
      final JobOptions jobOpts = args != null && args.length > 1 ? JobOptions.parseCommandLine(args)
          : optionsRegistry.get(klass);

      if (this.opts == null) { // HACK: **inject dependencies**
        this.opts = jobOpts;
      }

      final BasePersonIndexerJob<?, ?> job =
          (BasePersonIndexerJob<?, ?>) JobsGuiceInjector.getInjector().getInstance(klass);
      job.setOpts(jobOpts);
      return job;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!: {}", e.getMessage());
    }
  }

  @Override
  public BasePersonIndexerJob createJob(String jobName, String... args) throws NeutronException {
    try {
      return createJob(Class.forName(jobName), args);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!!: {}", e.getMessage());
    }
  }

}
