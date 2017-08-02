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

import gov.ca.cwds.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAttorney;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class AttorneyIndexerJobTest {

  @SuppressWarnings("unused")
  private static ReplicatedAttorneyDao attorneyDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
    attorneyDao = new ReplicatedAttorneyDao(sessionFactory);
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
    assertThat(AttorneyIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedAttorneyDao attorneyDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    AttorneyIndexerJob target = new AttorneyIndexerJob(attorneyDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query =
        session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testFindAllByBucketExists() throws Exception {
    Query query = session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllByBucket");
    assertThat(query, is(notNullValue()));
  }

}
