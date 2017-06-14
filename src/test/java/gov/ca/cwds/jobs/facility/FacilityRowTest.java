package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

public class FacilityRowTest {

  @Test
  public void type() throws Exception {
    assertThat(FacilityRow.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    FacilityRow target = new FacilityRow();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    FacilityRow target = new FacilityRow();
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
