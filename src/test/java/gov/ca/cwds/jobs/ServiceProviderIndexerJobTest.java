package gov.ca.cwds.jobs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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

import gov.ca.cwds.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.data.es.ElasticsearchDao;

/**
 * @author CWDS API Team
 *
 */
@SuppressWarnings("javadoc")
public class ServiceProviderIndexerJobTest {

  @SuppressWarnings("unused")
  private static ReplicatedServiceProviderDao serviceProviderDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
    serviceProviderDao = new ReplicatedServiceProviderDao(sessionFactory);
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
    assertThat(ServiceProviderIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedServiceProviderDao serviceProviderDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ServiceProviderIndexerJob target = new ServiceProviderIndexerJob(serviceProviderDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedServiceProvider.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

}
