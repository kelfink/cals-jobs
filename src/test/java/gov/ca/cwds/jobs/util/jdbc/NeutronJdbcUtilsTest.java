package gov.ca.cwds.jobs.util.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.component.AtomHibernate;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class NeutronJdbcUtilsTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  private static final class TestAtomHibernate extends TestIndexerJob
      implements AtomHibernate<TestNormalizedEntity, TestDenormalizedEntity> {

    public TestAtomHibernate(final TestNormalizedEntityDao mainDao, final ElasticsearchDao esDao,
        @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
        @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory) {
      super(mainDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory);
    }

    @Override
    public BaseDaoImpl getJobDao() {
      return null;
    }

    @Override
    public boolean isLargeDataSet() {
      return true;
    }

  }

  AtomHibernate initialLoad;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    initialLoad = new TestAtomHibernate(null, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory,
        jobHistory);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronJdbcUtils.class, notNullValue());
  }

  @Test
  public void makeTimestampString_Args__Date() throws Exception {
    Date date = mock(Date.class);
    String actual = NeutronJdbcUtils.makeTimestampString(date);
    String expected = "TIMESTAMP('1969-12-31 16:00:00.000')";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void makeSimpleTimestampString_Args__Date() throws Exception {
    Date date = new Date(1508521402357L);
    String actual = NeutronJdbcUtils.makeSimpleTimestampString(date);
    String expected = "2017-10-20 10:43:22.357";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDBSchemaName_Args__() throws Exception {
    String actual = NeutronJdbcUtils.getDBSchemaName();
    String expected = "CWSRS1";
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
    NeutronJdbcUtils.prepHibernateLastChange(session, lastRunTime, sqlInsertLastChange, func);
  }

  @Test
  public void calcReaderThreads_Args__JobOptions() throws Exception {
    FlightPlan opts = mock(FlightPlan.class);
    when(opts.getThreadCount()).thenReturn(4L);

    int actual = NeutronThreadUtils.calcReaderThreads(opts);
    int expected = 4;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges64_Args__() throws Exception {
    List actual = NeutronJdbcUtils.getPartitionRanges64();
    assertThat(actual.size(), is(equalTo(64)));
  }

  @Test
  public void getPartitionRanges16_Args__() throws Exception {
    List actual = NeutronJdbcUtils.getPartitionRanges16();
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void getPartitionRanges4_Args__() throws Exception {
    List actual = NeutronJdbcUtils.getPartitionRanges4();
    assertThat(actual.size(), is(equalTo(4)));
  }

  @Test
  public void getCommonPartitionRanges4_Args__AtomHibernate() throws Exception {
    List actual = NeutronJdbcUtils.getCommonPartitionRanges4(initialLoad);
    assertThat(actual.size(), is(equalTo(4)));
  }

  @Test
  public void getCommonPartitionRanges16_Args__AtomHibernate() throws Exception {
    final List actual = NeutronJdbcUtils.getCommonPartitionRanges16(initialLoad);
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void getCommonPartitionRanges64_Args__AtomHibernate() throws Exception {
    final List actual = NeutronJdbcUtils.getCommonPartitionRanges64(initialLoad);
    assertThat(actual.size(), is(equalTo(64)));
  }

}
