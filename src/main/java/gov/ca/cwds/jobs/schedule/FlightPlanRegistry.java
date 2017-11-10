package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.neutron.atom.AtomFlightPlanManager;

@Singleton
public class FlightPlanRegistry implements AtomFlightPlanManager {

  private final FlightPlan baseFlightPlan;

  /**
   * Flight plans by rocket type.
   */
  private final Map<Class<?>, FlightPlan> registeredFlightPlans = new ConcurrentHashMap<>();

  @Inject
  public FlightPlanRegistry(final FlightPlan baseFlightPlan) {
    this.baseFlightPlan = baseFlightPlan;
  }

  @Override
  public FlightPlan getFlightPlan(Class<?> klazz) {
    return registeredFlightPlans.get(klazz);
  }

  @Override
  public void addFlightPlan(Class<?> klazz, FlightPlan opts) {
    if (!registeredFlightPlans.containsKey(klazz)) {
      registeredFlightPlans.put(klazz, opts);
    }
  }

  public FlightPlan getBaseFlightPlan() {
    return baseFlightPlan;
  }

}
