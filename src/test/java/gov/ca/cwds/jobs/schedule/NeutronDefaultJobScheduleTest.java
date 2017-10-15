package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NeutronDefaultJobScheduleTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronDefaultJobSchedule.class, notNullValue());
  }

  @Test
  public void getName_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.CHILD_CASE;
    String actual = target.getName();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isNewDocument_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.CLIENT;
    boolean actual = target.isNewDocument();
    assertThat(actual, is(equalTo(true)));
  }

  @Test
  public void getStartDelaySeconds_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.COLLATERAL_INDIVIDUAL;
    int actual = target.getStartDelaySeconds();
    int expected = 10;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void getPeriodSeconds_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.PARENT_CASE;
    int actual = target.getPeriodSeconds();
    int expected = 0;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void getLoadOrder_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.COLLATERAL_INDIVIDUAL;
    int actual = target.getLoadOrder();
    assertThat(actual, is(not(5)));
  }

  @Test
  public void getJsonElement_Args__() throws Exception {
    NeutronDefaultJobSchedule target = NeutronDefaultJobSchedule.REFERRAL;
    String actual = target.getJsonElement();
    String expected = "referrals";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lookupByJobName_Args__String() throws Exception {
    String key = "client";
    NeutronDefaultJobSchedule actual = NeutronDefaultJobSchedule.lookupByJobName(key);
    NeutronDefaultJobSchedule expected = NeutronDefaultJobSchedule.CLIENT;
    assertThat(actual, is(equalTo(expected)));
  }

}
