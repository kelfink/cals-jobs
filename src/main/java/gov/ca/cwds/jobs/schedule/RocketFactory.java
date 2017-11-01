package gov.ca.cwds.jobs.schedule;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.component.AtomRocketFactory;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

@Singleton
public class RocketFactory implements AtomRocketFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(RocketFactory.class);

  private final Injector injector;

  private final JobOptions baseOpts;

  private final RocketOptions rocketOptions;

  @Inject
  public RocketFactory(final Injector injector, final JobOptions opts,
      final RocketOptions rocketOptions) {
    this.injector = injector;
    this.baseOpts = opts;
    this.rocketOptions = rocketOptions;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public BasePersonIndexerJob createJob(Class<?> klass, final JobOptions opts)
      throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());
      final BasePersonIndexerJob ret = (BasePersonIndexerJob<?, ?>) injector.getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO SPAWN ON-DEMAND JOB!: {}", e.getMessage());
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public BasePersonIndexerJob createJob(String jobName, final JobOptions opts)
      throws NeutronException {
    try {
      return createJob(Class.forName(jobName), opts);
    } catch (ClassNotFoundException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO CREATE ROCKET!: {}", e.getMessage());
    }
  }

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
    final JobDetail jd = bundle.getJobDetail();

    Class<?> klazz;
    try {
      klazz = Class.forName(jd.getJobDataMap().getString(NeutronSchedulerConstants.ROCKET_CLASS));
    } catch (ClassNotFoundException e) {
      throw new SchedulerException("NO SUCH ROCKET CLASS!", e);
    }

    LOGGER.warn("LAUNCH! {}", klazz);

    NeutronInterruptableJob ret;
    try {
      final JobOptions opts = rocketOptions.getFlightSettings(klazz, jd.getKey().getName());
      ret = new NeutronInterruptableJob(createJob(klazz, opts));
    } catch (NeutronException e) {
      throw new SchedulerException("NO ROCKET SETTINGS!", e);
    }

    return ret;
  }

  public JobOptions getBaseOpts() {
    return baseOpts;
  }

  public RocketOptions getRocketOptions() {
    return rocketOptions;
  }

}
