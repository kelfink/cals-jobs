package gov.ca.cwds.jobs.component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.config.JobOptions;

public class AtomSecurityTest {

  private static final class TestAtomSecurity implements AtomSecurity {
  }

  AtomSecurity target = new TestAtomSecurity();

  @Before
  public void setup() throws Exception {
    target = new TestAtomSecurity();
  }

  @Test
  public void type() throws Exception {
    assertThat(AtomSecurity.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isNotSealedSensitive_Args__JobOptions__N() throws Exception {
    JobOptions opts = new JobOptions();
    String indicator = "N";
    boolean actual = AtomSecurity.isNotSealedSensitive(opts, indicator);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isNotSealedSensitive_Args__JobOptions__Y() throws Exception {
    JobOptions opts = new JobOptions();
    opts.setLoadSealedAndSensitive(true);
    String indicator = "R";
    boolean actual = AtomSecurity.isNotSealedSensitive(opts, indicator);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
