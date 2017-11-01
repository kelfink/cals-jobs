package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.JobOptions;

public interface AtomFlightPlan {

  JobOptions getFlightSettings(Class<?> klazz);

  void addFlightSettings(Class<?> klazz, JobOptions opts);

}
