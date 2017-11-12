package gov.ca.cwds.neutron.vox.jmx.cmd;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.vox.jmx.VoxJMXCommandClient;

public class VoxCommandShutdown extends VoxJMXCommandClient {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandShutdown.class);

  public VoxCommandShutdown() {
    super();
  }

  public VoxCommandShutdown(String host, String port) {
    super(host, port);
  }

  @Override
  public String run() {
    String ret = "SHUT DOWN!";
    LOGGER.warn("SHUTDOWN DOWN COMMAND CENTER!");

    try {
      getMbean().shutdown();
    } catch (NeutronException e) {
      LOGGER.error("FAILED TO SHUTDOWN DOWN COMMAND CENTER! {}", e.getMessage(), e);
      ret = JobLogs.stackToString(e);
    }

    return ret;
  }

}
