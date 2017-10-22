package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;

public class JobRunnerTest extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  JobRunner target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    opts.setEsConfigLoc("config/local.yaml");
    target = JobRunner.getInstance();
    target.setStartingOpts(opts);
    target.setEsDao(esDao);
  }

  @Test
  public void type() throws Exception {
    assertThat(JobRunner.class, notNullValue());
  }

  @Test
  public void resetTimestamps_Args__boolean__int() throws Exception {
    boolean initialMode = false;
    int hoursInPast = 0;
    target.resetTimestamps(initialMode, hoursInPast);
  }

  @Test
  public void resetTimestamps_Args__boolean__int_T__IOException() throws Exception {
    boolean initialMode = false;
    int hoursInPast = 0;
    try {
      target.resetTimestamps(initialMode, hoursInPast);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void resetTimestampsForInitialLoad_Args__() throws Exception {
    String actual = target.resetTimestampsForInitialLoad();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void resetTimestampsForLastChange_Args__int() throws Exception {
    int hoursInPast = 0;
    target.resetTimestampsForLastChange(hoursInPast);
  }

  @Test
  public void resetTimestampsForLastChange_Args__int_T__IOException() throws Exception {
    int hoursInPast = 0;
    try {
      target.resetTimestampsForLastChange(hoursInPast);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void stopScheduler_Args__boolean() throws Exception {
    boolean waitForJobsToComplete = false;
    target.stopScheduler(waitForJobsToComplete);
  }

  @Test
  @Ignore
  public void stopScheduler_Args__boolean_T__NeutronException() throws Exception {
    boolean waitForJobsToComplete = false;
    try {
      target.stopScheduler(waitForJobsToComplete);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void startScheduler_Args__() throws Exception {
    target.startScheduler();
  }

  @Test
  public void startScheduler_Args___T__NeutronException() throws Exception {
    try {
      target.startScheduler();
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void initScheduler_Args__() throws Exception {
    target.initScheduler();
  }

  @Test
  public void initScheduler_Args___T__NeutronException() throws Exception {
    try {
      target.initScheduler();
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void registerJob_Args__Class__JobOptions() throws Exception {
    JobRunner target = JobRunner.getInstance();
    JobOptions opts = new JobOptions();
    target.registerJob(TestIndexerJob.class, opts);
  }

  @Test
  @Ignore
  public void registerJob_Args__Class__JobOptions_T__NeutronException() throws Exception {
    try {
      target.registerJob(TestIndexerJob.class, opts);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void createJob_Args__Class__StringArray() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {};
    BasePersonIndexerJob actual = target.createJob(klass, args);
    BasePersonIndexerJob expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void createJob_Args__Class__StringArray_T__NeutronException() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {};
    try {
      target.createJob(klass, args);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void createJob_Args__String__StringArray() throws Exception {
    String jobName = "";
    String[] args = new String[] {};
    BasePersonIndexerJob actual = target.createJob(jobName, args);
    BasePersonIndexerJob expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void runScheduledJob_Args__Class__StringArray() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {};
    JobProgressTrack actual = target.runScheduledJob(klass, args);
    JobProgressTrack expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void runScheduledJob_Args__Class__StringArray_T__NeutronException() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {};
    try {
      target.runScheduledJob(klass, args);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  @Test
  public void runScheduledJob_Args__String__StringArray() throws Exception {
    String jobName = TestIndexerJob.class.getName();
    String[] args = new String[] {};
    JobProgressTrack actual = target.runScheduledJob(jobName, args);
    JobProgressTrack expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isSchedulerMode_Args__() throws Exception {
    boolean actual = JobRunner.isSchedulerMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    boolean actual = JobRunner.isTestMode();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    boolean mode = false;
    JobRunner.setTestMode(mode);
  }

  @Test
  public void runStandalone_Args__Class__StringArray() throws Exception {
    String[] args = new String[] {};
    JobRunner.runStandalone(TestIndexerJob.class, args);
  }

  @Test
  public void getInstance_Args__() throws Exception {
    JobRunner actual = JobRunner.getInstance();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isInitialMode_Args__() throws Exception {
    boolean actual = JobRunner.isInitialMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getEsDao_Args__() throws Exception {
    ElasticsearchDao actual = target.getEsDao();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setEsDao_Args__ElasticsearchDao() throws Exception {
    target.setEsDao(esDao);
  }

  // @Test
  // public void main_Args__StringArray() throws Exception {
  // String[] args = new String[] {};
  // JobRunner.main(args);
  // }

}
