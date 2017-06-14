package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
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

}
