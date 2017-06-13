package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.std.ApiPersonAware;

public class IntakeScreeningTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakeScreening.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IntakeScreening target = new IntakeScreening();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPrimaryKey();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void toEsScreening_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonScreening actual = target.toEsScreening();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonScreening expected = new ElasticSearchPersonScreening();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPersons_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ApiPersonAware[] actual = target.getPersons();
    // then
    // e.g. : verify(mocked).called();
    ApiPersonAware[] expected = new ApiPersonAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void getEsScreenings_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonScreening[] actual = target.getEsScreenings();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonScreening[] expected = {new ElasticSearchPersonScreening()};
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addParticipant_Args__IntakeParticipant() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    IntakeParticipant prt = mock(IntakeParticipant.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addParticipant(prt);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addAllegation_Args__IntakeAllegation() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    IntakeAllegation alg = mock(IntakeAllegation.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addAllegation(alg);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void addParticipantRole_Args__String__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    String partcId = null;
    String role = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addParticipantRole(partcId, role);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void findParticipantRoles_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    // given
    String partcId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Set<String> actual = target.findParticipantRoles(partcId);
    // then
    // e.g. : verify(mocked).called();
    Set<String> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

}
