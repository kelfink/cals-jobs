package gov.ca.cwds.neutron.launch.listener;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import gov.ca.cwds.neutron.launch.listener.NeutronJobListener;

public class NeutronJobListenerTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronJobListener.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    NeutronJobListener target = new NeutronJobListener();
    assertThat(target, notNullValue());
  }

  @Test
  public void getName_Args__() throws Exception {
    NeutronJobListener target = new NeutronJobListener();
    String actual = target.getName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void jobToBeExecuted_Args__JobExecutionContext() throws Exception {
    NeutronJobListener target = new NeutronJobListener();
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    target.jobToBeExecuted(context_);
  }

  @Test
  public void jobExecutionVetoed_Args__JobExecutionContext() throws Exception {
    NeutronJobListener target = new NeutronJobListener();
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    target.jobExecutionVetoed(context_);
  }

  @Test
  public void jobWasExecuted_Args__JobExecutionContext__JobExecutionException() throws Exception {
    NeutronJobListener target = new NeutronJobListener();
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    JobExecutionException jobException = mock(JobExecutionException.class);
    target.jobWasExecuted(context_, jobException);
  }

}
