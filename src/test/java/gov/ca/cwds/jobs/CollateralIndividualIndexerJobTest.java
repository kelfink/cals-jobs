package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.StaticSessionFactory;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class CollateralIndividualIndexerJobTest {

  @SuppressWarnings("unused")
  private static ReplicatedCollateralIndividualDao collateralIndividualDao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    // sessionFactory =
    // new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
    sessionFactory = StaticSessionFactory.getSessionFactory();
    collateralIndividualDao = new ReplicatedCollateralIndividualDao(sessionFactory);
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
    assertThat(CollateralIndividualIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedCollateralIndividualDao collateralIndividualDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    CollateralIndividualIndexerJob target = new CollateralIndividualIndexerJob(
        collateralIndividualDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findPartitionedBuckets");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

}
