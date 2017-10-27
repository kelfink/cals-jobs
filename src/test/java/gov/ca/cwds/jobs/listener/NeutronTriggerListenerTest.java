package gov.ca.cwds.jobs.listener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;

import gov.ca.cwds.jobs.schedule.JobRunner;
import gov.ca.cwds.jobs.schedule.NeutronDefaultJobSchedule;
import gov.ca.cwds.jobs.schedule.NeutronInterruptableJob;
import gov.ca.cwds.jobs.schedule.NeutronSchedulerConstants;
import gov.ca.cwds.jobs.test.TestIndexerJob;

public class NeutronTriggerListenerTest {

  NeutronTriggerListener target;
  NeutronInterruptableJob job;

  JobExecutionContext context_;
  JobDataMap jobDataMap;
  JobDetail jobDetail;

  TriggerKey triggerKey;
  Trigger trigger;

  @Before
  public void setup() throws Exception {
    target = new NeutronTriggerListener();
    job = new NeutronInterruptableJob();
    context_ = mock(JobExecutionContext.class);
    triggerKey = new TriggerKey("fakejob", NeutronSchedulerConstants.GRP_LST_CHG);
    trigger = mock(Trigger.class);
    jobDataMap = mock(JobDataMap.class);
    jobDetail = mock(JobDetail.class);

    when(context_.getJobInstance()).thenReturn(job);
    when(context_.getJobDetail()).thenReturn(jobDetail);
    when(context_.getTrigger()).thenReturn(trigger);

    when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
    when(trigger.getKey()).thenReturn(triggerKey);
    when(jobDataMap.getString(any(String.class))).thenReturn(TestIndexerJob.class.getName());

    JobRunner.getInstance().scheduleJob(TestIndexerJob.class, NeutronDefaultJobSchedule.CLIENT);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronTriggerListener.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getName_Args__() throws Exception {
    String actual = target.getName();
    String expected = "neutron_trigger_listener";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void triggerFired_Args__Trigger__JobExecutionContext() throws Exception {
    target.triggerFired(trigger, context_);
  }

  @Test
  public void vetoJobExecution_Args__Trigger__JobExecutionContext() throws Exception {
    boolean actual = target.vetoJobExecution(trigger, context_);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void triggerMisfired_Args__Trigger() throws Exception {
    target.triggerMisfired(trigger);
  }

  @Test
  public void triggerComplete_Args__Trigger__JobExecutionContext__CompletedExecutionInstruction()
      throws Exception {
    CompletedExecutionInstruction triggerInstructionCode =
        CompletedExecutionInstruction.SET_TRIGGER_COMPLETE;
    target.triggerComplete(trigger, context_, triggerInstructionCode);
  }

}
