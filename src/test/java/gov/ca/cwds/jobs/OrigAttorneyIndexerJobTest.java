package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedAttorneyDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAttorney;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class OrigAttorneyIndexerJobTest
    extends Goddard<ReplicatedAttorney, ReplicatedAttorney> {

  ReplicatedAttorneyDao dao;
  OrigAttorneyIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedAttorneyDao(this.sessionFactory);
    target = new OrigAttorneyIndexerJob(dao, esDao, lastRunFile, MAPPER, sessionFactory,
        flightRecorder, flightPlan);
  }

  @Test
  public void testType() throws Exception {
    assertThat(OrigAttorneyIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  // @Test
  // @Ignore
  // public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
  // final Query query =
  // session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  // @Test
  // @Ignore
  // public void testFindAllByBucketExists() throws Exception {
  // final Query query =
  // session.getNamedQuery(ReplicatedAttorney.class.getName() + ".findAllByBucket");
  // assertThat(query, is(notNullValue()));
  // }

}
