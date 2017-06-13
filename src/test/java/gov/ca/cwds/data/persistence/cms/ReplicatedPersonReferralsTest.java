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

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReferral;

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
  public void geReferrals_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPersonReferral> actual = target.geReferrals();
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPersonReferral> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addReferral_Args__ElasticSearchPersonReferral__ElasticSearchPersonAllegation()
      throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    ElasticSearchPersonReferral referral = mock(ElasticSearchPersonReferral.class);
    ElasticSearchPersonAllegation allegation = mock(ElasticSearchPersonAllegation.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addReferral(referral, allegation);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Serializable actual = target.getPrimaryKey();
    // then
    // e.g. : verify(mocked).called();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getBirthDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getGender();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getMiddleName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getNameSuffix();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedPersonReferrals target = new ReplicatedPersonReferrals();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getSsn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
