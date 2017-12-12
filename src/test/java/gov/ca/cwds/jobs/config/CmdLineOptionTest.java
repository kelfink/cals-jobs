package gov.ca.cwds.jobs.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.commons.cli.Option;
import org.junit.Test;

import gov.ca.cwds.neutron.flight.FlightPlan;

public class CmdLineOptionTest {

  @Test
  public void type() throws Exception {
    assertThat(CmdLineOption.class, notNullValue());
  }

  @Test
  public void test_valueof() throws Exception {
    CmdLineOption actual = CmdLineOption.valueOf("BUCKET_RANGE");
    CmdLineOption expected = CmdLineOption.BUCKET_RANGE;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpt_Args__() throws Exception {
    Option actual = CmdLineOption.BUCKET_RANGE.getOpt();
    Option expected = FlightPlan.makeOpt("r", FlightPlan.CMD_LINE_BUCKET_RANGE,
        "bucket range (-r 20-24)", false, 2, Integer.class, '-');
    assertThat(actual, is(equalTo(expected)));
  }

}
