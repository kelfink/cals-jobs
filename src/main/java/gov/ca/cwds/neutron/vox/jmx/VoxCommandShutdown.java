package gov.ca.cwds.neutron.vox.jmx;

import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;

public class VoxCommandShutdown extends VoxJMXCommandClient {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandShutdown.class);

  public VoxCommandShutdown(String host, String port) {
    super(host, port);
  }

  @Override
  public String run() {
    final String ret = getMbean().status();
    LOGGER.info("status: {}", ret);
    return ret;
  }

}
