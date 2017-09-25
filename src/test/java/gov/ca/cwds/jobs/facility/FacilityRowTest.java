package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;

import org.junit.Test;

public class FacilityRowTest {

  @Test
  public void type() throws Exception {
    assertThat(FacilityRow.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    FacilityRow target = new FacilityRow();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    FacilityRow target = new FacilityRow();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    FacilityRow target = new FacilityRow();
    String actual = target.getId();
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    FacilityRow target = new FacilityRow();
    String id = null;
    target.setId(id);
  }

  @Test
  public void getType_Args__() throws Exception {
    FacilityRow target = new FacilityRow();
    String actual = target.getType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setType_Args__String() throws Exception {
    FacilityRow target = new FacilityRow();
    String type = null;
    target.setType(type);
  }

  @Test
  public void getName_Args__() throws Exception {
    FacilityRow target = new FacilityRow();
    String actual = target.getName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setName_Args__String() throws Exception {
    FacilityRow target = new FacilityRow();
    String name = null;
    target.setName(name);
  }

  @Test
  public void getLicenseeName_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLicenseeName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseeName_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String licenseeName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLicenseeName(licenseeName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAssignedWorker_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAssignedWorker();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAssignedWorker_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String assignedWorker = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAssignedWorker(assignedWorker);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getDistrictOffice_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getDistrictOffice();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDistrictOffice_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String districtOffice = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setDistrictOffice(districtOffice);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLicenseNumber_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLicenseNumber();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseNumber_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String licenseNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLicenseNumber(licenseNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLicenseStatus_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLicenseStatus();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseStatus_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String licenseStatus = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLicenseStatus(licenseStatus);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCapacity_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCapacity();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCapacity_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String capacity = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCapacity(capacity);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLicenseEffectiveDate_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getLicenseEffectiveDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseEffectiveDate_Args__Date() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    Date licenseEffectiveDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLicenseEffectiveDate(licenseEffectiveDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getOriginalApplicationReceivedDate_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getOriginalApplicationReceivedDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOriginalApplicationReceivedDate_Args__Date() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    Date originalApplicationReceivedDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setOriginalApplicationReceivedDate(originalApplicationReceivedDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLastVisitDate_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getLastVisitDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastVisitDate_Args__Date() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    Date lastVisitDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastVisitDate(lastVisitDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLastVisitReason_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLastVisitReason();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastVisitReason_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String lastVisitReason = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastVisitReason(lastVisitReason);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPrimaryPhoneNumber_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPrimaryPhoneNumber();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPrimaryPhoneNumber_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String primaryPhoneNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPrimaryPhoneNumber(primaryPhoneNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAltPhoneNumber_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAltPhoneNumber();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAltPhoneNumber_Args__String() throws Exception {

    FacilityRow target = new FacilityRow();
    // given
    String altPhoneNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAltPhoneNumber(altPhoneNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStateCodeType_Args__() throws Exception {

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
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

    FacilityRow target = new FacilityRow();
    // given
    String county = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCounty(county);
    // then
    // e.g. : verify(mocked).called();
  }

}
