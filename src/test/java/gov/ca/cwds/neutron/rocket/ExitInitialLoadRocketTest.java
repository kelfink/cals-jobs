package gov.ca.cwds.neutron.rocket;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.jobs.Goddard;

public class ExitInitialLoadRocketTest extends Goddard {

  ReplicatedOtherAdultInPlacemtHomeDao dao;
  ExitInitialLoadRocket target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedOtherAdultInPlacemtHomeDao(sessionFactory);
    target = new ExitInitialLoadRocket(dao, esDao, mapper, launchDirector, flightPlan);
  }

  @Test
  public void type() throws Exception {
    assertThat(ExitInitialLoadRocket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void executeJob_Args__Date() throws Exception {
    Date lastRunDate = new Date();
    Date actual = target.executeJob(lastRunDate);
    Date expected = lastRunDate;
    assertThat(actual, is(equalTo(expected)));
  }

}
