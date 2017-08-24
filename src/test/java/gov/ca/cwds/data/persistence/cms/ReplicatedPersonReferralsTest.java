package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;

public class ReplicatedPersonReferralsTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedPersonReferrals.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    assertThat(target, notNullValue());
  }

  @Test
  public void getReferrals_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    List<ElasticSearchPersonReferral> actual = target.getReferrals();
    List<ElasticSearchPersonReferral> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void addReferral_Args__ElasticSearchPersonReferral__ElasticSearchPersonAllegation()
      throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    ElasticSearchPersonReferral referral = mock(ElasticSearchPersonReferral.class);
    ElasticSearchPersonAllegation allegation = mock(ElasticSearchPersonAllegation.class);
    target.addReferral(referral, allegation);
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
