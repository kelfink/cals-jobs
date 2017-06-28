package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class LastSuccessfulRunJobTest {

  private static class TestLastSuccessfulRunJob extends LastSuccessfulRunJob {

    public TestLastSuccessfulRunJob(String lastJobRunTimeFilename) {
      super(lastJobRunTimeFilename);
    }

    @Override
    public Date _run(Date lastSuccessfulRunTime) {
      return null;
    }

    @Override
    protected void finish() {}

  }

  private static final String FIXED_DATETIME = "2017-06-14 14:09:47";

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  SessionFactory sessionFactory;
  TestNormalizedEntityDao dao;
  ElasticsearchDao esDao;
  File tempFile;
  String lastJobRunTimeFilename;
  ObjectMapper mapper = ElasticSearchPerson.MAPPER;
  LastSuccessfulRunJob target;

  @Before
  public void setup() throws Exception {
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();
    target = new TestLastSuccessfulRunJob(lastJobRunTimeFilename);

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
    Date actual = target.determineLastSuccessfulRunTime();
    Date expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(FIXED_DATETIME);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void writeLastSuccessfulRunTime_Args__Date() throws Exception {
    Date datetime = mock(Date.class);
    target.writeLastSuccessfulRunTime(datetime);
  }

  @Test
  public void getLastJobRunTimeFilename_Args__() throws Exception {
    String actual = target.getLastJobRunTimeFilename();
    String expected = tempFile.getAbsolutePath();
    assertThat(actual, is(equalTo(expected)));
  }

}
