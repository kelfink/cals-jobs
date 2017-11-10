package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.jobs.config.FlightPlan;

public interface AtomFlightPlanManager {

  FlightPlan getFlightPlan(Class<?> klazz);

  void addFlightPlan(Class<?> klazz, FlightPlan opts);

}
