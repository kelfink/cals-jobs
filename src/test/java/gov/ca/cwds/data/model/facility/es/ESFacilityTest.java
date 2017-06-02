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

}
