package gov.ca.cwds.neutron.jetpack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

import gov.ca.cwds.jobs.test.TestIndexerJob;

public class JetPackLoggerTest {

  JetPackLogger target;
  String msg;

  @Before
  public void setup() throws Exception {
    target = new JetPackLogger(JetPackLoggerTest.class);
    msg = "well that was fun";
  }

  @Test
  public void trace_Args__String__SupplierArray() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isTraceEnabled()).thenReturn(true);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.trace("{} {}", () -> "Shrink", () -> "Ray");
  }

  @Test
  public void trace_Args__String__SupplierArray__2() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isTraceEnabled()).thenReturn(false);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.trace("{} {}", () -> "Shrink", () -> "Ray");
  }

  @Test
  public void debug_Args__String__SupplierArray() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isDebugEnabled()).thenReturn(true);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.debug("{} {}", () -> "Hyper", () -> "Cube");
  }

  @Test
  public void debug_Args__String__SupplierArray__2() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isDebugEnabled()).thenReturn(false);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.debug("{} {}", () -> "Hyper", () -> "Cube");
  }

  @Test
  public void info_Args__String__SupplierArray() throws Exception {
    target.info("first name: {}, last name: {}", () -> "Jimmy", () -> "Neutron");
  }

  @Test
  public void warn_Args__String__SupplierArray() throws Exception {
    target.warn("{} {}", () -> "Burping", () -> "Soda");
  }

  @Test
  public void error_Args__String__SupplierArray() throws Exception {
    target.error("{} {}", () -> "X-Ray", () -> "Specs");
  }

  @Test
  public void info_Args__String__SupplierArray__2() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isInfoEnabled()).thenReturn(false);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.info("first name: {}, last name: {}", () -> "Jimmy", () -> "Neutron");
  }

  @Test
  public void warn_Args__String__SupplierArray__2() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isWarnEnabled()).thenReturn(false);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.warn("{} {}", () -> "Burping", () -> "Soda");
  }

  @Test
  public void error_Args__String__SupplierArray__2() throws Exception {
    final Logger log = mock(Logger.class);
    when(log.isErrorEnabled()).thenReturn(false);
    target = new JetPackLogger(TestIndexerJob.class);
    target.setLogger(log);
    target.error("{} {}", () -> "X-Ray", () -> "Spex");
  }

  @Test
  public void type() throws Exception {
    assertThat(JetPackLogger.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getName_Args__() throws Exception {
    String actual = target.getName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isTraceEnabled_Args__() throws Exception {
    boolean actual = target.isTraceEnabled();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trace_Args__String() throws Exception {
    target.trace(msg);
  }

  @Test
  public void trace_Args__String__Object() throws Exception {
    String format = null;
    Object arg = null;
    target.trace(format, arg);
  }

  @Test
  public void trace_Args__String__Object__Object() throws Exception {
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.trace(format, arg1, arg2);
  }

  @Test
  public void trace_Args__String__ObjectArray() throws Exception {
    String format = null;
    Object[] arguments = new Object[] {};
    target.trace(format, arguments);
  }

  @Test
  public void trace_Args__String__Throwable() throws Exception {
    Throwable t = null;
    target.trace(msg, t);
  }

  @Test
  public void isTraceEnabled_Args__Marker() throws Exception {
    Marker marker = mock(Marker.class);
    boolean actual = target.isTraceEnabled(marker);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trace_Args__Marker__String() throws Exception {
    Marker marker = mock(Marker.class);
    target.trace(marker, msg);
  }

  @Test
  public void trace_Args__Marker__String__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg = null;
    target.trace(marker, format, arg);
  }

  @Test
  public void trace_Args__Marker__String__Object__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.trace(marker, format, arg1, arg2);
  }

  @Test
  public void trace_Args__Marker__String__ObjectArray() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object[] argArray = new Object[] {};
    target.trace(marker, format, argArray);
  }

  @Test
  public void trace_Args__Marker__String__Throwable() throws Exception {
    Marker marker = mock(Marker.class);
    Throwable t = null;
    target.trace(marker, msg, t);
  }

  @Test
  public void isDebugEnabled_Args__() throws Exception {
    boolean actual = target.isDebugEnabled();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void debug_Args__String() throws Exception {
    target.debug(msg);
  }

  @Test
  public void debug_Args__String__Object() throws Exception {
    String format = null;
    Object arg = null;
    target.debug(format, arg);
  }

  @Test
  public void debug_Args__String__Object__Object() throws Exception {
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.debug(format, arg1, arg2);
  }

  @Test
  public void debug_Args__String__ObjectArray() throws Exception {
    String format = null;
    Object[] arguments = new Object[] {};
    target.debug(format, arguments);
  }

  @Test
  public void debug_Args__String__Throwable() throws Exception {
    Throwable t = null;
    target.debug(msg, t);
  }

  @Test
  public void isDebugEnabled_Args__Marker() throws Exception {
    Marker marker = mock(Marker.class);
    boolean actual = target.isDebugEnabled(marker);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void debug_Args__Marker__String() throws Exception {
    Marker marker = mock(Marker.class);
    target.debug(marker, msg);
  }

  @Test
  public void debug_Args__Marker__String__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg = null;
    target.debug(marker, format, arg);
  }

  @Test
  public void debug_Args__Marker__String__Object__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.debug(marker, format, arg1, arg2);
  }

  @Test
  public void debug_Args__Marker__String__ObjectArray() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object[] arguments = new Object[] {};
    target.debug(marker, format, arguments);
  }

  @Test
  public void debug_Args__Marker__String__Throwable() throws Exception {
    Marker marker = mock(Marker.class);
    Throwable t = null;
    target.debug(marker, msg, t);
  }

  @Test
  public void isInfoEnabled_Args__() throws Exception {
    boolean actual = target.isInfoEnabled();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void info_Args__String() throws Exception {
    target.info(msg);
  }

  @Test
  public void info_Args__String__Object() throws Exception {
    String format = null;
    Object arg = null;
    target.info(format, arg);
  }

  @Test
  public void info_Args__String__Object__Object() throws Exception {
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.info(format, arg1, arg2);
  }

  @Test
  public void info_Args__String__ObjectArray() throws Exception {
    String format = null;
    Object[] arguments = new Object[] {};
    target.info(format, arguments);
  }

  @Test
  public void info_Args__String__Throwable() throws Exception {
    Throwable t = null;
    target.info(msg, t);
  }

  @Test
  public void isInfoEnabled_Args__Marker() throws Exception {
    Marker marker = mock(Marker.class);
    boolean actual = target.isInfoEnabled(marker);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void info_Args__Marker__String() throws Exception {
    Marker marker = mock(Marker.class);
    target.info(marker, msg);
  }

  @Test
  public void info_Args__Marker__String__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg = null;
    target.info(marker, format, arg);
  }

  @Test
  public void info_Args__Marker__String__Object__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.info(marker, format, arg1, arg2);
  }

  @Test
  public void info_Args__Marker__String__ObjectArray() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object[] arguments = new Object[] {};
    target.info(marker, format, arguments);
  }

  @Test
  public void info_Args__Marker__String__Throwable() throws Exception {
    Marker marker = mock(Marker.class);
    Throwable t = null;
    target.info(marker, msg, t);
  }

  @Test
  public void isWarnEnabled_Args__() throws Exception {
    boolean actual = target.isWarnEnabled();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void warn_Args__String() throws Exception {
    target.warn(msg);
  }

  @Test
  public void warn_Args__String__Object() throws Exception {
    String format = null;
    Object arg = null;
    target.warn(format, arg);
  }

  @Test
  public void warn_Args__String__ObjectArray() throws Exception {
    String format = null;
    Object[] arguments = new Object[] {};
    target.warn(format, arguments);
  }

  @Test
  public void warn_Args__String__Object__Object() throws Exception {
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.warn(format, arg1, arg2);
  }

  @Test
  public void warn_Args__String__Throwable() throws Exception {
    Throwable t = null;
    target.warn(msg, t);
  }

  @Test
  public void isWarnEnabled_Args__Marker() throws Exception {
    Marker marker = mock(Marker.class);
    boolean actual = target.isWarnEnabled(marker);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void warn_Args__Marker__String() throws Exception {
    Marker marker = mock(Marker.class);
    target.warn(marker, msg);
  }

  @Test
  public void warn_Args__Marker__String__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg = null;
    target.warn(marker, format, arg);
  }

  @Test
  public void warn_Args__Marker__String__Object__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.warn(marker, format, arg1, arg2);
  }

  @Test
  public void warn_Args__Marker__String__ObjectArray() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object[] arguments = new Object[] {};
    target.warn(marker, format, arguments);
  }

  @Test
  public void warn_Args__Marker__String__Throwable() throws Exception {
    Marker marker = mock(Marker.class);
    Throwable t = null;
    target.warn(marker, msg, t);
  }

  @Test
  public void isErrorEnabled_Args__() throws Exception {
    boolean actual = target.isErrorEnabled();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void error_Args__String() throws Exception {
    target.error(msg);
  }

  @Test
  public void error_Args__String__Object() throws Exception {
    String format = null;
    Object arg = null;
    target.error(format, arg);
  }

  @Test
  public void error_Args__String__Object__Object() throws Exception {
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.error(format, arg1, arg2);
  }

  @Test
  public void error_Args__String__ObjectArray() throws Exception {
    String format = null;
    Object[] arguments = new Object[] {};
    target.error(format, arguments);
  }

  @Test
  public void error_Args__String__Throwable() throws Exception {
    Throwable t = null;
    target.error(msg, t);
  }

  @Test
  public void isErrorEnabled_Args__Marker() throws Exception {
    Marker marker = mock(Marker.class);
    boolean actual = target.isErrorEnabled(marker);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void error_Args__Marker__String() throws Exception {
    Marker marker = mock(Marker.class);
    target.error(marker, msg);
  }

  @Test
  public void error_Args__Marker__String__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg = null;
    target.error(marker, format, arg);
  }

  @Test
  public void error_Args__Marker__String__Object__Object() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object arg1 = null;
    Object arg2 = null;
    target.error(marker, format, arg1, arg2);
  }

  @Test
  public void error_Args__Marker__String__ObjectArray() throws Exception {
    Marker marker = mock(Marker.class);
    String format = null;
    Object[] arguments = new Object[] {};
    target.error(marker, format, arguments);
  }

  @Test
  public void error_Args__Marker__String__Throwable() throws Exception {
    Marker marker = mock(Marker.class);
    Throwable t = null;
    target.error(marker, msg, t);
  }

}
