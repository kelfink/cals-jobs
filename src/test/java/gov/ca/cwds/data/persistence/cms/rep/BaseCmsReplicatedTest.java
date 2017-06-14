package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  public void testReplicationOperation() throws Exception {
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
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
