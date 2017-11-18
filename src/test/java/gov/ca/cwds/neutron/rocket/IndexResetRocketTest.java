package gov.ca.cwds.neutron.rocket;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.jobs.Goddard;

public class IndexResetRocketTest
    extends Goddard<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  ReplicatedOtherAdultInPlacemtHomeDao dao;
  IndexResetRocket target;

  @Override
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedOtherAdultInPlacemtHomeDao(sessionFactory);
    target = new IndexResetRocket(dao, esDao, mapper, flightPlan);
  }

  @Test
  public void type() throws Exception {
    assertThat(IndexResetRocket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void executeJob_Args__Date() throws Exception {
    Date lastRunDate = new Date();
    Date actual = target.launch(lastRunDate);
    Date expected = lastRunDate;
    assertThat(actual, is(equalTo(expected)));
  }

}
