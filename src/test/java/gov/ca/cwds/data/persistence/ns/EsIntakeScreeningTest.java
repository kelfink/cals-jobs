package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EsIntakeScreeningTest {

  @Test
  public void type() throws Exception {
    assertThat(EsIntakeScreening.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<IntakeParticipant> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<IntakeParticipant> expected = IntakeParticipant.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void fillParticipant_Args__IntakeParticipant__boolean() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    IntakeParticipant p = mock(IntakeParticipant.class);
    boolean isOther = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.fillParticipant(p, isOther);
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void fillParticipant_Args__boolean() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    boolean isOther = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.fillParticipant(isOther);
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void fillScreening_Args__IntakeScreening() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    IntakeScreening s = mock(IntakeScreening.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeScreening actual = target.fillScreening(s);
    // then
    // e.g. : verify(mocked).called();
    IntakeScreening expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void fillScreening_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeScreening actual = target.fillScreening();
    // then
    // e.g. : verify(mocked).called();
    IntakeScreening expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    Map<Object, IntakeParticipant> map = new HashMap<Object, IntakeParticipant>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
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
  public void getPrimaryKey_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Serializable actual = target.getPrimaryKey();
    // then
    // e.g. : verify(mocked).called();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    Object obj = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.equals(obj);
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChange_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getLastChange();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    Date lastChange = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastChange(lastChange);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void toString_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.toString();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
