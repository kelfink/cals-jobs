package gov.ca.cwds.jobs.schedule;

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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Trigger;

import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.component.Rocket;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class NeutronRocketTest extends Goddard {

  NeutronRocket target;
  TestNormalizedEntityDao dao;
  TestIndexerJob rocket;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new TestNormalizedEntityDao(sessionFactory);
    rocket = new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory,
        flightRecorder);
    rocket.setFlightPlan(flightPlan);
    rocket.init(this.tempFile.getAbsolutePath(), flightPlan);

    rocket.setTrack(flightRecord);
    target = new NeutronRocket(rocket);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronRocket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test(expected = JobExecutionException.class)
  public void execute_Args__JobExecutionContext_T__JobException() throws Exception {
    final JobExecutionContext context_ = mock(JobExecutionContext.class);
    final JobDetail jd = mock(JobDetail.class);
    final Trigger trg = mock(Trigger.class);

    final JobKey jobKey = new JobKey("crap", NeutronSchedulerConstants.GRP_LST_CHG);
    final JobDataMap jdm = new JobDataMap();
    jdm.put("job_class", "crap");
    jdm.put("cmd_line", "--invalid");

    when(context_.getJobDetail()).thenReturn(jd);
    when(context_.getTrigger()).thenReturn(trg);
    when(jd.getJobDataMap()).thenReturn(jdm);
    when(trg.getJobKey()).thenReturn(jobKey);

    doThrow(new IllegalArgumentException("uh oh")).when(context_).setResult(any(Object.class));
    target.execute(context_);
  }

  @Test
  public void execute__blow() throws Exception {
    final JobExecutionContext context_ = mock(JobExecutionContext.class);
    final JobDetail jd = mock(JobDetail.class);
    final Trigger trg = mock(Trigger.class);

    final JobKey jobKey = new JobKey("crap", NeutronSchedulerConstants.GRP_LST_CHG);
    final JobDataMap jdm = new JobDataMap();
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
  public void getTrack_Args__() throws Exception {
    FlightRecord actual = target.getTrack();
    FlightRecord expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTrack_Args__JobProgressTrack() throws Exception {
    FlightRecord track = mock(FlightRecord.class);
    target.setTrack(track);
  }

  @Test
  public void getRocket() throws Exception {
    Rocket actual = target.getRocket();
    assertThat(actual, is(notNullValue()));
  }

}
