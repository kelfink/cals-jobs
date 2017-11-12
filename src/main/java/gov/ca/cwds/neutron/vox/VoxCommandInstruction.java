package gov.ca.cwds.neutron.vox;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.schedule.StandardFlightSchedule;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.vox.jmx.VoxJmxDefaults;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class VoxCommandInstruction implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxCommandInstruction.class);

  private String rocket;
  private String host;
  private String port;
  private String command;

  public VoxCommandInstruction() {
    // default
  }

  public VoxCommandInstruction(String rocket, String command, String config) {
    this.rocket = rocket;

  }

  public static VoxCommandInstruction parseCommandLine(final String[] args) {
    LOGGER.info("PARSE COMMAND LINE");
    VoxCommandInstruction ret = new VoxCommandInstruction();
    final OptionParser parser = new OptionParser("h:p:r:c:");
    final OptionSet options = parser.parse(args);

    final String host =
        options.has("h") ? (String) options.valueOf("h") : VoxJmxDefaults.DEFAULT_HOST;
    final String port =
        options.has("p") ? (String) options.valueOf("p") : VoxJmxDefaults.DEFAULT_PORT;
    final String rocket = options.has("r") ? (String) options.valueOf("r")
        : StandardFlightSchedule.CLIENT.getShortName();
    final String cmd = options.has("c") ? (String) options.valueOf("c") : "status";

    ret.setHost(host);
    ret.setPort(port);
    ret.setRocket(rocket);
    ret.setCommand(cmd);

    LOGGER.info("VOX COMMAND: host: {}, port: {}, rocket: {}", host, port, rocket);
    return ret;
  }

  public String getRocket() {
    return rocket;
  }

  public String getCommand() {
    return command;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setRocket(String rocket) {
    this.rocket = rocket;
  }

  public void setCommand(String command) {
    this.command = command;
  }

}
