package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RocketCommandInstructionTest {

  @Test
  public void type() throws Exception {
    assertThat(RocketCommandInstruction.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    String jobName = null;
    String command = null;
    String body = null;
    RocketCommandInstruction target = new RocketCommandInstruction(jobName, command, body);
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobName_Args__() throws Exception {
    String jobName = "xyz";
    String command = null;
    String body = null;
    RocketCommandInstruction target = new RocketCommandInstruction(jobName, command, body);
    String actual = target.getRocketName();
    String expected = "xyz";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCommand_Args__() throws Exception {
    String jobName = null;
    String command = null;
    String body = null;
    RocketCommandInstruction target = new RocketCommandInstruction(jobName, command, body);
    String actual = target.getCommand();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getConfig_Args__() throws Exception {
    String jobName = null;
    String command = null;
    String body = null;
    RocketCommandInstruction target = new RocketCommandInstruction(jobName, command, body);
    String actual = target.getConfig();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
