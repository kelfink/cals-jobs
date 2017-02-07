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

import gov.ca.cwds.data.cms.EducationProviderContactDao;
import gov.ca.cwds.data.es.ElasticsearchDao;


/**
 * @author CWDS API Team
 *
 */
@SuppressWarnings("javadoc")
public class EducationProviderContactIndexerJobTest {
  @SuppressWarnings("unused")
  private static EducationProviderContactDao educationProviderContactDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    educationProviderContactDao = new EducationProviderContactDao(sessionFactory);
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
    assertThat(EducationProviderContactIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    EducationProviderContactDao educationProviderContactDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    EducationProviderContactIndexerJob target =
        new EducationProviderContactIndexerJob(educationProviderContactDao, elasticsearchDao,
            lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllNamedQueryExists() throws Exception {
    Query query =
        session.getNamedQuery("gov.ca.cwds.data.persistence.cms.EducationProviderContact.findAll");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.EducationProviderContact.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testFindAllByBucketNamedQueryExists() throws Exception {
    Query query = session
        .getNamedQuery("gov.ca.cwds.data.persistence.cms.EducationProviderContact.findAllByBucket");
    assertThat(query, is(notNullValue()));
  }
}
