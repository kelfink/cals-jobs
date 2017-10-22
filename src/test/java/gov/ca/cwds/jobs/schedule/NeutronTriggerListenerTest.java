package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

public class NeutronTriggerListenerTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronTriggerListener.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    assertThat(target, notNullValue());
  }

  @Test
  public void getName_Args__() throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    String actual = target.getName();
    String expected = "neutron_trigger_listener";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void triggerFired_Args__Trigger__JobExecutionContext() throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    Trigger trigger = mock(Trigger.class);
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    target.triggerFired(trigger, context_);
  }

  @Test
  public void vetoJobExecution_Args__Trigger__JobExecutionContext() throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    Trigger trigger = mock(Trigger.class);
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    boolean actual = target.vetoJobExecution(trigger, context_);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void triggerMisfired_Args__Trigger() throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    Trigger trigger = mock(Trigger.class);
    target.triggerMisfired(trigger);
  }

  @Test
  public void triggerComplete_Args__Trigger__JobExecutionContext__CompletedExecutionInstruction()
      throws Exception {
    NeutronTriggerListener target = new NeutronTriggerListener();
    Trigger trigger = mock(Trigger.class);
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    CompletedExecutionInstruction triggerInstructionCode =
        CompletedExecutionInstruction.SET_TRIGGER_COMPLETE;
    target.triggerComplete(trigger, context_, triggerInstructionCode);
  }

}
