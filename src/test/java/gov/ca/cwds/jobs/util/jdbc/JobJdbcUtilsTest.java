package gov.ca.cwds.jobs.util.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.AtomHibernate;
import gov.ca.cwds.jobs.config.JobOptions;

public class JobJdbcUtilsTest extends PersonJobTester {

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
  }

  @Test
  public void type() throws Exception {
    assertThat(JobJdbcUtils.class, notNullValue());
  }

  @Test
  public void makeTimestampString_Args__Date() throws Exception {
    Date date = mock(Date.class);
    String actual = JobJdbcUtils.makeTimestampString(date);
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void makeSimpleTimestampString_Args__Date() throws Exception {
    Date date = mock(Date.class);
    String actual = JobJdbcUtils.makeSimpleTimestampString(date);
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDBSchemaName_Args__() throws Exception {
    String actual = JobJdbcUtils.getDBSchemaName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepHibernateLastChange_Args__Session__Transaction__Date__String__Function()
      throws Exception {
    Session session = mock(Session.class);
    Transaction txn = mock(Transaction.class);
    Date lastRunTime = mock(Date.class);
    String sqlInsertLastChange = null;
    Function<Connection, PreparedStatement> func = mock(Function.class);
    JobJdbcUtils.prepHibernateLastChange(session, txn, lastRunTime, sqlInsertLastChange, func);
  }

  @Test
  public void prepHibernateLastChange_Args__Session__Transaction__Date__String__Function_T__HibernateException()
      throws Exception {
    Session session = mock(Session.class);
    Transaction txn = mock(Transaction.class);
    Date lastRunTime = mock(Date.class);
    String sqlInsertLastChange = null;
    Function<Connection, PreparedStatement> func = mock(Function.class);
    try {
      JobJdbcUtils.prepHibernateLastChange(session, txn, lastRunTime, sqlInsertLastChange, func);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  @Test
  public void calcReaderThreads_Args__JobOptions() throws Exception {
    JobOptions opts = mock(JobOptions.class);
    int actual = JobJdbcUtils.calcReaderThreads(opts);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges64_Args__() throws Exception {
    List actual = JobJdbcUtils.getPartitionRanges64();
    assertThat(actual.size(), is(equalTo(64)));
  }

  @Test
  public void getPartitionRanges16_Args__() throws Exception {
    List actual = JobJdbcUtils.getPartitionRanges16();
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void getPartitionRanges4_Args__() throws Exception {
    List actual = JobJdbcUtils.getPartitionRanges4();
    assertThat(actual.size(), is(equalTo(4)));
  }

  @Test
  public void getCommonPartitionRanges4_Args__AtomHibernate() throws Exception {
    AtomHibernate initialLoad = mock(AtomHibernate.class);
    List actual = JobJdbcUtils.getCommonPartitionRanges4(initialLoad);
    assertThat(actual.size(), is(equalTo(4)));
  }

  @Test
  public void getCommonPartitionRanges16_Args__AtomHibernate() throws Exception {
    AtomHibernate initialLoad = mock(AtomHibernate.class);
    List actual = JobJdbcUtils.getCommonPartitionRanges16(initialLoad);
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void getCommonPartitionRanges64_Args__AtomHibernate() throws Exception {
    AtomHibernate initialLoad = mock(AtomHibernate.class);
    List actual = JobJdbcUtils.getCommonPartitionRanges64(initialLoad);
    assertThat(actual.size(), is(equalTo(64)));
  }

}
