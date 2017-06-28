package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
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
  private static ReplicatedCollateralIndividualDao dao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory = StaticSessionFactory.getSessionFactory();
    dao = new ReplicatedCollateralIndividualDao(sessionFactory);
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

  @Test
  public void type() throws Exception {
    assertThat(CollateralIndividualIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedCollateralIndividualDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    CollateralIndividualIndexerJob target = new CollateralIndividualIndexerJob(mainDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    ReplicatedCollateralIndividualDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    CollateralIndividualIndexerJob target = new CollateralIndividualIndexerJob(mainDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.getJobTotalBuckets();
    // then
    // e.g. : verify(mocked).called();
    int expected = 12;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    ReplicatedCollateralIndividualDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    CollateralIndividualIndexerJob target = new CollateralIndividualIndexerJob(mainDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacySourceTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = "COLTRL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void main_Args__StringArray() throws Exception {
    // given
    String[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    CollateralIndividualIndexerJob.main(args);
    // then
    // e.g. : verify(mocked).called();
  }

}
