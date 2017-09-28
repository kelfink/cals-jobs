package gov.ca.cwds.jobs.util;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.slf4j.Logger;

import gov.ca.cwds.jobs.exception.JobsException;

public class JobLogsTest {

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
    JobLogs.raiseError(log, e, pattern, args);
  }

  @Test(expected = JobsException.class)
  public void throwFatalError_Args__Logger__Throwable__String() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = new IllegalStateException("error message");
    String message = "hello world";
    JobLogs.raiseError(log, e, message);
  }

  @Test(expected = JobsException.class)
  public void logEvery_Args__Logger__int__String__ObjectArray1() throws Exception {
    Exception e = new Exception();
    JobLogs.raiseError(JobLogs.LOGGER, e, "BATCH ERROR! {}", e.getMessage());
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
    JobLogs.raiseError(log, e, pattern, args);
  }

  @Test(expected = JobsException.class)
  public void raiseError_Args__Logger__Throwable__ObjectArray3() throws Exception {
    Logger log = mock(Logger.class);
    Throwable e = null;
    Object[] args = new Object[] {};
    JobLogs.raiseError(log, e, args);
  }

}

