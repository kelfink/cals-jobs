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
import org.quartz.JobKey;
import org.quartz.Trigger;

import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class NeutronRocketTest extends PersonJobTester {

  NeutronRocket target;
  TestNormalizedEntityDao dao;
  TestIndexerJob rocket;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new TestNormalizedEntityDao(sessionFactory);
    rocket =
        new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory, flightRecorder);
    rocket.setOpts(opts);
    rocket.init(this.tempFile.getAbsolutePath(), opts);

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

  @Test
  public void execute_Args__JobExecutionContext_T__JobException() throws Exception {
    JobExecutionContext context_ = mock(JobExecutionContext.class);
    JobDetail jd = mock(JobDetail.class);
    Trigger trg = mock(Trigger.class);

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

}
