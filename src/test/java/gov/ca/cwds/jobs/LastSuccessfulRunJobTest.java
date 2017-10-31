package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class LastSuccessfulRunJobTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  private static class TestLastSuccessfulRunJob extends LastSuccessfulRunJob {

    ElasticsearchDao esDao;

    FlightRecord track = new FlightRecord();

    public TestLastSuccessfulRunJob(String lastJobRunTimeFilename, ElasticsearchDao esDao,
        FlightRecorder jobHistory) {
      super(lastJobRunTimeFilename, jobHistory);
    }

    @Override
    public Date executeJob(Date lastSuccessfulRunTime) {
      return null;
    }

    @Override
    protected void finish() {}

    @Override
    public FlightRecord getTrack() {
      return track;
    }

    @Override
    public ElasticsearchDao getEsDao() {
      return esDao;
    }

  }

  private static final String FIXED_DATETIME = "2017-06-14 14:09:47";

  TestNormalizedEntityDao dao;
  TestLastSuccessfulRunJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new TestLastSuccessfulRunJob(lastJobRunTimeFilename, esDao, jobHistory);

    try (BufferedWriter w = new BufferedWriter(new FileWriter(tempFile))) {
      w.write(FIXED_DATETIME);
    } catch (IOException e) {
    }
  }

  @Test
  public void type() throws Exception {
    assertThat(LastSuccessfulRunJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void run_Args__() throws Exception {
    target.run();
  }

  @Test
  public void determineLastSuccessfulRunTime_Args__() throws Exception {
    final Date actual = target.determineLastSuccessfulRunTime();
    final Date expected =
        NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.formatter().parse(FIXED_DATETIME);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test(expected = JobsException.class)
  public void determineLastSuccessfulRunTime_error__() throws Exception {
    target.setLastRunTimeFilename("zugzug_oompa_loompa");
    final Date actual = target.determineLastSuccessfulRunTime();
    final Date expected =
        NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.formatter().parse(FIXED_DATETIME);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void writeLastSuccessfulRunTime_Args__Date() throws Exception {
    FlightRecord track = mock(FlightRecord.class);
    when(track.isFailed()).thenReturn(false);
    target.track = track;

    target.setLastRunTimeFilename(tempFile.getAbsolutePath());

    final Date datetime = new Date();
    target.writeLastSuccessfulRunTime(datetime);
  }

  @Test(expected = JobsException.class)
  public void writeLastSuccessfulRunTime_Args__bomb() throws Exception {
    FlightRecord track = mock(FlightRecord.class);
    when(track.isFailed()).thenReturn(false);
    target.track = track;

    target.setLastRunTimeFilename("/does/not/exist/garbage");

    final Date datetime = new Date();
    target.writeLastSuccessfulRunTime(datetime);
  }

  @Test
  public void getLastJobRunTimeFilename_Args__() throws Exception {
    final String actual = target.getLastJobRunTimeFilename();
    final String expected = tempFile.getAbsolutePath();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void done_Args__() throws Exception {
    target.done();
  }

  @Test
  public void fail_Args__() throws Exception {
    target.fail();
  }

  @Test
  public void doneRetrieve_Args__() throws Exception {
    target.doneRetrieve();
  }

  @Test
  public void doneTransform_Args__() throws Exception {
    target.doneTransform();
  }

  @Test
  public void doneIndex_Args__() throws Exception {
    target.doneIndex();
  }

  @Test
  public void isRunning_Args__() throws Exception {
    boolean actual = target.isRunning();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isFailed_Args__() throws Exception {
    boolean actual = target.isFailed();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isRetrieveDone_Args__() throws Exception {
    boolean actual = target.isRetrieveDone();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isTransformDone_Args__() throws Exception {
    boolean actual = target.isTransformDone();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isIndexDone_Args__() throws Exception {
    boolean actual = target.isIndexDone();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void calcLastRunDate_Args__Date__JobOptions() throws Exception {
    Date lastSuccessfulRunTime = new Date();
    Date actual = target.calcLastRunDate(lastSuccessfulRunTime, opts);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void calcLastRunDate_Args__Date() throws Exception {
    Date lastSuccessfulRunTime = new Date();

    JobOptions opts = new JobOptions();
    opts.setLastRunTime(lastSuccessfulRunTime);
    target.setOpts(opts);

    final Date actual = target.calcLastRunDate(lastSuccessfulRunTime);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    JobOptions opts = new JobOptions();
    target.setOpts(opts);
    JobOptions actual = target.getOpts();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void getLogger_Args__() throws Exception {
    Logger actual = target.getLogger();
    assertThat(actual, is(notNullValue()));
  }

}
