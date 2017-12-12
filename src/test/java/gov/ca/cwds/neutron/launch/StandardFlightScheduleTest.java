package gov.ca.cwds.neutron.launch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
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
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getInitialLoadRockets_Args__() throws Exception {
    List<StandardFlightSchedule> actual = StandardFlightSchedule.getInitialLoadRockets();

    List<StandardFlightSchedule> expected = new ArrayList<>();
    expected.add(StandardFlightSchedule.RESET_INDEX);
    expected.add(StandardFlightSchedule.CLIENT);
    expected.add(StandardFlightSchedule.REPORTER);
    expected.add(StandardFlightSchedule.COLLATERAL_INDIVIDUAL);
    expected.add(StandardFlightSchedule.SERVICE_PROVIDER);
    expected.add(StandardFlightSchedule.SUBSTITUTE_CARE_PROVIDER);
    expected.add(StandardFlightSchedule.EDUCATION_PROVIDER);
    expected.add(StandardFlightSchedule.OTHER_ADULT_IN_HOME);
    expected.add(StandardFlightSchedule.OTHER_CHILD_IN_HOME);
    expected.add(StandardFlightSchedule.OTHER_CLIENT_NAME);
    expected.add(StandardFlightSchedule.CASES);
    expected.add(StandardFlightSchedule.RELATIONSHIP);
    expected.add(StandardFlightSchedule.REFERRAL);
    expected.add(StandardFlightSchedule.SAFETY_ALERT);
    expected.add(StandardFlightSchedule.INTAKE_SCREENING);
    expected.add(StandardFlightSchedule.EXIT_INITIAL_LOAD);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChangeRockets_Args__() throws Exception {
    List<StandardFlightSchedule> actual = StandardFlightSchedule.getLastChangeRockets();

    List<StandardFlightSchedule> expected = new ArrayList<>();
    expected.add(StandardFlightSchedule.CLIENT);
    expected.add(StandardFlightSchedule.REPORTER);
    expected.add(StandardFlightSchedule.COLLATERAL_INDIVIDUAL);
    expected.add(StandardFlightSchedule.SERVICE_PROVIDER);
    expected.add(StandardFlightSchedule.SUBSTITUTE_CARE_PROVIDER);
    expected.add(StandardFlightSchedule.EDUCATION_PROVIDER);
    expected.add(StandardFlightSchedule.OTHER_ADULT_IN_HOME);
    expected.add(StandardFlightSchedule.OTHER_CHILD_IN_HOME);
    expected.add(StandardFlightSchedule.OTHER_CLIENT_NAME);
    expected.add(StandardFlightSchedule.CASES);
    expected.add(StandardFlightSchedule.RELATIONSHIP);
    expected.add(StandardFlightSchedule.REFERRAL);
    expected.add(StandardFlightSchedule.SAFETY_ALERT);
    expected.add(StandardFlightSchedule.INTAKE_SCREENING);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getRocketName_Args__() throws Exception {
    String actual = target.getRocketName();
    String expected = "client";
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
    int expected = 5;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getWaitPeriodSeconds_Args__() throws Exception {
    int actual = target.getWaitPeriodSeconds();
    int expected = 20;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastRunPriority_Args__() throws Exception {
    int actual = target.getLastRunPriority();
    int expected = 1000;
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
    String key = StandardFlightSchedule.CASES.getRocketName();
    StandardFlightSchedule actual = StandardFlightSchedule.lookupByRocketName(key);
    StandardFlightSchedule expected = StandardFlightSchedule.CASES;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lookupByClass_Args__Class() throws Exception {
    Class<?> key = StandardFlightSchedule.CLIENT.getRocketClass();
    StandardFlightSchedule actual = StandardFlightSchedule.lookupByRocketClass(key);
    StandardFlightSchedule expected = StandardFlightSchedule.CLIENT;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadOrder_Args__() throws Exception {
    int actual = target.getInitialLoadOrder();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRunLastChange_Args__() throws Exception {
    boolean actual = target.isRunLastChange();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRunLastChange_Args__2() throws Exception {
    target = StandardFlightSchedule.EXIT_INITIAL_LOAD;
    boolean actual = target.isRunLastChange();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRunInitialLoad_Args__() throws Exception {
    boolean actual = target.isRunInitialLoad();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
