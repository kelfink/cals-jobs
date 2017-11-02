package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.config.FlightPlan;

@Singleton
public class FlightPlanLog implements AtomFlightPlanLog {

  private final FlightPlan globalOpts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, FlightPlan> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public FlightPlanLog(final FlightPlan baseOpts) {
    this.globalOpts = baseOpts;
  }

  @Override
  public FlightPlan getFlightSettings(Class<?> klazz) {
    return optionsRegistry.get(klazz);
  }

  @Override
  public void addFlightSettings(Class<?> klazz, FlightPlan opts) {
    if (!optionsRegistry.containsKey(klazz)) {
      optionsRegistry.put(klazz, opts);
    }
  }

  public FlightPlan getGlobalOpts() {
    return globalOpts;
  }

}
