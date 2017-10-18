package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.data.std.ApiMarker;

public class NeutronJobManagementBean implements ApiMarker {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private final String jobName;
  private final String command;
  private final String config;

  public NeutronJobManagementBean(String jobName, String command, String body) {
    this.jobName = jobName;
    this.command = command;
    this.config = body;
  }

  public String getJobName() {
    return jobName;
  }

  public String getCommand() {
    return command;
  }

  public String getConfig() {
    return config;
  }

}
