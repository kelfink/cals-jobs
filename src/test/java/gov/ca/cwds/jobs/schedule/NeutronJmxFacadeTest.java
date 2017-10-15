package gov.ca.cwds.jobs.schedule;

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
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

public class NeutronJmxFacadeTest {

  Scheduler scheduler;
  NeutronDefaultJobSchedule sched;
  NeutronJmxFacade target;

  @Before
  public void setup() throws Exception {
    // scheduler = new TestScheduler();
    scheduler = mock(Scheduler.class);
    sched = NeutronDefaultJobSchedule.CLIENT;
    target = new NeutronJmxFacade(scheduler, sched);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronJmxFacade.class, notNullValue());
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

  @Test
  public void schedule_Args___T__SchedulerException() throws Exception {
    try {
      when(scheduler.checkExists(any(JobKey.class))).thenThrow(SchedulerException.class);
      when(scheduler.getJobDetail(any(JobKey.class))).thenThrow(SchedulerException.class);
      target.schedule();
      fail("Expected exception was not thrown!");
    } catch (SchedulerException e) {
    }
  }

  @Test
  public void unschedule_Args__() throws Exception {
    target.unschedule();
  }

  @Test
  public void unschedule_Args___T__SchedulerException() throws Exception {
    try {
      doThrow(SchedulerException.class).when(scheduler).pauseTrigger(any(TriggerKey.class));
      target.unschedule();
      fail("Expected exception was not thrown!");
    } catch (SchedulerException e) {
    }
  }

  @Test
  public void status_Args__() throws Exception {
    target.status();
  }

  @Test
  public void status_Args___T__SchedulerException() throws Exception {
    try {
      when(scheduler.getJobDetail(any(JobKey.class))).thenThrow(SchedulerException.class);
      target.status();
      fail("Expected exception was not thrown!");
    } catch (SchedulerException e) {
    }
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

}
