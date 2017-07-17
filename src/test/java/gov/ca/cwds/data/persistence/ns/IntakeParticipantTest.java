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

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
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
    String actual = target.buildUpdateJson();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    target.setId("1234");
    Serializable actual = target.getPrimaryKey();
    Serializable expected = "1234";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhones_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ApiPhoneAware[] actual = target.getPhones();
    ApiPhoneAware[] expected = new ApiPhoneAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ApiAddressAware[] actual = target.getAddresses();
    ApiAddressAware[] expected = new ApiAddressAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getEsScreenings_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ElasticSearchPersonScreening[] actual = target.getEsScreenings();
    ElasticSearchPersonScreening[] expected = new ElasticSearchPersonScreening[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addPhone_Args__ElasticSearchPersonPhone() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ElasticSearchPersonPhone ph = mock(ElasticSearchPersonPhone.class);
    target.addPhone(ph);
  }

  @Test
  public void addAddress_Args__ElasticSearchPersonAddress() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ElasticSearchPersonAddress addr = mock(ElasticSearchPersonAddress.class);
    target.addAddress(addr);
  }

  @Test
  public void addScreening_Args__IntakeScreening() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    IntakeScreening screening = mock(IntakeScreening.class);
    target.addScreening(screening);
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String id = null;
    target.setId(id);
  }

  @Test
  public void setFirstName_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String firstName = null;
    target.setFirstName(firstName);
  }

  @Test
  public void setLastName_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String lastName = null;
    target.setLastName(lastName);
  }

  @Test
  public void setBirthDate_Args__Date() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Date birthDate = mock(Date.class);
    target.setBirthDate(birthDate);
  }

  @Test
  public void setGender_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String gender = null;
    target.setGender(gender);
  }

  @Test
  public void setSsn_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String ssn = null;
    target.setSsn(ssn);
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLegacyId_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String legacyId = null;
    target.setLegacyId(legacyId);
  }

  @Test
  public void toEsPerson_Args__Object__IntakeScreening_1() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    EsPersonType esType = EsPersonType.REPORTER;
    IntakeScreening screening = mock(IntakeScreening.class);
    ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void toEsPerson_Args__Object__IntakeScreening_2() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    EsPersonType esType = EsPersonType.SOCIAL_WORKER;
    IntakeScreening screening = mock(IntakeScreening.class);
    ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void toEsPerson_Args__Object__IntakeScreening_3() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    EsPersonType esType = EsPersonType.STAFF;
    IntakeScreening screening = mock(IntakeScreening.class);
    ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void toEsPerson_Args__Object__IntakeScreening_4() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    EsPersonType esType = EsPersonType.ALL;
    IntakeScreening screening = mock(IntakeScreening.class);
    ElasticSearchPersonNestedPerson actual = target.toEsPerson(esType, screening);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getScreenings_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Map<String, IntakeScreening> actual = target.getScreenings();
    Map<String, IntakeScreening> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreenings_Args__Map() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Map<String, IntakeScreening> screenings = new HashMap<String, IntakeScreening>();
    target.setScreenings(screenings);
  }

  @Test
  public void setAddresses_Args__Map() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Map<String, ElasticSearchPersonAddress> addresses =
        new HashMap<String, ElasticSearchPersonAddress>();
    target.setAddresses(addresses);
  }

  @Test
  public void setPhones_Args__Map() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Map<String, ElasticSearchPersonPhone> phones = new HashMap<String, ElasticSearchPersonPhone>();
    target.setPhones(phones);
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getLegacyLastUpdated_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Date actual = target.getLegacyLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLegacyLastUpdated_Args__Date() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    Date legacyLastUpdated = mock(Date.class);
    target.setLegacyLastUpdated(legacyLastUpdated);
  }

  @Test
  public void getLegacyTable_Args__() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String actual = target.getLegacyTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLegacyTable_Args__String() throws Exception {
    IntakeParticipant target = new IntakeParticipant();
    String legacyTable = null;
    target.setLegacyTable(legacyTable);
  }

}
