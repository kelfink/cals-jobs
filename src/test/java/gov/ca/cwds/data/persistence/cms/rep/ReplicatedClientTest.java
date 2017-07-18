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

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
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
    Set<ReplicatedClientAddress> actual = target.getClientAddresses();
    Set<ReplicatedClientAddress> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientAddresses_Args__Set() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    Set<ReplicatedClientAddress> clientAddresses = mock(Set.class);
    target.setClientAddresses(clientAddresses);
  }

  @Test
  public void addClientAddress_Args__ReplicatedClientAddress() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    ReplicatedClientAddress clientAddress = mock(ReplicatedClientAddress.class);
    target.addClientAddress(clientAddress);
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    ApiAddressAware[] actual = target.getAddresses();
    ApiAddressAware[] expected = new ApiAddressAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhones_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    ApiPhoneAware[] actual = target.getPhones();
    ApiPhoneAware[] expected = new ApiPhoneAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    String actual = target.getLegacyId();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void toString_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    String actual = target.toString();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    int actual = target.hashCode();
    int expected = -73508643;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ReplicatedClient target = new ReplicatedClient();
    Date lastUpdatedTime = new Date();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
