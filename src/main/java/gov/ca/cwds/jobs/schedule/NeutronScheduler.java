package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Scheduler;

import gov.ca.cwds.jobs.config.JobOptions;

public class NeutronScheduler {

  private Scheduler scheduler;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  /**
   * Scheduled jobs.
   */
  private final Map<Class<?>, NeutronJobMgtFacade> scheduleRegistry = new ConcurrentHashMap<>();

  public Scheduler getScheduler() {
    return scheduler;
  }

  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public Map<Class<?>, JobOptions> getOptionsRegistry() {
    return optionsRegistry;
  }

  public Map<Class<?>, NeutronJobMgtFacade> getScheduleRegistry() {
    return scheduleRegistry;
  }



}
