package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.Scheduler;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.JobProgressTrack;
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
    opts.setBaseDirectory("/var/lib/jenkins/");
    opts.setLastRunLoc(lastJobRunTimeFilename);

    target = JobRunner.getInstance();
    target.setStartingOpts(opts);
    target.setEsDao(esDao);
    JobRunner.setTestMode(true);

    Scheduler scheduler = mock(Scheduler.class);
    target.setScheduler(scheduler);
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

  @Test(expected = IOException.class)
  @Ignore
  public void resetTimestamps_Args__boolean__int_T__IOException() throws Exception {
    boolean initialMode = false;
    int hoursInPast = 0;
    opts.setLastRunLoc(".././.././aintthere");
    target.resetTimestamps(initialMode, hoursInPast);
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
  public void stopScheduler_Args__boolean() throws Exception {
    boolean waitForJobsToComplete = false;
    target.stopScheduler(waitForJobsToComplete);
  }

  @Test(expected = NeutronException.class)
  @Ignore
  public void stopScheduler_Args__boolean_T__NeutronException() throws Exception {
    boolean waitForJobsToComplete = false;
    target.stopScheduler(waitForJobsToComplete);
  }

  @Test
  @Ignore
  public void startScheduler_Args__() throws Exception {
    target.startScheduler();
  }

  @Test(expected = NeutronException.class)
  @Ignore
  public void startScheduler_Args___T__NeutronException() throws Exception {
    target.startScheduler();
  }

  @Test
  @Ignore
  public void initScheduler_Args__() throws Exception {
    target.initScheduler();
  }

  @Test(expected = NeutronException.class)
  @Ignore
  public void initScheduler_Args___T__NeutronException() throws Exception {
    target.initScheduler();
  }

  @Test
  @Ignore
  public void registerJob_Args__Class__JobOptions() throws Exception {
    target.registerJob(TestIndexerJob.class, opts);
  }

  @Test(expected = NeutronException.class)
  @Ignore
  public void registerJob_Args__Class__JobOptions_T__NeutronException() throws Exception {
    target.registerJob(TestIndexerJob.class, opts);
  }

  @Test
  @Ignore
  public void createJob_Args__Class__StringArray() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"};
    BasePersonIndexerJob actual = target.createJob(klass, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = NeutronException.class)
  public void createJob_Args__Class__StringArray_T__NeutronException() throws Exception {
    final Class<?> klass = TestIndexerJob.class;
    final String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"};
    target.createJob(klass, args);
  }

  @Test
  @Ignore
  public void createJob_Args__String__StringArray() throws Exception {
    final String jobName = TestIndexerJob.class.getName();
    final String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"};
    final BasePersonIndexerJob actual = target.createJob(jobName, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  @Ignore
  public void runScheduledJob_Args__Class__StringArray() throws Exception {
    final Class<?> klass = TestIndexerJob.class;
    final String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"};
    final JobProgressTrack actual = target.runScheduledJob(klass, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = NeutronException.class)
  public void runScheduledJob_Args__Class__StringArray_T__NeutronException() throws Exception {
    Class<?> klass = TestIndexerJob.class;
    String[] args = new String[] {};
    target.runScheduledJob(klass, args);
  }

  @Test
  @Ignore
  public void runScheduledJob_Args__String__StringArray() throws Exception {
    final String jobName = TestIndexerJob.class.getName();
    final String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"};
    JobProgressTrack actual = target.runScheduledJob(jobName, args);
    assertThat(actual, is(notNullValue()));
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
  @Ignore
  public void runStandalone_Args__Class__StringArray() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/"};
    target.runStandalone(TestIndexerJob.class, args);
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
