package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.config.JobOptions;

@Singleton
public class RocketOptions implements AtomFlightSettings {

  private final JobOptions baseOpts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public RocketOptions(final JobOptions baseOpts) {
    this.baseOpts = baseOpts;
  }

  @Override
  public JobOptions getFlightSettings(Class<?> klazz, String jobName) {
    return optionsRegistry.get(klazz);
  }

  @Override
  public void addFlightSettings(Class<?> klazz, String jobName, JobOptions opts) {
    optionsRegistry.put(klazz, opts);
  }

  public JobOptions getBaseOpts() {
    return baseOpts;
  }

}
