package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;

import org.junit.Test;

import gov.ca.cwds.data.std.ApiPhoneAware;

public class IntakePhoneTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakePhone.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IntakePhone target = new IntakePhone();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
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
  public void getPhoneId_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneNumber_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneNumber();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneNumberExtension_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneNumberExtension();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhoneType_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getPhoneType();
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    IntakePhone target = new IntakePhone();
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
    IntakePhone target = new IntakePhone();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setPhoneNumber_Args__String() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    String phoneNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhoneNumber(phoneNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void setPhoneType_Args__ApiPhoneAwarePhoneType() throws Exception {
    IntakePhone target = new IntakePhone();
    // given
    ApiPhoneAware.PhoneType phoneType = mock(ApiPhoneAware.PhoneType.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhoneType(phoneType);
    // then
    // e.g. : verify(mocked).called();
  }

}
