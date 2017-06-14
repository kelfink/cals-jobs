package gov.ca.cwds.data.model.facility.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;

import org.junit.Test;

public class ESFacilityTest {

  @Test
  public void type() throws Exception {
    assertThat(ESFacility.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ESFacility target = new ESFacility();
    assertThat(target, notNullValue());
  }

  @Test
  public void getLicenseEffectiveDate_Args__() throws Exception {
    ESFacility target = new ESFacility();
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
    ESFacility target = new ESFacility();
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
    ESFacility target = new ESFacility();
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
    ESFacility target = new ESFacility();
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
    ESFacility target = new ESFacility();
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
    ESFacility target = new ESFacility();
    // given
    Date lastVisitDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastVisitDate(lastVisitDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ESFacility target = new ESFacility();
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
  public void getId_Args__() throws Exception {

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getType_Args__() throws Exception {

    ESFacility target = new ESFacility();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getType();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setType_Args__String() throws Exception {

    ESFacility target = new ESFacility();
    // given
    String type = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setType(type);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getName_Args__() throws Exception {

    ESFacility target = new ESFacility();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setName_Args__String() throws Exception {

    ESFacility target = new ESFacility();
    // given
    String name = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setName(name);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLicenseeName_Args__() throws Exception {

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
    // given
    String capacity = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCapacity(capacity);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLastVisitReason_Args__() throws Exception {

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
    // given
    String lastVisitReason = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastVisitReason(lastVisitReason);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCounty_Args__() throws Exception {

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
    // given
    String county = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCounty(county);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPrimaryPhoneNumber_Args__() throws Exception {

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
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

    ESFacility target = new ESFacility();
    // given
    String altPhoneNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAltPhoneNumber(altPhoneNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAddress_Args__() throws Exception {

    ESFacility target = new ESFacility();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ESFacilityAddress actual = target.getAddress();
    // then
    // e.g. : verify(mocked).called();
    ESFacilityAddress expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddress_Args__ESFacilityAddress() throws Exception {

    ESFacility target = new ESFacility();
    // given
    ESFacilityAddress address = mock(ESFacilityAddress.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAddress(address);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void hashCode_Args__() throws Exception {

    ESFacility target = new ESFacility();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = -997072353;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {

    ESFacility target = new ESFacility();
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
