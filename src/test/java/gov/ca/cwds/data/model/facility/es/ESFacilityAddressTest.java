package gov.ca.cwds.data.model.facility.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
    String actual = target.getStateCodeType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStateCodeType_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String stateCodeType = null;
    target.setStateCodeType(stateCodeType);
  }

  @Test
  public void getZipCode_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String actual = target.getZipCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipCode_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String zipCode = null;
    target.setZipCode(zipCode);
  }

  @Test
  public void getZipSuffixCode_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String actual = target.getZipSuffixCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipSuffixCode_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String zipSuffixCode = null;
    target.setZipSuffixCode(zipSuffixCode);
  }

  @Test
  public void getStreetAddress_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String actual = target.getStreetAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStreetAddress_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String streetAddress = null;
    target.setStreetAddress(streetAddress);
  }

  @Test
  public void getCity_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String actual = target.getCity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCity_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String city = null;
    target.setCity(city);
  }

  @Test
  public void getCounty_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String actual = target.getCounty();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__String() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    String county = null;
    target.setCounty(county);
  }

  @Test
  public void hashCode_Args__() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
