package gov.ca.cwds.neutron.jetpack;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.exception.NeutronException;

public class JobLogsTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobLogsTest.class);

  @Test
  public void type() throws Exception {
    assertThat(JobLogs.class, notNullValue());
  }

  @Test
  public void logEvery_Args__Logger__int__String__StringArray() throws Exception {
    Logger log = mock(Logger.class);
    int cntr = 0;
    String action = null;
    Object[] args = new String[] {};
    JobLogs.logEvery(log, cntr, action, args);
  }

  @Test
  public void logEvery_Args__int__String__StringArray() throws Exception {
    int cntr = 0;
    String action = null;
    Object[] args = new String[] {};
    JobLogs.logEvery(cntr, action, args);
  }

  @Test(expected = JobsException.class)
  public void throwFatalError_Args__Logger__Throwable__String__ObjectArray4() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = new IllegalStateException("hello world");
    String pattern = null;
    Object[] args = new Object[] {};
    throw JobLogs.buildRuntimeException(log, e, pattern, args);
  }

  @Test(expected = JobsException.class)
  public void throwFatalError_Args__Logger__Throwable__String() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = new IllegalStateException("error message");
    String message = "hello world";
    throw JobLogs.buildRuntimeException(log, e, message);
  }

  @Test(expected = JobsException.class)
  public void logEvery_Args__Logger__int__String__ObjectArray1() throws Exception {
    Exception e = new Exception();
    throw JobLogs.buildRuntimeException(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
  }

  @Test
  public void logEvery_Args__int__String__ObjectArray() throws Exception {
    int cntr = 0;
    String action = null;
    Object[] args = new Object[] {};
    JobLogs.logEvery(cntr, action, args);
  }

  @Test(expected = JobsException.class)
  public void raiseError_Args__Logger__Throwable__String__ObjectArray2() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = null;
    Object[] args = new Object[] {};
    throw JobLogs.buildRuntimeException(log, e, pattern, args);
  }

  @Test(expected = JobsException.class)
  public void raiseError_Args__Logger__Throwable__ObjectArray3() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    throw JobLogs.buildRuntimeException(log, e, "something bad", "who", "cares");
  }

  @Test
  public void logEvery_Args__Logger__int__int__String__ObjectArray() throws Exception {
    Logger log = mock(Logger.class);
    int logEvery = 0;
    int cntr = 0;
    String action = null;
    Object[] args = new Object[] {};
    JobLogs.logEvery(log, logEvery, cntr, action, args);
  }

  @Test
  public void logEvery_Args__Logger__int__String__ObjectArray() throws Exception {
    Logger log = mock(Logger.class);
    int cntr = 0;
    String action = null;
    Object[] args = new Object[] {};
    for (int i = 0; i < 10000; i++) {
      JobLogs.logEvery(log, ++cntr, action, args);
    }
  }

  @Test
  public void buildRuntimeException_Args__Logger__Throwable__String__ObjectArray()
      throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = null;
    Object[] args = new Object[] {};
    JobsException actual = JobLogs.buildRuntimeException(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void buildRuntimeException_Args__2() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = "uh oh: {}";
    Object[] args = new Object[] {"oops!"};
    JobsException actual = JobLogs.buildRuntimeException(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void buildRuntimeException_Args__3() throws Exception {
    Logger log = null;
    Throwable e = null;
    String pattern = "uh oh: {}";
    Object[] args = new Object[] {"oops!"};
    JobsException actual = JobLogs.buildRuntimeException(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void buildCheckedException_Args__Logger__Throwable__String__ObjectArray()
      throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = null;
    Object[] args = new Object[] {};
    NeutronException actual = JobLogs.buildCheckedException(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void checked_Args__Logger__Throwable__String__ObjectArray() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = null;
    Object[] args = new Object[] {};
    NeutronException actual = JobLogs.checked(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void runtime_Args__Logger__Throwable__String__ObjectArray() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    String pattern = null;
    Object[] args = new Object[] {};
    JobsException actual = JobLogs.runtime(log, e, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void stackToString_Args__Exception() throws Exception {
    Exception e = new IllegalStateException("test this");
    String actual = JobLogs.stackToString(e);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void runtime_Args__Logger__String__ObjectArray() throws Exception {
    Logger log = mock(Logger.class);
    String pattern = null;
    Object[] args = new Object[] {};
    JobsException actual = JobLogs.runtime(log, pattern, args);
    assertThat(actual, is(notNullValue()));
  }

}
