package gov.ca.cwds.neutron.vox.jmx.cmd;

import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.vox.jmx.VoxJMXCommandClient;

public class VoxCommandFetchLogs extends VoxJMXCommandClient {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandFetchLogs.class);

  public VoxCommandFetchLogs() {
    super();
  }

  public VoxCommandFetchLogs(String host, String port) {
    super(host, port);
  }

  @Override
  public String run() {
    LOGGER.info("Pull logs for rocket {}", getRocket());
    return getMbean().logs();
  }

}
