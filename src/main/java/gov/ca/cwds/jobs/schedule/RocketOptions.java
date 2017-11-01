package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.config.JobOptions;

@Singleton
public class RocketOptions implements AtomFlightSettings {

  private final JobOptions globalOpts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public RocketOptions(final JobOptions baseOpts) {
    this.globalOpts = baseOpts;
  }

  @Override
  public JobOptions getFlightSettings(Class<?> klazz) {
    return optionsRegistry.get(klazz);
  }

  @Override
  public void addFlightSettings(Class<?> klazz, JobOptions opts) {
    if (!optionsRegistry.containsKey(klazz)) {
      optionsRegistry.put(klazz, opts);
    }
  }

  public JobOptions getGlobalOpts() {
    return globalOpts;
  }

}
