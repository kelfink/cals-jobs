package gov.ca.cwds.data.model.facility.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ESFacilityAddressTest {

  @Test
  public void type() throws Exception {
    assertThat(ESFacilityAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    assertThat(target, notNullValue());
  }

  @Test
  public void getStateCodeType_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getStateCodeType();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStateCodeType_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String stateCodeType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setStateCodeType(stateCodeType);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getZipCode_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getZipCode();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipCode_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String zipCode = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setZipCode(zipCode);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getZipSuffixCode_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getZipSuffixCode();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipSuffixCode_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String zipSuffixCode = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setZipSuffixCode(zipSuffixCode);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStreetAddress_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getStreetAddress();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStreetAddress_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String streetAddress = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setStreetAddress(streetAddress);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCity_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCity();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCity_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String city = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCity(city);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCounty_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCounty();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__String() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    String county = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCounty(county);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void hashCode_Args__() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = 887503681;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {

    ESFacilityAddress target = new ESFacilityAddress();
    // given
    Object obj = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.equals(obj);
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
