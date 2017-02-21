package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.data.cms.ReporterDao;
import gov.ca.cwds.data.es.ElasticsearchDao;

/**
 * @author Tabpcenc1
 *
 */
@SuppressWarnings("javadoc")
public class ReporterIndexerJobTest {
  @SuppressWarnings("unused")
  private static ReporterDao reporterDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
    reporterDao = new ReporterDao(sessionFactory);
  }

  @AfterClass
  public static void afterClass() {
    sessionFactory.close();
  }

  @Before
  public void setup() {
    session = sessionFactory.getCurrentSession();
    session.beginTransaction();
  }

  @After
  public void teardown() {
    session.getTransaction().rollback();
  }

  @Test
  public void testType() throws Exception {
    assertThat(ReporterIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedReporterDao reporterDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ReporterIndexerJob target = new ReporterIndexerJob(reporterDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  // @Test
  // public void testfindAllNamedQueryExists() throws Exception {
  // Query query =
  // session.getNamedQuery("gov.ca.cwds.data.persistence.cms.ReplicatedReporter.findAll");
  // assertThat(query, is(notNullValue()));
  // }
  //
  // @Test
  // public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
  // Query query = session
  // .getNamedQuery("gov.ca.cwds.data.persistence.cms.ReplicatedReporter.findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  // @Test
  // public void testFindAllByBucketNamedQueryExists() throws Exception {
  // Query query =
  // session.getNamedQuery("gov.ca.cwds.data.persistence.cms.Reporter.findAllByBucket");
  // assertThat(query, is(notNullValue()));
  // }

}
