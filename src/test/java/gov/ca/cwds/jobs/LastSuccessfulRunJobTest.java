package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.component.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class LastSuccessfulRunJobTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  private static class TestLastSuccessfulRunJob extends LastSuccessfulRunJob {

    ElasticsearchDao esDao;

    public TestLastSuccessfulRunJob(String lastJobRunTimeFilename, ElasticsearchDao esDao) {
      super(lastJobRunTimeFilename);
    }

    @Override
    public Date _run(Date lastSuccessfulRunTime) {
      return null;
    }

    @Override
    protected void finish() {}

    @Override
    public JobProgressTrack getTrack() {
      return null;
    }

    @Override
    public ElasticsearchDao getEsDao() {
      return esDao;
    }

  }

  private static final String FIXED_DATETIME = "2017-06-14 14:09:47";

  TestNormalizedEntityDao dao;
  LastSuccessfulRunJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new TestLastSuccessfulRunJob(lastJobRunTimeFilename, esDao);

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
    final Date expected = new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
        .parse(FIXED_DATETIME);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void writeLastSuccessfulRunTime_Args__Date() throws Exception {
    final Date datetime = new Date();
    target.writeLastSuccessfulRunTime(datetime);
  }

  @Test
  public void getLastJobRunTimeFilename_Args__() throws Exception {
    final String actual = target.getLastJobRunTimeFilename();
    final String expected = tempFile.getAbsolutePath();
    assertThat(actual, is(equalTo(expected)));
  }

}
