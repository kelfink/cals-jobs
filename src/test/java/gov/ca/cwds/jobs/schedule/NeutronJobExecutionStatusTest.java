package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NeutronJobExecutionStatusTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronJobExecutionStatus.class, notNullValue());
  }

}
