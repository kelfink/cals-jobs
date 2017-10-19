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
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.component.JobProgressTrack;

public class NeutronJobFacadeTest {

  Scheduler scheduler;
  NeutronDefaultJobSchedule sched;
  NeutronJobFacade target;

  @Before
  public void setup() throws Exception {
    scheduler = mock(Scheduler.class);
    sched = NeutronDefaultJobSchedule.CLIENT;
    target = new NeutronJobFacade(scheduler, sched);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronJobFacade.class, notNullValue());
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
    JobDetail jd = mock(JobDetail.class);
    when(scheduler.getJobDetail(any(JobKey.class))).thenReturn(jd);

    JobDataMap jdm = new JobDataMap();
    jdm.put("job_class", "TestNeutronJob");
    jdm.put("cmd_line", "--invalid");

    final JobProgressTrack track = new JobProgressTrack();
    jdm.put("track", track);

    when(jd.getJobDataMap()).thenReturn(jdm);

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
