package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.quartz.listeners.JobChainingJobListener;

import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.neutron.launch.StandardFlightSchedule;

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
  public void getName_Args__() throws Exception {
    target = StandardFlightSchedule.CHILD_CASE;
    String actual = target.getRocketName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isNewDocument_Args__() throws Exception {
    target = StandardFlightSchedule.CLIENT;
    boolean actual = target.isNewDocument();
    assertThat(actual, is(equalTo(true)));
  }

  @Test
  public void getStartDelaySeconds_Args__() throws Exception {
    target = StandardFlightSchedule.COLLATERAL_INDIVIDUAL;
    int actual = target.getStartDelaySeconds();
    int expected = 10;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void getPeriodSeconds_Args__() throws Exception {
    target = StandardFlightSchedule.PARENT_CASE;
    int actual = target.getWaitPeriodSeconds();
    int expected = 0;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void getLoadOrder_Args__() throws Exception {
    target = StandardFlightSchedule.COLLATERAL_INDIVIDUAL;
    int actual = target.getLastRunPriority();
    assertThat(actual, is(not(5)));
  }

  @Test
  public void getJsonElement_Args__() throws Exception {
    target = StandardFlightSchedule.REFERRAL;
    String actual = target.getNestedElement();
    String expected = "referrals";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lookupByJobName_Args__String() throws Exception {
    String key = "client";
    StandardFlightSchedule actual = StandardFlightSchedule.lookupByJobName(key);
    StandardFlightSchedule expected = StandardFlightSchedule.CLIENT;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildFullLoadJobChainListener_Args__() throws Exception {
    JobChainingJobListener actual = StandardFlightSchedule.buildInitialLoadJobChainListener();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getShortName_Args__() throws Exception {
    target = StandardFlightSchedule.CLIENT;
    String actual = target.getRocketName();
    String expected = "client";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getWaitPeriodSeconds_Args__() throws Exception {
    target = StandardFlightSchedule.CLIENT;
    int actual = target.getWaitPeriodSeconds();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void getLastRunPriority_Args__() throws Exception {
    target = StandardFlightSchedule.CLIENT;
    int actual = target.getLastRunPriority();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void getInitialLoadOrder_Args__() throws Exception {
    target = StandardFlightSchedule.CLIENT;
    int actual = target.getInitialLoadOrder();
    int expected = 5;
    assertThat(actual, is(equalTo(expected)));
  }

}
