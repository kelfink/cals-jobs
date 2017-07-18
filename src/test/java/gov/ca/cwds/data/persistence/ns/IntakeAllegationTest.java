package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

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

  @Test
  public void toEsAllegation_Args__() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
    target.setId("1234567");
    IntakeParticipant victim = new IntakeParticipant();
    victim.setId("8888888");
    target.setVictim(victim);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonAllegation actual = target.toEsAllegation();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonAllegation expected = new ElasticSearchPersonAllegation();
    expected.setId("1234567");
    expected.setLegacyId("1234567");
    expected.setLegacyDescriptor(
        ElasticTransformer.createLegacyDescriptor("1234567", null, LegacyTable.ALLEGATION));

    expected.setVictimId("8888888");
    expected.getVictim().setId("8888888");

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

  @Test
  public void getId_Args__() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationTypes_Args__() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<String> actual = target.getAllegationTypes();
    // then
    // e.g. : verify(mocked).called();
    List<String> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationTypes_Args__List() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    List<String> allegationTypes = new ArrayList<String>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationTypes(allegationTypes);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationDescription_Args__() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAllegationDescription();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationDescription_Args__String() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    String allegationDescription = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationDescription(allegationDescription);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getDispositionDescription_Args__() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getDispositionDescription();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDispositionDescription_Args__String() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    String dispositionDescription = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setDispositionDescription(dispositionDescription);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void getVictim_Args__() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
    target.setId("abc1234");
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.getVictim();
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = new IntakeParticipant();
    expected.setId("abc1234");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictim_Args__IntakeParticipant() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    IntakeParticipant victim = mock(IntakeParticipant.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setVictim(victim);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void getPerpetrator_Args__() throws Exception {
    IntakeAllegation target = new IntakeAllegation();
    target.setId("abc1234");
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.getPerpetrator();
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = new IntakeParticipant();
    // expected.setId("abc1234");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetrator_Args__IntakeParticipant() throws Exception {

    IntakeAllegation target = new IntakeAllegation();
    // given
    IntakeParticipant perpetrator = mock(IntakeParticipant.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPerpetrator(perpetrator);
    // then
    // e.g. : verify(mocked).called();
  }

}
