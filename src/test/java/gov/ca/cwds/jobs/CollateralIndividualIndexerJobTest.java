package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.jobs.config.JobOptionsTest;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class CollateralIndividualIndexerJobTest
    extends PersonJobTester<ReplicatedCollateralIndividual, ReplicatedCollateralIndividual> {

  ReplicatedCollateralIndividualDao dao;
  CollateralIndividualIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedCollateralIndividualDao(sessionFactory);
    target = new CollateralIndividualIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
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
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    final int actual = target.getJobTotalBuckets();
    final int expected = 12;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    String expected = "COLTRL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    CollateralIndividualIndexerJob.main(args);
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final List actual = target.getPartitionRanges();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_Linux() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRS1");

    when(meta.getDatabaseMajorVersion()).thenReturn(12);
    when(meta.getDatabaseMinorVersion()).thenReturn(1);
    when(meta.getDatabaseProductVersion()).thenReturn("DB2/LINUXX8664");

    final List actual = target.getPartitionRanges();
    assertThat(target, notNullValue());
  }

}
