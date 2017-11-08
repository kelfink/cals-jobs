package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.jobs.exception.NeutronException;

public interface AtomLaunchCommand {

  void stopScheduler(boolean waitForJobsToComplete) throws NeutronException;

  void startScheduler() throws NeutronException;
}
