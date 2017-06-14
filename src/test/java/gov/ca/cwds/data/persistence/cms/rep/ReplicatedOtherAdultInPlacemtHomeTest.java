package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReplicatedOtherAdultInPlacemtHomeTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedOtherAdultInPlacemtHome.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedOtherAdultInPlacemtHome> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedOtherAdultInPlacemtHome> expected = ReplicatedOtherAdultInPlacemtHome.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    // given
    Map<Object, ReplicatedOtherAdultInPlacemtHome> map =
        new HashMap<Object, ReplicatedOtherAdultInPlacemtHome>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedOtherAdultInPlacemtHome actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedOtherAdultInPlacemtHome expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getNormalizationGroupKey();
    // then
    // e.g. : verify(mocked).called();
    Object expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

}
