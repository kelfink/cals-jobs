package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

  @Test
  public void getId_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
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

    IntakeScreening target = new IntakeScreening();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReference_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReference();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReference_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String reference = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReference(reference);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStartedAt_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getStartedAt();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartedAt_Args__Date() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    Date startedAt = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setStartedAt(startedAt);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getEndedAt_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getEndedAt();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndedAt_Args__Date() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    Date endedAt = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setEndedAt(endedAt);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getIncidentDate_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getIncidentDate();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentDate_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String incidentDate = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setIncidentDate(incidentDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLocationType_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLocationType();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLocationType_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String locationType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLocationType(locationType);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCommunicationMethod_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCommunicationMethod();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCommunicationMethod_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String communicationMethod = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCommunicationMethod(communicationMethod);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getScreeningName_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getScreeningName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningName_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String screeningName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreeningName(screeningName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getScreeningDecision_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getScreeningDecision();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecision_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String screeningDecision = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreeningDecision(screeningDecision);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getIncidentCounty_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getIncidentCounty();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentCounty_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String incidentCounty = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setIncidentCounty(incidentCounty);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReportNarrative_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReportNarrative();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReportNarrative_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String reportNarrative = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReportNarrative(reportNarrative);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAssignee_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAssignee();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAssignee_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String assignee = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAssignee(assignee);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAdditionalInformation_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAdditionalInformation();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdditionalInformation_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String additionalInformation = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAdditionalInformation(additionalInformation);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getScreeningDecisionDetail_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getScreeningDecisionDetail();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecisionDetail_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String screeningDecisionDetail = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreeningDecisionDetail(screeningDecisionDetail);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void getSocialWorker_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.getSocialWorker();
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = new IntakeParticipant();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSocialWorker_Args__IntakeParticipant() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    IntakeParticipant assignedSocialWorker = mock(IntakeParticipant.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setSocialWorker(assignedSocialWorker);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParticipants_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Map<String, IntakeParticipant> actual = target.getParticipants();
    // then
    // e.g. : verify(mocked).called();
    Map<String, IntakeParticipant> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAllegations_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Map<String, IntakeAllegation> actual = target.getAllegations();
    // then
    // e.g. : verify(mocked).called();
    Map<String, IntakeAllegation> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void getReporter_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    target.setId("1234");
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeParticipant actual = target.getReporter();
    // then
    // e.g. : verify(mocked).called();
    IntakeParticipant expected = new IntakeParticipant();
    expected.setId("1234");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporter_Args__IntakeParticipant() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    IntakeParticipant reporter = mock(IntakeParticipant.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReporter(reporter);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParticipantRoles_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Map<String, Set<String>> actual = target.getParticipantRoles();
    // then
    // e.g. : verify(mocked).called();
    Map<String, Set<String>> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReferralId_Args__() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReferralId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {

    IntakeScreening target = new IntakeScreening();
    // given
    String referralId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReferralId(referralId);
    // then
    // e.g. : verify(mocked).called();
  }

}
