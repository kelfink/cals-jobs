package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

public class EsPersonReferralTest {

  @Test
  public void type() throws Exception {
    assertThat(EsPersonReferral.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Class<ReplicatedPersonReferrals> actual = target.getNormalizationClass();
    Class<ReplicatedPersonReferrals> expected = ReplicatedPersonReferrals.class;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void normalize_Args__Map() throws Exception {
  // EsPersonReferral target = new EsPersonReferral();
  // final Map<Object, ReplicatedPersonReferrals> map =
  // new HashMap<Object, ReplicatedPersonReferrals>();
  // final ReplicatedPersonReferrals actual = target.normalize(map);
  // final ReplicatedPersonReferrals expected = new ReplicatedPersonReferrals();
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Object actual = target.getNormalizationGroupKey();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void hashCode_Args__() throws Exception {
  // EsPersonReferral target = new EsPersonReferral();
  // // given
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // int actual = target.hashCode();
  // // then
  // // e.g. : verify(mocked).called();
  // int expected = 0;
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void equals_Args__Object() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.toString();
    String expected = new EsPersonReferral().toString();
    assertThat(actual, is(equalTo(expected)));
  }

}
