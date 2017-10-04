package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
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
    target = new MSearchJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory);
  }

  @Test
  public void type() throws Exception {
    assertThat(MSearchJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void doInitialLoadJdbc_Args__() throws Exception {
    target.doInitialLoadJdbc();
  }

  @Test
  @Ignore
  public void extractHibernate_Args__() throws Exception {
    int actual = target.extractHibernate();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    assertThat(actual, notNullValue());
  }

}
