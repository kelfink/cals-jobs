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

public class ReplicatedAttorneyTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedAttorney.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedAttorney> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedAttorney> expected = ReplicatedAttorney.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    // given
    Map<Object, ReplicatedAttorney> map = new HashMap<Object, ReplicatedAttorney>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedAttorney actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedAttorney expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
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
    ReplicatedAttorney target = new ReplicatedAttorney();
    // target.id = "12345";

    String actual = target.getLegacyId();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

}
