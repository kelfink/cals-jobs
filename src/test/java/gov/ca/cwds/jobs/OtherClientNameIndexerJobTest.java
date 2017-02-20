package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.cms.OtherClientNameDao;
import gov.ca.cwds.data.es.ElasticsearchDao;

/**
 * @author CWDS API Team
 *
 */
@SuppressWarnings("javadoc")
public class OtherClientNameIndexerJobTest {
  @SuppressWarnings("unused")
  private static OtherClientNameDao otherClientNameDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
    otherClientNameDao = new OtherClientNameDao(sessionFactory);
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
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    OtherClientNameDao otherClientNameDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(otherClientNameDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery("gov.ca.cwds.data.persistence.cms.OtherClientName.findAll");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session
        .getNamedQuery("gov.ca.cwds.data.persistence.cms.OtherClientName.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  // @Test
  // public void testFindAllByBucketExists() throws Exception {
  // Query query =
  // session.getNamedQuery("gov.ca.cwds.data.persistence.cms.OtherClientName.findAllByBucket");
  // assertThat(query, is(notNullValue()));
  // }

}
