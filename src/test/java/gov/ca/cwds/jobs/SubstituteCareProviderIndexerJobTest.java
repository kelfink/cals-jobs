package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedSubstituteCareProviderDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class SubstituteCareProviderIndexerJobTest
    extends PersonJobTester<ReplicatedSubstituteCareProvider, ReplicatedSubstituteCareProvider> {

  ReplicatedSubstituteCareProviderDao dao;
  SubstituteCareProviderIndexJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedSubstituteCareProviderDao(sessionFactory);
    target = new SubstituteCareProviderIndexJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void testType() throws Exception {
    assertThat(SubstituteCareProviderIndexJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(7)));
  }

  @Test
  public void getPartitionRanges_REP() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSREP");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(7)));
  }

  @Test
  public void getPartitionRanges_RS1() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRS1");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(1)));
  }

  @Test
  public void getPartitionRanges_RS1_Linux() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRS1");
    when(meta.getDatabaseProductVersion()).thenReturn("LINUX");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(1)));
  }

}
