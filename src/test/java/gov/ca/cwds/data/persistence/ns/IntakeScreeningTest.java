package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.Goddard;

public class IntakeScreeningTest extends Goddard {

  IntakeScreening target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new IntakeScreening();
  }

  @Test
  public void type() throws Exception {
    assertThat(IntakeScreening.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation2() throws Exception {
    target = new IntakeScreening(DEFAULT_CLIENT_ID);
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    String actual = target.getPrimaryKey();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toEsScreening_Args__() throws Exception {
    target.setId(DEFAULT_CLIENT_ID);

    IntakeAllegation alg = new IntakeAllegation();
    alg.setId(DEFAULT_CLIENT_ID);
    List<String> types = new ArrayList<>();
    types.add("abuse");
    types.add("neglect");
    types.add("greed");
    alg.setAllegationTypes(types);
    target.addAllegation(alg);

    IntakeParticipant p = new IntakeParticipant();
    p.setId(DEFAULT_CLIENT_ID);
    target.addParticipant(p);

    ElasticSearchPersonScreening actual = target.toEsScreening();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getPersons_Args__() throws Exception {
    ApiPersonAware[] actual = target.getPersons();
    ApiPersonAware[] expected = new ApiPersonAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getEsScreenings_Args__() throws Exception {
    ElasticSearchPersonScreening[] actual = target.getEsScreenings();
    ElasticSearchPersonScreening[] expected = {new ElasticSearchPersonScreening()};
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addParticipant_Args__IntakeParticipant() throws Exception {
    IntakeParticipant prt = mock(IntakeParticipant.class);
    target.addParticipant(prt);
  }

  @Test
  public void addAllegation_Args__IntakeAllegation() throws Exception {
    IntakeAllegation alg = mock(IntakeAllegation.class);
    target.addAllegation(alg);
  }

  @Test
  public void addParticipantRole_Args__String__String() throws Exception {
    String partcId = DEFAULT_CLIENT_ID;
    String role = "office_admin";
    target.addParticipantRole(partcId, role);

    role = "tri_county_officer";
    target.addParticipantRole(partcId, role);
  }

  @Test
  public void findParticipantRoles_Args__String() throws Exception {
    String partcId = null;
    Set<String> actual = target.findParticipantRoles(partcId);
    Set<String> expected = new HashSet<>();
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
  public void getReference_Args__() throws Exception {
    String actual = target.getReference();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReference_Args__String() throws Exception {
    String reference = null;
    target.setReference(reference);
  }

  @Test
  public void getStartedAt_Args__() throws Exception {
    Date actual = target.getStartedAt();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartedAt_Args__Date() throws Exception {
    Date startedAt = mock(Date.class);
    target.setStartedAt(startedAt);
  }

  @Test
  public void getEndedAt_Args__() throws Exception {
    Date actual = target.getEndedAt();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndedAt_Args__Date() throws Exception {
    Date endedAt = mock(Date.class);
    target.setEndedAt(endedAt);
  }

  @Test
  public void getIncidentDate_Args__() throws Exception {
    String actual = target.getIncidentDate();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentDate_Args__String() throws Exception {
    String incidentDate = null;
    target.setIncidentDate(incidentDate);
  }

  @Test
  public void getLocationType_Args__() throws Exception {
    String actual = target.getLocationType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLocationType_Args__String() throws Exception {
    String locationType = null;
    target.setLocationType(locationType);
  }

  @Test
  public void getCommunicationMethod_Args__() throws Exception {
    String actual = target.getCommunicationMethod();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCommunicationMethod_Args__String() throws Exception {
    String communicationMethod = null;
    target.setCommunicationMethod(communicationMethod);
  }

  @Test
  public void getScreeningName_Args__() throws Exception {
    String actual = target.getScreeningName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningName_Args__String() throws Exception {
    String screeningName = null;
    target.setScreeningName(screeningName);
  }

  @Test
  public void getScreeningDecision_Args__() throws Exception {
    String actual = target.getScreeningDecision();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecision_Args__String() throws Exception {
    String screeningDecision = null;
    target.setScreeningDecision(screeningDecision);
  }

  @Test
  public void getIncidentCounty_Args__() throws Exception {
    String actual = target.getIncidentCounty();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setIncidentCounty_Args__String() throws Exception {
    String incidentCounty = null;
    target.setIncidentCounty(incidentCounty);
  }

  @Test
  public void getReportNarrative_Args__() throws Exception {
    String actual = target.getReportNarrative();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReportNarrative_Args__String() throws Exception {
    String reportNarrative = null;
    target.setReportNarrative(reportNarrative);
  }

  @Test
  public void getAssignee_Args__() throws Exception {
    String actual = target.getAssignee();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAssignee_Args__String() throws Exception {
    String assignee = null;
    target.setAssignee(assignee);
  }

  @Test
  public void getAdditionalInformation_Args__() throws Exception {
    String actual = target.getAdditionalInformation();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdditionalInformation_Args__String() throws Exception {
    String additionalInformation = null;
    target.setAdditionalInformation(additionalInformation);
  }

  @Test
  public void getScreeningDecisionDetail_Args__() throws Exception {
    String actual = target.getScreeningDecisionDetail();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecisionDetail_Args__String() throws Exception {
    String screeningDecisionDetail = null;
    target.setScreeningDecisionDetail(screeningDecisionDetail);
  }

  @Test
  public void getSocialWorker_Args__() throws Exception {
    IntakeParticipant actual = target.getSocialWorker();
    IntakeParticipant expected = new IntakeParticipant();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSocialWorker_Args__IntakeParticipant() throws Exception {
    IntakeParticipant assignedSocialWorker = mock(IntakeParticipant.class);
    target.setSocialWorker(assignedSocialWorker);
  }

  @Test
  public void getParticipants_Args__() throws Exception {
    Map<String, IntakeParticipant> actual = target.getParticipants();
    Map<String, IntakeParticipant> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAllegations_Args__() throws Exception {
    Map<String, IntakeAllegation> actual = target.getAllegations();
    Map<String, IntakeAllegation> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReporter_Args__() throws Exception {
    target.setId("1234");
    IntakeParticipant actual = target.getReporter();
    IntakeParticipant expected = new IntakeParticipant();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setReporter_Args__IntakeParticipant() throws Exception {
    IntakeParticipant reporter = mock(IntakeParticipant.class);
    target.setReporter(reporter);
  }

  @Test
  public void getParticipantRoles_Args__() throws Exception {
    Map<String, Set<String>> actual = target.getParticipantRoles();
    Map<String, Set<String>> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReferralId_Args__() throws Exception {
    String actual = target.getReferralId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {
    String referralId = null;
    target.setReferralId(referralId);
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    int expected = 0;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    String actual = target.toString();
    assertThat(actual, is(notNullValue()));
  }

}
