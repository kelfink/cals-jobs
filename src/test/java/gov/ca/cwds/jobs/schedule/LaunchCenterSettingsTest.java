package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LaunchCenterSettingsTest {

  @Test
  public void type() throws Exception {
    assertThat(LaunchCenterSettings.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    assertThat(target, notNullValue());
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean actual = target.isTestMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean testMode = false;
    target.setTestMode(testMode);
  }

  @Test
  public void isContinuousMode_Args__() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean actual = target.isContinuousMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setContinuousMode_Args__boolean() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean continuousMode = false;
    target.setContinuousMode(continuousMode);
  }

  @Test
  public void isInitialMode_Args__() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean actual = target.isInitialMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialMode_Args__boolean() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean initialMode = false;
    target.setInitialMode(initialMode);
  }

  @Test
  public void isMinimalTestMode_Args__() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean actual = target.isMinimalTestMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMinimalTestMode_Args__boolean() throws Exception {
    LaunchCenterSettings target = new LaunchCenterSettings();
    boolean minimalTestMode = false;
    target.setMinimalTestMode(minimalTestMode);
  }

}
