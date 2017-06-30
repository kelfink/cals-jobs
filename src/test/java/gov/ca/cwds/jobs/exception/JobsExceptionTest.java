package gov.ca.cwds.jobs.exception;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JobsExceptionTest {

  @Test
  public void type() throws Exception {
    assertThat(JobsException.class, notNullValue());
  }

  @Test
  public void instantiation1() throws Exception {
    String message = null;
    JobsException target = new JobsException(message);
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation2() throws Exception {
    String message = null;
    JobsException target = new JobsException(message, new IllegalArgumentException("whatever"));
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation4() throws Exception {
    String message = null;
    JobsException target =
        new JobsException(message, new IllegalArgumentException("whatever"), false, false);
    assertThat(target, notNullValue());
  }

}
