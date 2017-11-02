package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.FlightPlan;

public interface AtomFlightPlanLog {

  FlightPlan getFlightSettings(Class<?> klazz);

  void addFlightSettings(Class<?> klazz, FlightPlan opts);

}
