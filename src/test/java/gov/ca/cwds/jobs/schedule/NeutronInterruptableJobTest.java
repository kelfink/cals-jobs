package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Trigger;

import gov.ca.cwds.jobs.component.JobProgressTrack;

public class NeutronInterruptableJobTest {

  NeutronInterruptableJob target;

  @Before
  public void setup() throws Exception {
    target = new NeutronInterruptableJob();
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronInterruptableJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test(expected = JobExecutionException.class)
  public void execute_Args__JobExecutionContext_T__JobExecutionException() throws Exception {
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    JobDetail jd = mock(JobDetail.class);
    Trigger trg = mock(Trigger.class);

    final JobKey jobKey = new JobKey("crap", NeutronSchedulerConstants.GRP_LST_CHG);

    JobDataMap jdm = new JobDataMap();
    jdm.put("job_class", "crap");
    jdm.put("cmd_line", "--invalid");

    when(context_.getJobDetail()).thenReturn(jd);
    when(context_.getTrigger()).thenReturn(trg);
    when(jd.getJobDataMap()).thenReturn(jdm);
    when(trg.getJobKey()).thenReturn(jobKey);

    target.execute(context_);
  }

  @Test
  public void interrupt_Args__() throws Exception {
    target.interrupt();
  }

  @Test
  public void getClassName_Args__() throws Exception {
    String actual = target.getClassName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClassName_Args__String() throws Exception {
    String className = null;
    target.setClassName(className);
  }

  @Test
  public void getCmdLine_Args__() throws Exception {
    String actual = target.getCmdLine();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCmdLine_Args__String() throws Exception {
    String cmdLine = null;
    target.setCmdLine(cmdLine);
  }

  @Test
  public void getTrack_Args__() throws Exception {
    JobProgressTrack actual = target.getTrack();
    JobProgressTrack expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTrack_Args__JobProgressTrack() throws Exception {
    JobProgressTrack track = mock(JobProgressTrack.class);
    target.setTrack(track);
  }

}
