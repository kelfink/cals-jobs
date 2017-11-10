package gov.ca.cwds.neutron.enums;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gov.ca.cwds.neutron.enums.FlightStatus;

public class FlightStatusTest {

  @Test
  public void type() throws Exception {
    assertThat(FlightStatus.class, notNullValue());
  }

}
