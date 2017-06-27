package gov.ca.cwds.jobs.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JobConfigurationTest {

  @Test
  public void type() throws Exception {
    assertThat(JobConfiguration.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    JobConfiguration target = new JobConfiguration();
    assertThat(target, notNullValue());
  }

  @Test
  public void getJobLisReaderQuery_Args__() throws Exception {
    JobConfiguration target = new JobConfiguration();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getJobLisReaderQuery();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
