package gov.ca.cwds.jobs.exception;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NeutronExceptionTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronException.class, notNullValue());
  }

  @Test
  public void instantiation1() throws Exception {
    String message = "test";
    NeutronException target = new NeutronException(message);
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation2() throws Exception {
    String message = null;
    NeutronException target =
        new NeutronException(message, new IllegalArgumentException("whatever"));
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation4() throws Exception {
    String message = null;
    NeutronException target =
        new NeutronException(message, new IllegalArgumentException("whatever"), false, false);
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation5() throws Exception {
    NeutronException target = new NeutronException(new IllegalArgumentException("whatever"));
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    String message = null;
    NeutronException target = new NeutronException(message);
    assertThat(target, notNullValue());
  }

}
