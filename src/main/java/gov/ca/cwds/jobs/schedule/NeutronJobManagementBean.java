package gov.ca.cwds.jobs.schedule;

import gov.ca.cwds.data.std.ApiMarker;

public class NeutronJobManagementBean implements ApiMarker {

  private final String datePath;
  private final String usrHash;
  private final String body;

  public NeutronJobManagementBean(String datePath, String usrHash, String body) {
    this.datePath = datePath;
    this.usrHash = usrHash;
    this.body = body;
  }

  public String getDatePath() {
    return datePath;
  }

  public String getUsrHash() {
    return usrHash;
  }

  public String getBody() {
    return body;
  }

}
