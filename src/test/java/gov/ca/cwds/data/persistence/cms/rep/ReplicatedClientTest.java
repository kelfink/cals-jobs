package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiPhoneAware;

public class ReplicatedClientTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClient.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    assertThat(target, notNullValue());
  }

  @Test
  public void getClientAddresses_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Set<ReplicatedClientAddress> actual = target.getClientAddresses();
    // then
    // e.g. : verify(mocked).called();
    Set<ReplicatedClientAddress> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientAddresses_Args__Set() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    Set<ReplicatedClientAddress> clientAddresses = mock(Set.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setClientAddresses(clientAddresses);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addClientAddress_Args__ReplicatedClientAddress() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    ReplicatedClientAddress clientAddress = mock(ReplicatedClientAddress.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addClientAddress(clientAddress);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
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
  public void getPhones_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
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
  public void getLegacyId_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void toString_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.toString();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = -73508643;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
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
