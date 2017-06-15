package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReporter;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiPhoneAware;

public class IntakeParticipantTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakeParticipant.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    assertThat(target, notNullValue());
  }

  @Test
  public void buildUpdateJson_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.buildUpdateJson();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    target.setId("1234");
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Serializable actual = target.getPrimaryKey();
    // then
    // e.g. : verify(mocked).called();
    Serializable expected = "1234";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
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
    IntakeParticipant target = new IntakeParticipant();
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
  public void getPhones_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ApiPhoneAware[] actual = target.getPhones();
    // then
    // e.g. : verify(mocked).called();
    ApiPhoneAware[] expected = new ApiPhoneAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ApiAddressAware[] actual = target.getAddresses();
    // then
    // e.g. : verify(mocked).called();
    ApiAddressAware[] expected = new ApiAddressAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getEsScreenings_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonScreening[] actual = target.getEsScreenings();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonScreening[] expected = new ElasticSearchPersonScreening[0];
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void toEsPerson_Args__Object__IntakeScreening() throws Exception {
  // IntakeParticipant target = new IntakeParticipant();
  // // given
  // Object esType = null;
  // IntakeScreening screening = mock(IntakeScreening.class);
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
  // // then
  // // e.g. : verify(mocked).called();
  // ElasticSearchPersonNestedPerson expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void addPhone_Args__ElasticSearchPersonPhone() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    ElasticSearchPersonPhone ph = mock(ElasticSearchPersonPhone.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addPhone(ph);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addAddress_Args__ElasticSearchPersonAddress() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    ElasticSearchPersonAddress addr = mock(ElasticSearchPersonAddress.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addAddress(addr);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addScreening_Args__IntakeScreening() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    IntakeScreening screening = mock(IntakeScreening.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addScreening(screening);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getBirthDate_Args__() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
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

    IntakeParticipant target = new IntakeParticipant();
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

    IntakeParticipant target = new IntakeParticipant();
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

    IntakeParticipant target = new IntakeParticipant();
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
  public void getSsn_Args__() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getSsn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setFirstName_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String firstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFirstName(firstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setLastName_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String lastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastName(lastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setBirthDate_Args__Date() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    Date birthDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setBirthDate(birthDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setGender_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String gender = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setGender(gender);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setSsn_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String ssn = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setSsn(ssn);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLegacyId_Args__() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLegacyId_Args__String() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    String legacyId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLegacyId(legacyId);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void toEsPerson_Args__Object__IntakeScreening() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    IntakeParticipant.EsPersonType esType = IntakeParticipant.EsPersonType.REPORTER;
    IntakeScreening screening = mock(IntakeScreening.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonNestedPerson expected = new ElasticSearchPersonReporter();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getScreenings_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Map<String, IntakeScreening> actual = target.getScreenings();
    // then
    // e.g. : verify(mocked).called();
    Map<String, IntakeScreening> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreenings_Args__Map() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    // given
    Map<String, IntakeScreening> screenings = new HashMap<String, IntakeScreening>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreenings(screenings);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setAddresses_Args__Map() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    Map<String, ElasticSearchPersonAddress> addresses =
        new HashMap<String, ElasticSearchPersonAddress>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAddresses(addresses);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setPhones_Args__Map() throws Exception {

    IntakeParticipant target = new IntakeParticipant();
    // given
    Map<String, ElasticSearchPersonPhone> phones = new HashMap<String, ElasticSearchPersonPhone>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhones(phones);
    // then
    // e.g. : verify(mocked).called();
  }

}
