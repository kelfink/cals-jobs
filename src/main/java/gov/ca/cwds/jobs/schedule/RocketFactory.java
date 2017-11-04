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
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

@Singleton
public class RocketFactory implements AtomRocketFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(RocketFactory.class);

  private final Injector injector;

  private final FlightPlan baseOpts;

  private final FlightPlanLog rocketOptions;

  @Inject
  public RocketFactory(final Injector injector, final FlightPlan opts,
      final FlightPlanLog rocketOptions) {
    this.injector = injector;
    this.baseOpts = opts;
    this.rocketOptions = rocketOptions;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public BasePersonIndexerJob createJob(Class<?> klass, final FlightPlan opts)
      throws NeutronException {
    try {
      LOGGER.info("Create registered job: {}", klass.getName());

      // QUESTION: is there a cleaner way to call this??
      final BasePersonIndexerJob ret = (BasePersonIndexerJob<?, ?>) injector.getInstance(klass);
      ret.init(opts.getLastRunLoc(), opts);
      return ret;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO CREATE ROCKET!: {}", e.getMessage());
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public BasePersonIndexerJob createJob(String jobName, final FlightPlan opts)
      throws NeutronException {
    try {
      return createJob(Class.forName(jobName), opts);
    } catch (ClassNotFoundException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO CREATE ROCKET!: {}", e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
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

    NeutronRocket ret;
    try {
      final FlightPlan opts = rocketOptions.getFlightPlan(klazz);
      ret = new NeutronRocket(createJob(klazz, opts));
    } catch (NeutronException e) {
      throw new SchedulerException("NO ROCKET SETTINGS!", e);
    }

    return ret;
  }

  public FlightPlan getBaseOpts() {
    return baseOpts;
  }

  public FlightPlanLog getRocketOptions() {
    return rocketOptions;
  }

}
