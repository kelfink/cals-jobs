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

import gov.ca.cwds.data.cms.OtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;

/**
 * @author CWDS API Team
 *
 */
@SuppressWarnings("javadoc")
public class OtherAdultInPlacementHomeTest {
  @SuppressWarnings("unused")
  private static OtherAdultInPlacemtHomeDao otherAdultInPlacementHomeDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    otherAdultInPlacementHomeDao = new OtherAdultInPlacemtHomeDao(sessionFactory);
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
    assertThat(OtherAdultInPlacemtHomeIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    OtherAdultInPlacemtHomeDao otherAdultInPlacementHomeDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherAdultInPlacemtHomeIndexerJob target =
        new OtherAdultInPlacemtHomeIndexerJob(otherAdultInPlacementHomeDao, elasticsearchDao,
            lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllNamedQueryExists() throws Exception {
    Query query =
        session.getNamedQuery("gov.ca.cwds.data.persistence.cms.OtherAdultInPlacemtHome.findAll");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.OtherAdultInPlacemtHome.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testFindAllByBucketNamedQueryExists() throws Exception {
    Query query = session
        .getNamedQuery("gov.ca.cwds.data.persistence.cms.OtherAdultInPlacemtHome.findAllByBucket");
    assertThat(query, is(notNullValue()));
  }

}
