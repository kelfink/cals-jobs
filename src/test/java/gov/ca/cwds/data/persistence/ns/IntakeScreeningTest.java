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

import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
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
    String actual = target.getPrimaryKey();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void toEsScreening_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    ElasticSearchPersonScreening actual = target.toEsScreening();
    ElasticSearchPersonScreening expected = new ElasticSearchPersonScreening();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPersons_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    ApiPersonAware[] actual = target.getPersons();
    ApiPersonAware[] expected = new ApiPersonAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void getEsScreenings_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    ElasticSearchPersonScreening[] actual = target.getEsScreenings();
    ElasticSearchPersonScreening[] expected = {new ElasticSearchPersonScreening()};
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addParticipant_Args__IntakeParticipant() throws Exception {
    IntakeScreening target = new IntakeScreening();
    IntakeParticipant prt = mock(IntakeParticipant.class);
    target.addParticipant(prt);
  }

  @Test
  public void addAllegation_Args__IntakeAllegation() throws Exception {
    IntakeScreening target = new IntakeScreening();
    IntakeAllegation alg = mock(IntakeAllegation.class);
    target.addAllegation(alg);
  }

  @Test
  public void addParticipantRole_Args__String__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String partcId = null;
    String role = null;
    target.addParticipantRole(partcId, role);
  }

  @Test
  public void findParticipantRoles_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String partcId = null;
    Set<String> actual = target.findParticipantRoles(partcId);
    Set<String> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String id = null;
    target.setId(id);
  }

  @Test
  public void getReference_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getReference();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReference_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String reference = null;
    target.setReference(reference);
  }

  @Test
  public void getStartedAt_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Date actual = target.getStartedAt();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartedAt_Args__Date() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Date startedAt = mock(Date.class);
    target.setStartedAt(startedAt);
  }

  @Test
  public void getEndedAt_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Date actual = target.getEndedAt();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndedAt_Args__Date() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Date endedAt = mock(Date.class);
    target.setEndedAt(endedAt);
  }

  @Test
  public void getIncidentDate_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getIncidentDate();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentDate_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String incidentDate = null;
    target.setIncidentDate(incidentDate);
  }

  @Test
  public void getLocationType_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getLocationType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLocationType_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String locationType = null;
    target.setLocationType(locationType);
  }

  @Test
  public void getCommunicationMethod_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getCommunicationMethod();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCommunicationMethod_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String communicationMethod = null;
    target.setCommunicationMethod(communicationMethod);
  }

  @Test
  public void getScreeningName_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getScreeningName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningName_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String screeningName = null;
    target.setScreeningName(screeningName);
  }

  @Test
  public void getScreeningDecision_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getScreeningDecision();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecision_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String screeningDecision = null;
    target.setScreeningDecision(screeningDecision);
  }

  @Test
  public void getIncidentCounty_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getIncidentCounty();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentCounty_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String incidentCounty = null;
    target.setIncidentCounty(incidentCounty);
  }

  @Test
  public void getReportNarrative_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getReportNarrative();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReportNarrative_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String reportNarrative = null;
    target.setReportNarrative(reportNarrative);
  }

  @Test
  public void getAssignee_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getAssignee();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAssignee_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String assignee = null;
    target.setAssignee(assignee);
  }

  @Test
  public void getAdditionalInformation_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getAdditionalInformation();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdditionalInformation_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String additionalInformation = null;
    target.setAdditionalInformation(additionalInformation);
  }

  @Test
  public void getScreeningDecisionDetail_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getScreeningDecisionDetail();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecisionDetail_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String screeningDecisionDetail = null;
    target.setScreeningDecisionDetail(screeningDecisionDetail);
  }

  @Test
  @Ignore
  public void getSocialWorker_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    IntakeParticipant actual = target.getSocialWorker();
    IntakeParticipant expected = new IntakeParticipant();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSocialWorker_Args__IntakeParticipant() throws Exception {
    IntakeScreening target = new IntakeScreening();
    IntakeParticipant assignedSocialWorker = mock(IntakeParticipant.class);
    target.setSocialWorker(assignedSocialWorker);
  }

  @Test
  public void getParticipants_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Map<String, IntakeParticipant> actual = target.getParticipants();
    Map<String, IntakeParticipant> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAllegations_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Map<String, IntakeAllegation> actual = target.getAllegations();
    Map<String, IntakeAllegation> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void getReporter_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    target.setId("1234");
    IntakeParticipant actual = target.getReporter();
    IntakeParticipant expected = new IntakeParticipant();
    expected.setId("1234");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporter_Args__IntakeParticipant() throws Exception {
    IntakeScreening target = new IntakeScreening();
    IntakeParticipant reporter = mock(IntakeParticipant.class);
    target.setReporter(reporter);
  }

  @Test
  public void getParticipantRoles_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Map<String, Set<String>> actual = target.getParticipantRoles();
    Map<String, Set<String>> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReferralId_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.getReferralId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String referralId = null;
    target.setReferralId(referralId);
  }

  @Test
  @Ignore
  public void hashCode_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    int actual = target.hashCode();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    IntakeScreening target = new IntakeScreening();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    IntakeScreening target = new IntakeScreening();
    String actual = target.toString();
    assertThat(actual, is(notNullValue()));
  }

}
