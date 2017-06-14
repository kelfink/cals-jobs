package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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

}
