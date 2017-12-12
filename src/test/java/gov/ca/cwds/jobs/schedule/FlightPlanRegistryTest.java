package gov.ca.cwds.jobs.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import gov.ca.cwds.jobs.test.Mach1TestRocket;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.launch.FlightPlanRegistry;

public class FlightPlanRegistryTest {

  @Test
  public void type() throws Exception {
    assertThat(FlightPlanRegistry.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    FlightPlan baseOpts = null;
    FlightPlanRegistry target = new FlightPlanRegistry(baseOpts);
    assertThat(target, notNullValue());
  }

  @Test
  public void getFlightSettings_Args__Class() throws Exception {
    FlightPlan baseOpts = null;
    FlightPlanRegistry target = new FlightPlanRegistry(baseOpts);
    Class<?> klazz = Mach1TestRocket.class;
    FlightPlan actual = target.getFlightPlan(klazz);
    FlightPlan expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addFlightSettings_Args__Class__FlightPlan() throws Exception {
    FlightPlan baseOpts = null;
    FlightPlanRegistry target = new FlightPlanRegistry(baseOpts);
    Class<?> klazz = Mach1TestRocket.class;
    FlightPlan opts = mock(FlightPlan.class);
    target.addFlightPlan(klazz, opts);
  }

  @Test
  public void getGlobalOpts_Args__() throws Exception {
    FlightPlan baseOpts = null;
    FlightPlanRegistry target = new FlightPlanRegistry(baseOpts);
    FlightPlan actual = target.getBaseFlightPlan();
    FlightPlan expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
