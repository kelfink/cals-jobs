package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.JobOptions;

public interface AtomRocketOptions {

  JobOptions getRocketOptions(Class<?> klazz, String jobName);

  void addRocketOptions(Class<?> klazz, String jobName, JobOptions inOpts);

}
