package gov.ca.cwds.neutron.vox.jmx.cmd;

import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.vox.jmx.VoxJMXCommandClient;

public class VoxCommandLastRunStatus extends VoxJMXCommandClient {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandLastRunStatus.class);

  public VoxCommandLastRunStatus() {
    super();
  }

  public VoxCommandLastRunStatus(String host, String port) {
    super(host, port);
  }

  @Override
  public String run() {
    final String ret = getMbean().status();
    LOGGER.info("status: {}", ret);
    return ret;
  }

}
