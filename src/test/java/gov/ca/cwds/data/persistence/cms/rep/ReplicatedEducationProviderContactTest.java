package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReplicatedEducationProviderContactTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedEducationProviderContact.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedEducationProviderContact> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedEducationProviderContact> expected = ReplicatedEducationProviderContact.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    // given
    Map<Object, ReplicatedEducationProviderContact> map =
        new HashMap<Object, ReplicatedEducationProviderContact>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedEducationProviderContact actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedEducationProviderContact expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getNormalizationGroupKey();
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
