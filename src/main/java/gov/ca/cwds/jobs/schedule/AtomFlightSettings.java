package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.JobOptions;

public interface AtomFlightSettings {

  JobOptions getFlightSettings(Class<?> klazz, String jobName);

  void addFlightSettings(Class<?> klazz, String jobName, JobOptions opts);

}
