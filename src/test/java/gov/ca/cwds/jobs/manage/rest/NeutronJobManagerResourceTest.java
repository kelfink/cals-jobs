package gov.ca.cwds.jobs.manage.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.manage.rest.NeutronJobManagerResource;

public class NeutronJobManagerResourceTest {

  NeutronJobManagerResource target;

  @Before
  public void setup() throws Exception {
    target = new NeutronJobManagerResource();
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronJobManagerResource.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void testIsServerAlive_Args__() throws Exception {
    String actual = target.testIsServerAlive();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void masterAndCommander_Args__String__String__String() throws Exception {
    String jobName = "validator";
    String command = "status";
    String body = "this is the command body";
    String actual = target.masterAndCommander(jobName, command, body);
    assertThat(actual, is(notNullValue()));
  }

}
