package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.ClientIndexerJob;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;

public class LaunchPadTest extends Goddard {
  Scheduler scheduler;
  DefaultFlightSchedule sched;
  FlightRecorder history;
  LaunchPad target;

  @Override
  @Before
  public void setup() throws Exception {
    scheduler = mock(Scheduler.class);
    sched = DefaultFlightSchedule.CLIENT;
    history = new FlightRecorder();
    target = new LaunchPad(scheduler, sched, history, flightPlan);
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

  @Test(expected = SchedulerException.class)
  public void schedule_Args___T__SchedulerException() throws Exception {
    when(scheduler.checkExists(any(JobKey.class))).thenThrow(SchedulerException.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenThrow(SchedulerException.class);
    target.schedule();
  }

  @Test
  public void unschedule_Args__() throws Exception {
    target.unschedule();
  }

  @Test(expected = SchedulerException.class)
  public void unschedule_Args___T__SchedulerException() throws Exception {
    doThrow(SchedulerException.class).when(scheduler).pauseTrigger(any(TriggerKey.class));
    target.unschedule();
  }

  @Test
  public void status_Args__() throws Exception {
    JobDetail jd = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(jd);
    JobDataMap jdm = new JobDataMap();
    jdm.put("job_class", "TestNeutronJob");
    jdm.put("cmd_line", "--invalid");
    final FlightRecord track = new FlightRecord();
    jdm.put("track", track);
    when(jd.getJobDataMap()).thenReturn(jdm);
    history.addTrack(ClientIndexerJob.class, track);
    target.status();
  }

  @Test
  public void stop_Args__() throws Exception {
    target.stop();
  }

  @Test
  public void stop_Args___T__SchedulerException() throws Exception {
    try {
      doThrow(SchedulerException.class).when(scheduler).pauseTrigger(any(TriggerKey.class));
      target.stop();
      fail("Expected exception was not thrown!");
    } catch (SchedulerException e) {
    }
  }

  @Test
  public void vetoScheduledJob_Args__() throws Exception {
    target.vetoScheduledJob();
  }

  @Test
  public void history_Args__() throws Exception {
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
    JobDetail actual = target.getJd();
    JobDetail expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    FlightPlan actual = target.getOpts();
    FlightPlan expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOpts_Args__FlightPlan() throws Exception {
    FlightPlan opts_ = mock(FlightPlan.class);
    target.setOpts(opts_);
  }

}
