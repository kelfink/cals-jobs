package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class IntakeAllegationTest {

  private IntakeAllegation target;

  @Before
  public void setup() {
    target = new IntakeAllegation();
  }

  @Test
  public void type() throws Exception {
    assertThat(IntakeAllegation.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void toEsAllegation_Args__() throws Exception {
    target.setId("1234567");
    IntakeParticipant victim = new IntakeParticipant();
    victim.setId("8888888");
    target.setVictim(victim);
    ElasticSearchPersonAllegation actual = target.toEsAllegation();
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
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    String id = null;
    target.setId(id);
  }

  @Test
  public void getAllegationTypes_Args__() throws Exception {
    List<String> actual = target.getAllegationTypes();
    List<String> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationTypes_Args__List() throws Exception {
    List<String> allegationTypes = new ArrayList<String>();
    target.setAllegationTypes(allegationTypes);
  }

  @Test
  public void getAllegationDescription_Args__() throws Exception {
    String actual = target.getAllegationDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationDescription_Args__String() throws Exception {
    String allegationDescription = null;
    target.setAllegationDescription(allegationDescription);
  }

  @Test
  public void getDispositionDescription_Args__() throws Exception {
    String actual = target.getDispositionDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDispositionDescription_Args__String() throws Exception {
    String dispositionDescription = null;
    target.setDispositionDescription(dispositionDescription);
  }

  @Test
  public void getVictim_Args__() throws Exception {
    target.setId(PersonJobTester.DEFAULT_CLIENT_ID);
    IntakeParticipant actual = target.getVictim();
    IntakeParticipant expected = new IntakeParticipant();
    expected.setId(PersonJobTester.DEFAULT_CLIENT_ID);
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  public void setVictim_Args__IntakeParticipant() throws Exception {
    IntakeParticipant victim = mock(IntakeParticipant.class);
    target.setVictim(victim);
  }

  @Test
  public void getPerpetrator_Args__() throws Exception {
    target.setId(PersonJobTester.DEFAULT_CLIENT_ID);
    IntakeParticipant actual = target.getPerpetrator();
    IntakeParticipant expected = new IntakeParticipant();
    // expected.setId(PersonJobTester.DEFAULT_CLIENT_ID);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetrator_Args__IntakeParticipant() throws Exception {
    IntakeParticipant perpetrator = mock(IntakeParticipant.class);
    target.setPerpetrator(perpetrator);
  }

}
