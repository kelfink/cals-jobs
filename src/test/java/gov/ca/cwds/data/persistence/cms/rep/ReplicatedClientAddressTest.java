package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ReplicatedClientAddressTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClientAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedClientAddress target = new ReplicatedClientAddress();
    assertThat(target, notNullValue());
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    ReplicatedClientAddress target = new ReplicatedClientAddress();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Set<ReplicatedAddress> actual = target.getAddresses();
    // then
    // e.g. : verify(mocked).called();
    Set<ReplicatedAddress> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddresses_Args__Set() throws Exception {
    ReplicatedClientAddress target = new ReplicatedClientAddress();
    // given
    Set<ReplicatedAddress> addresses = mock(Set.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAddresses(addresses);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addAddress_Args__ReplicatedAddress() throws Exception {
    ReplicatedClientAddress target = new ReplicatedClientAddress();
    // given
    ReplicatedAddress address = mock(ReplicatedAddress.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addAddress(address);
    // then
    // e.g. : verify(mocked).called();
  }

}
