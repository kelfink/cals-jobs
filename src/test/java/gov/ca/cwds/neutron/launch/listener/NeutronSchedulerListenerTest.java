package gov.ca.cwds.neutron.launch.listener;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.launch.listener.NeutronSchedulerListener;

public class NeutronSchedulerListenerTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronSchedulerListener.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    assertThat(target, notNullValue());
  }

  @Test
  public void jobScheduled_Args__Trigger() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    Trigger trigger = mock(Trigger.class);
    target.jobScheduled(trigger);
  }

  @Test
  public void jobUnscheduled_Args__TriggerKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    TriggerKey triggerKey = new TriggerKey("el_trigger", NeutronSchedulerConstants.GRP_LST_CHG);
    target.jobUnscheduled(triggerKey);
  }

  @Test
  public void triggerFinalized_Args__Trigger() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    Trigger trigger = mock(Trigger.class);
    target.triggerFinalized(trigger);
  }

  @Test
  public void triggerPaused_Args__TriggerKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    TriggerKey triggerKey = new TriggerKey("el_trigger", NeutronSchedulerConstants.GRP_LST_CHG);
    target.triggerPaused(triggerKey);
  }

  @Test
  public void triggersPaused_Args__String() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    String triggerGroup = null;
    target.triggersPaused(triggerGroup);
  }

  @Test
  public void triggerResumed_Args__TriggerKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    TriggerKey triggerKey = new TriggerKey("el_trigger", NeutronSchedulerConstants.GRP_LST_CHG);
    target.triggerResumed(triggerKey);
  }

  @Test
  public void triggersResumed_Args__String() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    String triggerGroup = null;
    target.triggersResumed(triggerGroup);
  }

  @Test
  public void jobAdded_Args__JobDetail() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    JobDetail jobDetail = mock(JobDetail.class);
    target.jobAdded(jobDetail);
  }

  @Test
  public void jobDeleted_Args__JobKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    final JobKey jobKey = new JobKey("el_job", NeutronSchedulerConstants.GRP_LST_CHG);
    target.jobDeleted(jobKey);
  }

  @Test
  public void jobPaused_Args__JobKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    JobKey jobKey = new JobKey("el_job", NeutronSchedulerConstants.GRP_LST_CHG);
    target.jobPaused(jobKey);
  }

  @Test
  public void jobsPaused_Args__String() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    String jobGroup = null;
    target.jobsPaused(jobGroup);
  }

  @Test
  public void jobResumed_Args__JobKey() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    JobKey jobKey = new JobKey("el_job", NeutronSchedulerConstants.GRP_LST_CHG);
    target.jobResumed(jobKey);
  }

  @Test
  public void jobsResumed_Args__String() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    String jobGroup = null;
    target.jobsResumed(jobGroup);
  }

  @Test
  public void schedulerError_Args__String__SchedulerException() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    String msg = null;
    SchedulerException cause = mock(SchedulerException.class);
    target.schedulerError(msg, cause);
  }

  @Test
  public void schedulerInStandbyMode_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulerInStandbyMode();
  }

  @Test
  public void schedulerStarted_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulerStarted();
  }

  @Test
  public void schedulerStarting_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulerStarting();
  }

  @Test
  public void schedulerShutdown_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulerShutdown();
  }

  @Test
  public void schedulerShuttingdown_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulerShuttingdown();
  }

  @Test
  public void schedulingDataCleared_Args__() throws Exception {
    NeutronSchedulerListener target = new NeutronSchedulerListener();
    target.schedulingDataCleared();
  }

}
