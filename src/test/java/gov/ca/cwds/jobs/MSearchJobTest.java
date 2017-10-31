package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;

public class MSearchJobTest
    extends PersonJobTester<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  ReplicatedOtherAdultInPlacemtHomeDao dao;
  MSearchJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedOtherAdultInPlacemtHomeDao(this.sessionFactory);
    target = new MSearchJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory, null,
        jobHistory, opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(MSearchJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

}
