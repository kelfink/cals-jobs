package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.config.JobOptions;

public interface AtomRocketOptions {

  JobOptions getRocketOptions(String jobName, Class<?> klazz);

  void addRocketOptions(Class<?> klazz, JobOptions opts);

}
