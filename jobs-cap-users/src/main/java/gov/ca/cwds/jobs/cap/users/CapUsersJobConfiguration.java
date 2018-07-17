package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;

public class CapUsersJobConfiguration extends BaseJobConfiguration {

  private String perryApiUrl;
  private String perryApiUser;
  private String perryApiPassword;
  private int jerseyClientConnectTimeout;
  private int jerseyClientReadTimeout;

  public String getPerryApiUrl() {
    return perryApiUrl;
  }

  public void setPerryApiUrl(String perryApiUrl) {
    this.perryApiUrl = perryApiUrl;
  }

  public String getPerryApiUser() {
    return perryApiUser;
  }

  public void setPerryApiUser(String perryApiUser) {
    this.perryApiUser = perryApiUser;
  }

  public String getPerryApiPassword() {
    return perryApiPassword;
  }

  public void setPerryApiPassword(String perryApiPassword) {
    this.perryApiPassword = perryApiPassword;
  }

  public int getJerseyClientConnectTimeout() {
    return jerseyClientConnectTimeout;
  }

  public void setJerseyClientConnectTimeout(int jerseyClientConnectTimeout) {
    this.jerseyClientConnectTimeout = jerseyClientConnectTimeout;
  }

  public int getJerseyClientReadTimeout() {
    return jerseyClientReadTimeout;
  }

  public void setJerseyClientReadTimeout(int jerseyClientReadTimeout) {
    this.jerseyClientReadTimeout = jerseyClientReadTimeout;
  }
}
