package gov.ca.cwds.neutron.vox.jmx;

import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;

public class VoxCommandLastRunStatus extends VoxJMXCommandClient implements VoxCommandAction {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandLastRunStatus.class);

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
