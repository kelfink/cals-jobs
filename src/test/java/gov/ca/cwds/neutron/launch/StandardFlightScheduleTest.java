package gov.ca.cwds.neutron.launch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.quartz.listeners.JobChainingJobListener;

import gov.ca.cwds.jobs.Goddard;

public class StandardFlightScheduleTest extends Goddard {
  StandardFlightSchedule target;

  @Override
  public void setup() throws Exception {
    super.setup();
    target = StandardFlightSchedule.CLIENT;
  }

  @Test
  public void type() throws Exception {
    assertThat(StandardFlightSchedule.class, notNullValue());
  }

  @Test
  public void buildInitialLoadJobChainListener_Args__() throws Exception {
    JobChainingJobListener actual = StandardFlightSchedule.buildInitialLoadJobChainListener();
    JobChainingJobListener expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadRockets_Args__() throws Exception {
    List<StandardFlightSchedule> actual = StandardFlightSchedule.getInitialLoadRockets();
    List<StandardFlightSchedule> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChangeRockets_Args__() throws Exception {
    List<StandardFlightSchedule> actual = StandardFlightSchedule.getLastChangeRockets();
    List<StandardFlightSchedule> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getRocketName_Args__() throws Exception {
    String actual = target.getRocketName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isNewDocument_Args__() throws Exception {
    boolean actual = target.isNewDocument();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isNewDocument_Args__2() throws Exception {
    target = StandardFlightSchedule.RELATIONSHIP;
    boolean actual = target.isNewDocument();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getStartDelaySeconds_Args__() throws Exception {
    int actual = target.getStartDelaySeconds();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getWaitPeriodSeconds_Args__() throws Exception {
    int actual = target.getWaitPeriodSeconds();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastRunPriority_Args__() throws Exception {
    int actual = target.getLastRunPriority();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNestedElement_Args__() throws Exception {
    StandardFlightSchedule target = StandardFlightSchedule.CLIENT;
    String actual = target.getNestedElement();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lookupByJobName_Args__String() throws Exception {
    String key = null;
    StandardFlightSchedule actual = StandardFlightSchedule.lookupByJobName(key);
    StandardFlightSchedule expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lookupByClass_Args__Class() throws Exception {
    Class<?> key = StandardFlightSchedule.CLIENT.getRocketClass();
    StandardFlightSchedule actual = StandardFlightSchedule.lookupByClass(key);
    StandardFlightSchedule expected = StandardFlightSchedule.CLIENT;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadOrder_Args__() throws Exception {
    int actual = target.getInitialLoadOrder();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRunLastChange_Args__() throws Exception {
    boolean actual = target.isRunLastChange();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRunInitialLoad_Args__() throws Exception {
    boolean actual = target.isRunInitialLoad();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
