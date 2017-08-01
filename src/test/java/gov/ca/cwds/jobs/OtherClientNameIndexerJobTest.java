package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedAkaDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.NeutronStaticSessionFactory;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class OtherClientNameIndexerJobTest {

  @SuppressWarnings("unused")
  private static ReplicatedAkaDao dao;
  private static SessionFactory sessionFactory;
  private Session session;

  @BeforeClass
  public static void beforeClass() {
    sessionFactory = NeutronStaticSessionFactory.getSessionFactory();
    dao = new ReplicatedAkaDao(sessionFactory);
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
    ReplicatedAkaDao otherClientNameDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(otherClientNameDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void type() throws Exception {
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedAkaDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(mainDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  // @Test
  public void getPartitionRanges_Args__() throws Exception {
    ReplicatedAkaDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(mainDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List actual = target.getPartitionRanges();
    // then
    // e.g. : verify(mocked).called();
    List expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    ReplicatedAkaDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(mainDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacySourceTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = "OCL_NM_T";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void main_Args__StringArray() throws Exception {
    // given
    String[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    OtherClientNameIndexerJob.main(args);
    // then
    // e.g. : verify(mocked).called();
  }

}
