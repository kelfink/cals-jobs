package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ReplicatedServiceProviderTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedServiceProvider.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedServiceProvider target = new ReplicatedServiceProvider();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedServiceProvider target = new ReplicatedServiceProvider();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedServiceProvider> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedServiceProvider> expected = ReplicatedServiceProvider.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedServiceProvider target = new ReplicatedServiceProvider();
    // given
    Map<Object, ReplicatedServiceProvider> map = new HashMap<Object, ReplicatedServiceProvider>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedServiceProvider actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedServiceProvider expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedServiceProvider target = new ReplicatedServiceProvider();
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
    ReplicatedServiceProvider target = new ReplicatedServiceProvider();
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
