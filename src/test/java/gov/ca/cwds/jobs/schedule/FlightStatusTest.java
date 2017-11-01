package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FlightStatusTest {

  @Test
  public void type() throws Exception {
    assertThat(FlightStatus.class, notNullValue());
  }

}
