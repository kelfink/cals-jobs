package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.std.ApiPhoneAware;

public class IntakePhoneTest {
  private IntakePhone target = new IntakePhone();

  @Before
  public void setup() {
    target = new IntakePhone();
  }

  @Test
  public void type() throws Exception {
    assertThat(IntakePhone.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneId_Args__() throws Exception {
    String actual = target.getPhoneId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneNumber_Args__() throws Exception {
    String actual = target.getPhoneNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneNumberExtension_Args__() throws Exception {
    String actual = target.getPhoneNumberExtension();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneType_Args__() throws Exception {
    Object actual = target.getPhoneType();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    String id = null;
    target.setId(id);
  }

  @Test
  public void setPhoneNumber_Args__String() throws Exception {
    String phoneNumber = null;
    target.setPhoneNumber(phoneNumber);
  }

  // @Test
  public void setPhoneType_Args__ApiPhoneAwarePhoneType() throws Exception {
    ApiPhoneAware.PhoneType phoneType = mock(ApiPhoneAware.PhoneType.class);
    target.setPhoneType(phoneType);
  }

}
