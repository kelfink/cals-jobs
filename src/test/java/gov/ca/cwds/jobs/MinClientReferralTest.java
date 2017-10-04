package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class MinClientReferralTest
    extends PersonJobTester<ReplicatedPersonReferrals, EsPersonReferral> {

  String clientId = DEFAULT_CLIENT_ID;
  String referralId = "ref1234567";
  String sensitivity = "N";
  MinClientReferral target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new MinClientReferral(clientId, referralId, sensitivity);
  }

  @Test
  public void type() throws Exception {
    assertThat(MinClientReferral.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    MinClientReferral actual = MinClientReferral.extract(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    try {
      // when(rs.close())
      MinClientReferral.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getClientId_Args__() throws Exception {
    String actual = target.getClientId();
    String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientId_Args__String() throws Exception {
    String clientId_ = DEFAULT_CLIENT_ID;
    target.setClientId(clientId_);
  }

  @Test
  public void getReferralId_Args__() throws Exception {
    String actual = target.getReferralId();
    String expected = "ref1234567";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {
    String referralId_ = "ref1111111";
    target.setReferralId(referralId_);
  }

  @Test
  public void getSensitivity_Args__() throws Exception {
    String actual = target.getSensitivity();
    String expected = "N";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSensitivity_Args__String() throws Exception {
    String sensitivity_ = "R";
    target.setSensitivity(sensitivity_);
  }

}
