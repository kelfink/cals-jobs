package gov.ca.cwds.jobs.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.commons.cli.Option;
import org.junit.Test;

public class JobCmdLineOptionTest {

  @Test
  public void type() throws Exception {
    assertThat(JobCmdLineOption.class, notNullValue());
  }

  @Test
  public void test_valueof() throws Exception {
    JobCmdLineOption actual = JobCmdLineOption.valueOf("BUCKET_RANGE");
    JobCmdLineOption expected = JobCmdLineOption.BUCKET_RANGE;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpt_Args__() throws Exception {
    Option actual = JobCmdLineOption.BUCKET_RANGE.getOpt();
    Option expected = JobOptions.makeOpt("r", JobOptions.CMD_LINE_BUCKET_RANGE,
        "bucket range (-r 20-24)", false, 2, Integer.class, '-');
    assertThat(actual, is(equalTo(expected)));
  }

}
