package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAttorney;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class AttorneyIndexerJobTest
    extends PersonJobTester<ReplicatedAttorney, ReplicatedAttorney> {

  ReplicatedAttorneyDao dao;
  AttorneyIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedAttorneyDao(this.sessionFactory);
    target = new AttorneyIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory);
  }

  @Test
  public void testType() throws Exception {
    assertThat(AttorneyIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    final Query query =
        session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  @Ignore
  public void testFindAllByBucketExists() throws Exception {
    final Query query =
        session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllByBucket");
    assertThat(query, is(notNullValue()));
  }

}
