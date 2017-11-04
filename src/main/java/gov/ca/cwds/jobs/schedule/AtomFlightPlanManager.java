package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.FlightPlan;

public interface AtomFlightPlanManager {

  FlightPlan getFlightPlan(Class<?> klazz);

  void addFlightPlan(Class<?> klazz, FlightPlan opts);

}
