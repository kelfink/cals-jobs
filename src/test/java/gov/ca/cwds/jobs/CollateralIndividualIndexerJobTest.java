package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.jobs.config.JobOptionsTest;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class CollateralIndividualIndexerJobTest extends PersonJobTester {

  ReplicatedCollateralIndividualDao dao;
  CollateralIndividualIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedCollateralIndividualDao(sessionFactory);
    target = new CollateralIndividualIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper,
        sessionFactory);
    target.setOpts(JobOptionsTest.makeGeneric());
  }

  @Test
  public void testType() throws Exception {
    assertThat(CollateralIndividualIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  // @Test
  // public void testfindAllNamedQueryExists() throws Exception {
  // Query query = session.getNamedQuery(
  // "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findPartitionedBuckets");
  // assertThat(query, is(notNullValue()));
  // }

  // @Test
  // public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
  // Query query = session.getNamedQuery(
  // "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  @Test
  public void type() throws Exception {
    assertThat(CollateralIndividualIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedCollateralIndividualDao dao = null;
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    target = new CollateralIndividualIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper,
        sessionFactory);
    int actual = target.getJobTotalBuckets();
    int expected = 12;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    target = new CollateralIndividualIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper,
        sessionFactory);
    String actual = target.getLegacySourceTable();
    String expected = "COLTRL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    CollateralIndividualIndexerJob.main(args);
  }

}

