package gov.ca.cwds.neutron.atom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.neutron.atom.AtomDocumentSecurity;
import gov.ca.cwds.neutron.flight.FlightPlan;

public class AtomDocumentSecurityTest {

  private static final class TestAtomSecurity implements AtomDocumentSecurity {
  }

  AtomDocumentSecurity target = new TestAtomSecurity();

  @Before
  public void setup() throws Exception {
    target = new TestAtomSecurity();
  }

  @Test
  public void type() throws Exception {
    assertThat(AtomDocumentSecurity.class, notNullValue());
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
    FlightPlan opts = new FlightPlan();
    String indicator = "N";
    boolean actual = AtomDocumentSecurity.isNotSealedSensitive(opts, indicator);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isNotSealedSensitive_Args__JobOptions__Y() throws Exception {
    FlightPlan opts = new FlightPlan();
    opts.setLoadSealedAndSensitive(true);
    String indicator = "R";
    boolean actual = AtomDocumentSecurity.isNotSealedSensitive(opts, indicator);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
