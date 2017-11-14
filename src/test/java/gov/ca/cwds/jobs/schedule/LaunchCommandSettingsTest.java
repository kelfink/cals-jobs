package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gov.ca.cwds.neutron.launch.LaunchCommandSettings;

public class LaunchCommandSettingsTest {

  @Test
  public void type() throws Exception {
    assertThat(LaunchCommandSettings.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    assertThat(target, notNullValue());
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean actual = target.isTestMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean testMode = false;
    target.setTestMode(testMode);
  }

  @Test
  public void isContinuousMode_Args__() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean actual = target.isSchedulerMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setContinuousMode_Args__boolean() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean continuousMode = false;
    target.setSchedulerMode(continuousMode);
  }

  @Test
  public void isInitialMode_Args__() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean actual = target.isInitialMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialMode_Args__boolean() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean initialMode = false;
    target.setInitialMode(initialMode);
  }

  @Test
  public void isMinimalTestMode_Args__() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean actual = target.isMinimalTestMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMinimalTestMode_Args__boolean() throws Exception {
    LaunchCommandSettings target = new LaunchCommandSettings();
    boolean minimalTestMode = false;
    target.setMinimalTestMode(minimalTestMode);
  }

}
