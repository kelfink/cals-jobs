package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;

public class ReplicatedAddressTest {

  private ReplicatedAddress target = new ReplicatedAddress();

  @Before
  public void setup() {
    target = new ReplicatedAddress();
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    String actual = target.getLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReplicationDate_Args__() throws Exception {
    Date actual = target.getReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation replicationOperation = CmsReplicationOperation.I;
    target.setReplicationOperation(replicationOperation);
  }

  @Test
  public void setReplicationDate_Args__Date() throws Exception {
    Date replicationDate = mock(Date.class);
    target.setReplicationDate(replicationDate);
  }

}
