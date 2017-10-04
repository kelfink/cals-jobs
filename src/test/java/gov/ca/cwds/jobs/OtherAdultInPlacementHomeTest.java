package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class OtherAdultInPlacementHomeTest
    extends PersonJobTester<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  ReplicatedOtherAdultInPlacemtHomeDao dao;
  OtherAdultInPlacemtHomeIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedOtherAdultInPlacemtHomeDao(this.sessionFactory);
    target = new OtherAdultInPlacemtHomeIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory);
  }

  @Test
  public void testType() throws Exception {
    assertThat(OtherAdultInPlacemtHomeIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

}
