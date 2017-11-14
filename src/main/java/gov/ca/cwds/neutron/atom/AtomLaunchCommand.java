package gov.ca.cwds.neutron.atom;

import gov.ca.cwds.jobs.exception.NeutronException;

public interface AtomLaunchCommand {

  void stopScheduler(boolean waitForJobsToComplete) throws NeutronException;

  void startScheduler() throws NeutronException;

  void shutdown() throws NeutronException;
}
