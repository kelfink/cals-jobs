package gov.ca.cwds.neutron.vox;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VoxCommandInstructionTest {

  @Test
  public void type() throws Exception {
    assertThat(VoxCommandInstruction.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    String jobName = null;
    String command = null;
    String body = null;
    VoxCommandInstruction target = new VoxCommandInstruction(jobName, command);
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobName_Args__() throws Exception {
    String jobName = "xyz";
    String command = null;
    String body = null;
    VoxCommandInstruction target = new VoxCommandInstruction(jobName, command);
    String actual = target.getRocket();
    String expected = "xyz";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCommand_Args__() throws Exception {
    String jobName = null;
    String command = null;
    String body = null;
    VoxCommandInstruction target = new VoxCommandInstruction(jobName, command);
    String actual = target.getCommand();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
