package gov.ca.cwds.neutron.vox.jmx.cmd;

import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.vox.jmx.VoxJMXCommandClient;

public class VoxCommandFlightHistory extends VoxJMXCommandClient {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandFlightHistory.class);

  public VoxCommandFlightHistory() {
    super();
  }

  public VoxCommandFlightHistory(String host, String port) {
    super(host, port);
  }

  @Override
  public String run() {
    final String ret = getMbean().history();
    LOGGER.info("Rocket flight history: {}", ret);
    return ret;
  }

}
