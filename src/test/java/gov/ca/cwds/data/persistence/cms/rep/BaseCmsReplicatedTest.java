package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class BaseCmsReplicatedTest {

  private static final String COMMON_ID = "abc1234oX4";

  private static final String COMMON_LEGACY_ID = "xyzABCDpY5";

  BaseCmsReplicated target;
  Supplier<String> supplyId;
  Supplier<String> supplyLegacyId;

  @Before
  public void setup() {
    supplyId = () -> COMMON_ID;
    supplyLegacyId = () -> COMMON_LEGACY_ID;
    target = new BaseCmsReplicated(supplyId, supplyLegacyId);
  }

  @Test
  public void type() throws Exception {
    assertThat(BaseCmsReplicated.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    BaseCmsReplicated target = new BaseCmsReplicated(supplyId, supplyLegacyId);
    assertThat(target, notNullValue());
  }

  @Test
  public void getId_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getId();
    // then
    // e.g. : verify(mocked).called();
    String expected = COMMON_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = COMMON_LEGACY_ID;
    assertThat(actual, is(equalTo(expected)));
  }

}
