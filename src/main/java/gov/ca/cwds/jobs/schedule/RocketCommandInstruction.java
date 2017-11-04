package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.data.std.ApiMarker;

public class RocketCommandInstruction implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private final String rocketName;
  private final String command;
  private final String config;

  public RocketCommandInstruction(String rocketName, String command, String body) {
    this.rocketName = rocketName;
    this.command = command;
    this.config = body;
  }

  public String getRocketName() {
    return rocketName;
  }

  public String getCommand() {
    return command;
  }

  public String getConfig() {
    return config;
  }

  @Override
  public String toString() {
    return "RocketCommandInstruction [jobName=" + rocketName + ", command=" + command + ", config="
        + config + "]";
  }

}
