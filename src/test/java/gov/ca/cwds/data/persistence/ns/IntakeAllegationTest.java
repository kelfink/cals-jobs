package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAllegation;

public class IntakeAllegationTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakeAllegation.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
    assertThat(target, notNullValue());
  }

  // @Test
  public void toEsAllegation_Args__() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
    target.setId("1234");
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonAllegation actual = target.toEsAllegation();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonAllegation expected = new ElasticSearchPersonAllegation();
    expected.setId("1234");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
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
