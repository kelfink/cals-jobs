package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedReporterDao;

/**
 * Test for {@link ReporterIndexerJob}.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ReporterIndexerJobTest extends PersonJobTester {

  @SuppressWarnings("unused")
  private ReplicatedReporterDao dao;

  private ReporterIndexerJob target;

  // private static SessionFactory sessionFactory;
  // private Session session;

  // @BeforeClass
  // public static void beforeClass() {
  // sessionFactory =
  // new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
  // dao = new ReplicatedReporterDao(sessionFactory);
  // }
  //
  // @AfterClass
  // public static void afterClass() {
  // sessionFactory.close();
  // }

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedReporterDao(sessionFactory);
    target = new ReporterIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(opts);
    // session = sessionFactory.getCurrentSession();
    // session.beginTransaction();
  }

  @After
  public void teardown() {
    // session.getTransaction().rollback();
  }

  @Test
  public void testType() throws Exception {
    assertThat(ReporterIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void type() throws Exception {
    assertThat(ReporterIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getIdColumn_Args__() throws Exception {
    final String actual = target.getIdColumn();
    final String expected = "FKREFERL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    String expected = "REPTR_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    ReporterIndexerJob.main(args);
  }

  @Test
  public void getPartitionRanges_Args() throws Exception {
    final List actual = target.getPartitionRanges();
    final List expected = new ArrayList<>();
    expected.add(Pair.of("aaaaaaaaaa", "9999999999"));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(64)));
  }

}
