package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.neutron.flight.FlightPlan;

public interface AtomFlightPlanManager {

  FlightPlan getFlightPlan(Class<?> klazz);

  void addFlightPlan(Class<?> klazz, FlightPlan opts);

}
