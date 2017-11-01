package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomRocketFactory;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

public class RocketFactory implements AtomRocketFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(RocketFactory.class);

  private final Injector injector;

  private final JobOptions baseOpts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public RocketFactory(final Injector injector, final JobOptions opts) {
    this.injector = injector;
    this.baseOpts = opts;
  }

  protected JobOptions getRocketOptions(String jobName, Class<?> klazz) {
    if (optionsRegistry.containsKey(klazz)) {
      final JobOptions opts = new JobOptions(baseOpts);
      opts.setLastRunLoc(opts.getBaseDirectory() + File.separator + jobName + ".time");
    }

    return optionsRegistry.get(klazz);
  }

  @Override
  public BasePersonIndexerJob createJob(Class<?> klass, final JobOptions opts)
      throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());
      return (BasePersonIndexerJob<?, ?>) injector.getInstance(klass);
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!: {}", e.getMessage());
    }
  }

  @Override
  public BasePersonIndexerJob createJob(String jobName, final JobOptions opts)
      throws NeutronException {
    try {
      return createJob(Class.forName(jobName), opts);
    } catch (ClassNotFoundException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!!: {}", e.getMessage());
    }
  }

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
    final JobDetail jd = bundle.getJobDetail();
    final NeutronInterruptableJob job =
        (NeutronInterruptableJob) injector.getInstance(bundle.getJobDetail().getJobClass());
    job.setOpts(getRocketOptions(jd.getKey().getName(), jd.getJobClass()));
    return job;
  }

}
