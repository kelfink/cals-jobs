package gov.ca.cwds.data.model.facility.es;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ESFacilityAddressTest {

  @Test
  public void type() throws Exception {
    assertThat(ESFacilityAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ESFacilityAddress target = new ESFacilityAddress();
    assertThat(target, notNullValue());
  }

}
