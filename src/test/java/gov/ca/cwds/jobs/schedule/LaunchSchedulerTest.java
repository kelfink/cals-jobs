package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.AtomRocketFactory;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.test.Mach1TestRocket;

public class LaunchSchedulerTest extends PersonJobTester {

  FlightRecorder jobHistory;
  RocketFactory rocketFactory;
  FlightPlanLog rocketOptions;
  TriggerKey key;
  Scheduler scheduler;
  LaunchPad launchPad;

  LaunchScheduler target;


  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    jobHistory = mock(FlightRecorder.class);
    rocketFactory = mock(RocketFactory.class);
    rocketOptions = mock(FlightPlanLog.class);
    scheduler = mock(Scheduler.class);
    launchPad = mock(LaunchPad.class);

    key = new TriggerKey("el_trigger", NeutronSchedulerConstants.GRP_LST_CHG);
    target = new LaunchScheduler(jobHistory, rocketFactory, rocketOptions);
    target.setScheduler(scheduler);
    target.setOpts(opts);

    target.getScheduleRegistry().put(Mach1TestRocket.class, launchPad);
  }

  @Test
  public void type() throws Exception {
    assertThat(LaunchScheduler.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void createJob_Args__Class__FlightPlan() throws Exception {
    Class<?> klass = Mach1TestRocket.class;
    BasePersonIndexerJob actual = target.createJob(klass, opts);
    BasePersonIndexerJob expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void createJob_Args__String__FlightPlan() throws Exception {
    String jobName = null;
    BasePersonIndexerJob actual = target.createJob(jobName, opts);
    BasePersonIndexerJob expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test(expected = NeutronException.class)
  public void runScheduledJob_Args__Class__FlightPlan() throws Exception {
    Class<?> klass = Mach1TestRocket.class;
    FlightRecord actual = target.runScheduledJob(klass, opts);
    FlightRecord expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void runScheduledJob_Args__Class__FlightPlan_T__NeutronException() throws Exception {
    Class<?> klass = Mach1TestRocket.class;
    try {
      target.runScheduledJob(klass, opts);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test(expected = NeutronException.class)
  public void runScheduledJob_Args__String__FlightPlan() throws Exception {
    String jobName = Mach1TestRocket.class.getName();
    FlightRecord actual = target.runScheduledLaunch(jobName, opts);
    FlightRecord expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void scheduleJob_Args__Class__DefaultFlightSchedule__FlightPlan() throws Exception {
    Class<?> klass = Mach1TestRocket.class;
    DefaultFlightSchedule sched = DefaultFlightSchedule.CLIENT;
    LaunchPad actual = target.scheduleLaunch(klass, sched, opts);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void stopScheduler_Args__boolean() throws Exception {
    boolean waitForJobsToComplete = false;
    target.stopScheduler(waitForJobsToComplete);
  }

  @Test
  public void startScheduler_Args__() throws Exception {
    target.startScheduler();
  }

  @Test
  public void addExecutingJob_Args__TriggerKey__NeutronRocket() throws Exception {
    NeutronRocket job = mock(NeutronRocket.class);
    target.trackInFlightRocket(key, job);
  }

  @Test
  public void removeExecutingJob_Args__TriggerKey() throws Exception {
    target.removeExecutingJob(key);
  }

  @Test
  public void getExecutingJobs_Args__() throws Exception {
    Map<TriggerKey, NeutronRocket> actual = target.getExecutingJobs();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getRocketFactory_Args__() throws Exception {
    AtomRocketFactory actual = target.getRocketFactory();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    FlightPlan actual = target.getOpts();
    // assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setOpts_Args__FlightPlan() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void getScheduler_Args__() throws Exception {
    Scheduler actual = target.getScheduler();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setScheduler_Args__Scheduler() throws Exception {
    Scheduler scheduler = mock(Scheduler.class);
    target.setScheduler(scheduler);
  }

  @Test
  public void isJobVetoed_Args__String() throws Exception {
    String className = Mach1TestRocket.class.getName();
    boolean actual = target.isLaunchVetoed(className);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getRocketOptions_Args__() throws Exception {
    AtomFlightPlanLog actual = target.getRocketOptions();
    assertThat(actual, is(notNullValue()));
  }

}
