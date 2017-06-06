package gov.ca.cwds.jobs.util;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import gov.ca.cwds.jobs.exception.JobsException;

public class JobLogUtilsTest {

  @Test
  public void type() throws Exception {
    assertThat(JobLogUtils.class, notNullValue());
  }

  @Test
  public void logEvery_Args__Logger__int__String__StringArray() throws Exception {
    // given
    Logger log = mock(Logger.class);
    int cntr = 0;
    String action = null;
    String[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    JobLogUtils.logEvery(log, cntr, action, args);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void logEvery_Args__int__String__StringArray() throws Exception {
    // given
    int cntr = 0;
    String action = null;
    Object[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    JobLogUtils.logEvery(cntr, action, args);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test(expected = JobsException.class)
  public void throwFatalError_Args__Logger__Throwable__String__ObjectArray() throws Exception {
    // given
    Logger log = mock(Logger.class);
    Throwable e = new IllegalStateException("hello world");
    String pattern = null;
    Object[] args = new Object[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    JobLogUtils.throwFatalError(log, e, pattern, args);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test(expected = JobsException.class)
  public void throwFatalError_Args__Logger__Throwable__String() throws Exception {
    // given
    Logger log = mock(Logger.class);
    Throwable e = new IllegalStateException("error message");
    String message = "hello world";
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    JobLogUtils.throwFatalError(log, e, message);
    // then
    // e.g. : verify(mocked).called();
  }

}
