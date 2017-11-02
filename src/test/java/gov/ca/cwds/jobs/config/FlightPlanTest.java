package gov.ca.cwds.jobs.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.commons.cli.Option;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.exception.NeutronException;

public class FlightPlanTest {

  FlightPlan target;

  @Before
  public void setup() throws Exception {
    target = makeGeneric();
  }

  public static final FlightPlan makeGeneric() {
    return new FlightPlan("config/local.yaml", null, null, null, false, 1, 5, 1, true, false, null,
        false, false);
  }

  @Test
  public void type() throws Exception {
    assertThat(FlightPlan.class, notNullValue());
  }

  @Test
  public void getEsConfigLoc_Args__() throws Exception {
    String actual = target.getEsConfigLoc();
    String expected = "config/local.yaml";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastRunLoc_Args__() throws Exception {
    String actual = target.getLastRunLoc();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastRunTime_Args__() throws Exception {
    Date actual = target.getLastRunTime();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getIndexName_Args__() throws Exception {
    String actual = target.getIndexName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getStartBucket_Args__() throws Exception {
    long actual = target.getStartBucket();
    long expected = 1L;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getEndBucket_Args__() throws Exception {
    long actual = target.getEndBucket();
    long expected = 5L;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getThreadCount_Args__() throws Exception {
    long actual = target.getThreadCount();
    long expected = 1L;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void makeOpt_Args__String__String__String() throws Exception {
  // String shortOpt = null;
  // String longOpt = null;
  // String description = null;
  //
  // Option actual = JobOptions.makeOpt(shortOpt, longOpt, description);
  // Option expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }

  // @Test
  // public void makeOpt_Args__String__String__String__boolean__int__Class__char() throws Exception
  // {
  // String shortOpt = null;
  // String longOpt = null;
  // String description = null;
  // boolean required = false;
  // int argc = 0;
  // Class<?> type = mock(Class.class);
  // char sep = ' ';
  //
  // Option actual = JobOptions.makeOpt(shortOpt, longOpt, description, required, argc, type, sep);
  // Option expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void printUsage_Args__() throws Exception {
    FlightPlan.printUsage();
  }

  @Test(expected = NeutronException.class)
  public void parseCommandLine_Args__T__no_args() throws Exception {
    String[] args = new String[] {"--invalid"};
    FlightPlan actual = FlightPlan.parseCommandLine(args);
  }

  @Test
  public void parseCommandLine_Args__1() throws Exception {
    String[] args = new String[] {"-c", "config/local.yaml", "-l",
        "/Users/CWS-NS3/client_indexer_time.txt", "-t", "4", "-x", "99"};
    FlightPlan.parseCommandLine(args);
  }

  @Test
  public void parseCommandLine_Args__2() throws Exception {
    String[] args = new String[] {"-c", "config/local.yaml", "-b", "3", "-m", "4", "-r", "20-24",
        "-t", "4", "-x", "99", "-a", "2010-01-01 00:00:00", "-i", "my-index"};
    FlightPlan.parseCommandLine(args);
  }

  @Test(expected = NeutronException.class)
  public void parseCommandLine_Args__3() throws Exception {
    String[] args = new String[] {"-c", "config/local.yaml", "-b", "3", "-m", "4", "-r", "20-24",
        "-t", "4", "-x", "99", "-a", "2010-01-01 00:00:gg", "-i", "my-index"};
    FlightPlan.parseCommandLine(args);
  }

  @Test(expected = NeutronException.class)
  public void parseCommandLine_Args__4() throws Exception {
    String[] args = new String[] {"-c", "config/local.yaml", "-b", "g", "-m", "4", "-r", "20-24",
        "-t", "4", "-x", "99", "-a", "2010-01-01 00:00:gg", "-i", "my-index"};
    FlightPlan.parseCommandLine(args);
  }

  @Test
  public void instantiation() throws Exception {
    String esConfigLoc = null;
    String lastRunLoc = null;
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;
    String minId = null;
    String maxId = null;
    FlightPlan target = new FlightPlan(esConfigLoc, null, null, lastRunLoc, lastRunMode,
        startBucket, endBucket, threadCount, true, false, null, false, false);
    assertThat(target, notNullValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void makeOpt_Args__String__String__String__all_null_args() throws Exception {
    String shortOpt = null;
    String longOpt = null;
    String description = null;
    Option actual = FlightPlan.makeOpt(shortOpt, longOpt, description);
    Option expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartBucket_Args__long() throws Exception {
    String esConfigLoc = null;
    String lastRunLoc = null;
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;
    String minId = null;
    String maxId = null;
    FlightPlan target = new FlightPlan(esConfigLoc, null, null, lastRunLoc, lastRunMode,
        startBucket, endBucket, threadCount, true, false, null, false, false);
    long startBucket_ = 0L;
    target.setStartBucket(startBucket_);
  }

  @Test
  public void setEndBucket_Args__long() throws Exception {
    String esConfigLoc = null;
    String lastRunLoc = null;
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;
    String minId = null;
    String maxId = null;
    FlightPlan target = new FlightPlan(esConfigLoc, null, null, lastRunLoc, lastRunMode,
        startBucket, endBucket, threadCount, true, false, null, false, false);
    long endBucket_ = 0L;
    target.setEndBucket(endBucket_);
  }

  @Test
  public void setThreadCount_Args__long() throws Exception {
    String esConfigLoc = null;
    String lastRunLoc = null;
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;
    String minId = null;
    String maxId = null;
    FlightPlan target = new FlightPlan(esConfigLoc, null, null, lastRunLoc, lastRunMode,
        startBucket, endBucket, threadCount, true, false, null, false, false);
    long threadCount_ = 0L;
    target.setThreadCount(threadCount_);
  }

}
