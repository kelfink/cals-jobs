package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class FacilityRowTest {

  private FacilityRow target = new FacilityRow();

  @Before
  public void setup() {
    target = new FacilityRow();
  }

  @Test
  public void type() throws Exception {
    assertThat(FacilityRow.class, notNullValue());
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
  public void getType_Args__() throws Exception {
    String actual = target.getType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setType_Args__String() throws Exception {
    String type = null;
    target.setType(type);
  }

  @Test
  public void getName_Args__() throws Exception {
    String actual = target.getName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setName_Args__String() throws Exception {
    String name = null;
    target.setName(name);
  }

  @Test
  public void getLicenseeName_Args__() throws Exception {
    String actual = target.getLicenseeName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseeName_Args__String() throws Exception {
    String licenseeName = null;
    target.setLicenseeName(licenseeName);
  }

  @Test
  public void getAssignedWorker_Args__() throws Exception {
    String actual = target.getAssignedWorker();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAssignedWorker_Args__String() throws Exception {
    String assignedWorker = null;
    target.setAssignedWorker(assignedWorker);
  }

  @Test
  public void getDistrictOffice_Args__() throws Exception {
    String actual = target.getDistrictOffice();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDistrictOffice_Args__String() throws Exception {
    String districtOffice = null;
    target.setDistrictOffice(districtOffice);
  }

  @Test
  public void getLicenseNumber_Args__() throws Exception {
    String actual = target.getLicenseNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseNumber_Args__String() throws Exception {
    String licenseNumber = null;
    target.setLicenseNumber(licenseNumber);
  }

  @Test
  public void getLicenseStatus_Args__() throws Exception {
    String actual = target.getLicenseStatus();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseStatus_Args__String() throws Exception {
    String licenseStatus = null;
    target.setLicenseStatus(licenseStatus);
  }

  @Test
  public void getCapacity_Args__() throws Exception {
    String actual = target.getCapacity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCapacity_Args__String() throws Exception {
    String capacity = null;
    target.setCapacity(capacity);
  }

  @Test
  public void getLicenseEffectiveDate_Args__() throws Exception {
    Date actual = target.getLicenseEffectiveDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLicenseEffectiveDate_Args__Date() throws Exception {
    Date licenseEffectiveDate = mock(Date.class);
    target.setLicenseEffectiveDate(licenseEffectiveDate);
  }

  @Test
  public void getOriginalApplicationReceivedDate_Args__() throws Exception {
    Date actual = target.getOriginalApplicationReceivedDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOriginalApplicationReceivedDate_Args__Date() throws Exception {
    Date originalApplicationReceivedDate = mock(Date.class);
    target.setOriginalApplicationReceivedDate(originalApplicationReceivedDate);
  }

  @Test
  public void getLastVisitDate_Args__() throws Exception {
    Date actual = target.getLastVisitDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastVisitDate_Args__Date() throws Exception {
    Date lastVisitDate = mock(Date.class);
    target.setLastVisitDate(lastVisitDate);
  }

  @Test
  public void getLastVisitReason_Args__() throws Exception {
    String actual = target.getLastVisitReason();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastVisitReason_Args__String() throws Exception {
    String lastVisitReason = null;
    target.setLastVisitReason(lastVisitReason);
  }

  @Test
  public void getPrimaryPhoneNumber_Args__() throws Exception {
    String actual = target.getPrimaryPhoneNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPrimaryPhoneNumber_Args__String() throws Exception {
    String primaryPhoneNumber = null;
    target.setPrimaryPhoneNumber(primaryPhoneNumber);
  }

  @Test
  public void getAltPhoneNumber_Args__() throws Exception {
    String actual = target.getAltPhoneNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAltPhoneNumber_Args__String() throws Exception {
    String altPhoneNumber = null;
    target.setAltPhoneNumber(altPhoneNumber);
  }

  @Test
  public void getStateCodeType_Args__() throws Exception {
    String actual = target.getStateCodeType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStateCodeType_Args__String() throws Exception {
    String stateCodeType = null;
    target.setStateCodeType(stateCodeType);
  }

  @Test
  public void getZipCode_Args__() throws Exception {
    String actual = target.getZipCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipCode_Args__String() throws Exception {
    String zipCode = null;
    target.setZipCode(zipCode);
  }

  @Test
  public void getZipSuffixCode_Args__() throws Exception {
    String actual = target.getZipSuffixCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZipSuffixCode_Args__String() throws Exception {
    String zipSuffixCode = null;
    target.setZipSuffixCode(zipSuffixCode);
  }

  @Test
  public void getStreetAddress_Args__() throws Exception {
    String actual = target.getStreetAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStreetAddress_Args__String() throws Exception {
    String streetAddress = null;
    target.setStreetAddress(streetAddress);
  }

  @Test
  public void getCity_Args__() throws Exception {
    String actual = target.getCity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCity_Args__String() throws Exception {
    String city = null;
    target.setCity(city);
  }

  @Test
  public void getCounty_Args__() throws Exception {
    String actual = target.getCounty();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__String() throws Exception {
    String county = null;
    target.setCounty(county);
  }

}
