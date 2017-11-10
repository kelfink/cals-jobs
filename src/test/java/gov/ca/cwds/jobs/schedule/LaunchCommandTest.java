package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.test.Mach1TestRocket;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.atom.AtomLaunchScheduler;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;

public class LaunchCommandTest extends Goddard<TestNormalizedEntity, TestDenormalizedEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaunchCommandTest.class);

  LaunchCommand target;
  TriggerKey key;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    flightPlan.setEsConfigLoc("config/local.yaml");
    flightPlan.setBaseDirectory("/var/lib/jenkins/");
    flightPlan.setLastRunLoc(lastJobRunTimeFilename);
    target = new LaunchCommand(flightRecorder, launchScheduler, esDao);
    target.setStartingOpts(flightPlan);

    key = new TriggerKey("el_trigger", NeutronSchedulerConstants.GRP_LST_CHG);
    LaunchCommand.setTestMode(true);
  }

  @Test
  public void type() throws Exception {
    assertThat(LaunchCommand.class, notNullValue());
  }

  @Test
  public void resetTimestamps_Args__boolean__int() throws Exception {
    boolean initialMode = false;
    int hoursInPast = 0;
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

  @Test
  public void isSchedulerMode_Args__() throws Exception {
    boolean actual = LaunchCommand.isSchedulerMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    boolean actual = LaunchCommand.isTestMode();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    boolean mode = false;
    LaunchCommand.setTestMode(mode);
  }

  @Test
  public void isInitialMode_Args__() throws Exception {
    boolean actual = LaunchCommand.isInitialMode();
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

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void startScheduler_Args__() throws Exception {
    target.startScheduler();
  }

  @Test
  public void handleTimeFile_Args__FlightPlan__DateFormat__Date__DefaultFlightSchedule()
      throws Exception {
    final DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    final Date now = new Date();
    final StandardFlightSchedule sched = StandardFlightSchedule.CLIENT;
    target.handleTimeFile(flightPlan, fmt, now, sched);
  }

  @Test
  public void configureInitialMode_Args__Date() throws Exception {
    final Date now = new Date();
    target.configureInitialMode(now);
  }

  @Test
  public void initScheduler_Args__Injector() throws Exception {
    final Injector injector = mock(Injector.class);
    when(injector.getInstance(RocketFactory.class)).thenReturn(rocketFactory);
    target.getSettings().setExposeJmx(false);
    target.setInjector(injector);
    target.initScheduler();
  }

  @Test
  public void getStartingOpts_Args__() throws Exception {
    final FlightPlan actual = target.getStartingOpts();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setStartingOpts_Args__FlightPlan() throws Exception {
    target.setStartingOpts(flightPlan);
  }

  @Test
  public void getFlightRecorder_Args__() throws Exception {
    AtomFlightRecorder actual = target.getFlightRecorder();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setFlightRecorder_Args__FlightRecorder() throws Exception {
    target.setFlightRecorder(flightRecorder);
  }

  @Test
  public void getNeutronScheduler_Args__() throws Exception {
    AtomLaunchScheduler actual = target.getNeutronScheduler();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setNeutronScheduler_Args__LaunchScheduler() throws Exception {
    target.setLaunchScheduler(launchScheduler);
  }

  @Test
  public void getScheduler_Args__() throws Exception {
    final Scheduler actual = target.getScheduler();
  }

  @Test
  public void addExecutingJob_Args__TriggerKey__NeutronRocket() throws Exception {
    final NeutronRocket job = mock(NeutronRocket.class);
    target.trackInFlightRocket(key, job);
  }

  @Test
  public void getInstance_Args__() throws Exception {
    LaunchCommand actual = LaunchCommand.getInstance();
    assertThat(actual, is(notNullValue()));
  }

  // @Test
  // public void startContinuousMode_Args__StringArray() throws Exception {
  // LaunchCommand actual = LaunchCommand.startSchedulerMode();
  // assertThat(actual, is(notNullValue()));
  // }

  @Test
  public void runStandalone_Args__Class__StringArray() throws Exception {
    final Class<Mach1TestRocket> klass = Mach1TestRocket.class;
    final String[] args = new String[] {"-S", "-l", "/var/lib/jenkins/SuperRocket/LastRuntime.time",
        "-c", "config/local.yaml"};
    LaunchCommand.runStandalone(klass, args);
  }

  @Test
  public void main_Args__StringArray() throws Exception {
    final String[] args = new String[] {"-S", "-b", "/var/lib/jenkins", "-c", "config/local.yaml"};
    LaunchCommand.main(args);
  }

  // @Test
  // @Ignore
  // public void createJob_Args__Class__StringArray() throws Exception {
  // Class<?> klass = TestIndexerJob.class;
  // String[] args = new String[] {"-c", "config/local.yaml", "-b", "/var/lib/jenkins/", "-F"}
  // BasePersonIndexerJob actual = target.createJob(klass, args);
  // assertThat(actual, is(notNullValue()));
  // }

}
