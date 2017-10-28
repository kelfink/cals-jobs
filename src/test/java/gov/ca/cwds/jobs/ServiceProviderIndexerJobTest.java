package gov.ca.cwds.jobs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.JobOptionsTest;
import gov.ca.cwds.jobs.schedule.NeutronJobProgressHistory;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ServiceProviderIndexerJobTest extends PersonJobTester {

  private static final class TestServiceProviderIndexerJob extends ServiceProviderIndexerJob {

    public TestServiceProviderIndexerJob(ReplicatedServiceProviderDao dao, ElasticsearchDao esDao,
        String lastJobRunTimeFilename, ObjectMapper mapper, SessionFactory sessionFactory,
        NeutronJobProgressHistory jobHistory) {
      super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory);
    }

  }

  ServiceProviderIndexerJob target;
  ReplicatedServiceProviderDao dao;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedServiceProviderDao(sessionFactory);
    target = new ServiceProviderIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory, jobHistory);
    target.setOpts(JobOptionsTest.makeGeneric());
  }

  @After
  public void teardown() {
    // session.getTransaction().rollback();
  }

  @Test
  public void testType() throws Exception {
    assertThat(ServiceProviderIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    target = new ServiceProviderIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory, jobHistory);
    assertThat(target, notNullValue());
  }

  // @Test
  // public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
  // Query query = session.getNamedQuery(
  // "gov.ca.cwds.data.persistence.cms.rep.ReplicatedServiceProvider.findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  @Test
  public void type() throws Exception {
    assertThat(ServiceProviderIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    target = new ServiceProviderIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory, jobHistory);
    final List<Pair<String, String>> actual = target.getPartitionRanges();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    target = new ServiceProviderIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory, jobHistory);
    final List<Pair<String, String>> actual = target.getPartitionRanges();
    assertThat(actual, is(notNullValue()));
  }

}
