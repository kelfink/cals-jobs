package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;

public class ReplicatedAddressTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    assertThat(target, notNullValue());
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    // then
    // e.g. : verify(mocked).called();
    // ElasticSearchLegacyDescriptor expected = null;
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getReplicationOperation_Args__() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    CmsReplicationOperation actual = target.getReplicationOperation();
    // then
    // e.g. : verify(mocked).called();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReplicationDate_Args__() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getReplicationDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    CmsReplicationOperation replicationOperation = CmsReplicationOperation.I;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReplicationOperation(replicationOperation);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setReplicationDate_Args__Date() throws Exception {
    ReplicatedAddress target = new ReplicatedAddress();
    // given
    Date replicationDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReplicationDate(replicationDate);
    // then
    // e.g. : verify(mocked).called();
  }

}
