package gov.ca.cwds.neutron.launch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.ClientIndexerJob;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchDirector;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightPlan;

public class LaunchPadTest extends Goddard {

  StandardFlightSchedule sched;
  LaunchPad target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    flightPlan = new FlightPlan();
    when(launchDirector.getFlightRecorder()).thenReturn(flightRecorder);

    sched = StandardFlightSchedule.CLIENT;
    target = new LaunchPad(launchDirector, sched, flightPlan);
  }

  @Test
  public void type() throws Exception {
    assertThat(LaunchPad.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void run_Args__String() throws Exception {
    String cmdLineArgs = null;
    String actual = target.run(cmdLineArgs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void schedule_Args__() throws Exception {
    target.schedule();
  }

  @Test(expected = NeutronException.class)
  public void schedule_Args___T__SchedulerException() throws Exception {
    when(scheduler.getJobDetail(any(JobKey.class))).thenThrow(SchedulerException.class);
    when(launchDirector.launch(any(Class.class), any(FlightPlan.class)))
        .thenThrow(SchedulerException.class);
    when(scheduler.checkExists(any(JobKey.class))).thenThrow(SchedulerException.class);
    target.schedule();
  }

  @Test
  public void unschedule_Args__() throws Exception {
    target.unschedule();
  }

  @Test(expected = NeutronException.class)
  public void unschedule_Args___T__SchedulerException() throws Exception {
    doThrow(SchedulerException.class).when(scheduler).pauseTrigger(any(TriggerKey.class));
    when(scheduler.unscheduleJob(any(TriggerKey.class))).thenThrow(SchedulerException.class);
    target.unschedule();
  }

  @Test
  public void status_Args__() throws Exception {
    JobDetail jd = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(jd);
    JobDataMap jdm = new JobDataMap();
    jdm.put("job_class", "TestNeutronJob");
    jdm.put("cmd_line", "--invalid");
    final FlightLog track = new FlightLog();
    jdm.put("track", track);
    when(jd.getJobDataMap()).thenReturn(jdm);
    flightRecorder.logFlight(ClientIndexerJob.class, track);
    target.status();
  }

  @Test
  public void stop_Args__() throws Exception {
    target.stop();
  }

  @Test(expected = NeutronException.class)
  public void stop_Args___T__SchedulerException() throws Exception {
    doThrow(SchedulerException.class).when(scheduler).pauseTrigger(any(TriggerKey.class));
    when(scheduler.interrupt(any(JobKey.class))).thenThrow(SchedulerException.class);
    target.stop();
  }

  @Test
  public void history_Args__() throws Exception {
    launchDirector = new LaunchDirector(flightRecorder, rocketFactory, flightPlanManager);
    launchDirector.setScheduler(scheduler);
    String actual = target.history();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isVetoExecution_Args__() throws Exception {
    boolean actual = target.isVetoExecution();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVetoExecution_Args__boolean() throws Exception {
    boolean vetoExecution = false;
    target.setVetoExecution(vetoExecution);
  }

  @Test
  public void getJd_Args__() throws Exception {
    target.schedule();
    JobDetail actual = target.getJd();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    FlightPlan actual = target.getFlightPlan();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setOpts_Args__FlightPlan() throws Exception {
    FlightPlan opts_ = mock(FlightPlan.class);
    target.setFlightPlan(opts_);
  }

  @Test
  public void getFlightPlan_Args__() throws Exception {
    FlightPlan actual = target.getFlightPlan();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setFlightPlan_Args__FlightPlan() throws Exception {
    FlightPlan opts_ = mock(FlightPlan.class);
    target.setFlightPlan(opts_);
  }

  @Test
  public void getFlightSchedule_Args__() throws Exception {
    StandardFlightSchedule actual = target.getFlightSchedule();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getFlightRecorder_Args__() throws Exception {
    AtomFlightRecorder actual = target.getFlightRecorder();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getJobName_Args__() throws Exception {
    String actual = target.getRocketName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getTriggerName_Args__() throws Exception {
    String actual = target.getTriggerName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getJobKey_Args__() throws Exception {
    JobKey actual = target.getJobKey();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getLaunchScheduler_Args__() throws Exception {
    AtomLaunchDirector actual = target.getLaunchDirector();
    assertThat(actual, is(notNullValue()));
  }

}
