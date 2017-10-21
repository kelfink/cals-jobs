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

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;

public class AtomHibernateTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  private static final class TestAtomHibernate implements AtomHibernate {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestAtomHibernate.class);
    private final JobProgressTrack track = new JobProgressTrack();

    @Override
    public JobProgressTrack getTrack() {
      return track;
    }

    @Override
    public ElasticsearchDao getEsDao() {
      return null;
    }

    @Override
    public Logger getLogger() {
      return LOGGER;
    }

    @Override
    public JobOptions getOpts() {
      return null;
    }

    @Override
    public BaseDaoImpl getJobDao() {
      return null;
    }

  }

  AtomHibernate target = new TestAtomHibernate();

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new TestAtomHibernate();
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
    String expected = "VW_WHATEVER";
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
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
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
    boolean expected = false;
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
    target.prepHibernateLastChange(session, txn, lastRunTime);
  }

}
