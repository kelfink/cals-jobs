package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.JobOptions;

public interface AtomFlightSettings {

  JobOptions getFlightSettings(Class<?> klazz);

  void addFlightSettings(Class<?> klazz, JobOptions opts);

}
