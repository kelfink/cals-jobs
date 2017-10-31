package gov.ca.cwds.jobs.component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.function.Function;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class AtomHibernateTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  private static final class TestAtomHibernate extends TestIndexerJob
      implements AtomHibernate<TestNormalizedEntity, TestDenormalizedEntity> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestAtomHibernate.class);

    private final FlightRecord track = new FlightRecord();

    public TestAtomHibernate(final TestNormalizedEntityDao mainDao,
        final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
        final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
        FlightRecorder jobHistory) {
      super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory);
    }

    @Override
    public FlightRecord getTrack() {
      return track;
    }

    @Override
    public Logger getLogger() {
      return LOGGER;
    }

    @Override
    public String getDriverTable() {
      return super.getDriverTable();
    }

    @Override
    public String getPrepLastChangeSQL() {
      return "INSERT INTO GT_ID (IDENTIFIER)\nSELECT 'abc12347567'\nFROM sysibm.sysdummy1\n";
    }

  }

  TestNormalizedEntityDao dao;
  AtomHibernate target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new TestNormalizedEntityDao(sessionFactory);
    target = new TestAtomHibernate(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory,
        jobHistory);
  }

  @Test
  public void type() throws Exception {
    assertThat(AtomHibernate.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getDBSchemaName_Args__() throws Exception {
    String actual = target.getDBSchemaName();
    String expected = "CWSRS1";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void databaseSchemaName_Args__() throws Exception {
    String actual = AtomHibernate.databaseSchemaName();
    String expected = "CWSRS1";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getIdColumn_Args__() throws Exception {
    String actual = target.getIdColumn();
    String expected = "IDENTIFIER";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDriverTable_Args__() throws Exception {
    String actual = target.getDriverTable();
    String expected = "GOOBER_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrepLastChangeSQL_Args__() throws Exception {
    String actual = target.getPrepLastChangeSQL();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    Object actual = target.extract(rs);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isDB2OnZOS_Args__() throws Exception {
    boolean actual = target.isDB2OnZOS();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isLargeDataSet_Args__() throws Exception {
    boolean actual = target.isLargeDataSet();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPreparedStatementMaker_Args__() throws Exception {
    Function<Connection, PreparedStatement> actual = target.getPreparedStatementMaker();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void prepHibernateLastChange_Args__Session__Transaction__Date() throws Exception {
    Transaction txn = mock(Transaction.class);
    Date lastRunTime = new Date();
    target.prepHibernateLastChange(session, lastRunTime);
  }

}
