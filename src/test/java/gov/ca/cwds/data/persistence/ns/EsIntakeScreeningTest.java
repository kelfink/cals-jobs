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

  // @Test
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

  // @Test
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

  // @Test
  public void fillScreening_Args__IntakeScreening() throws Exception {
    EsIntakeScreening target = new EsIntakeScreening();
    // given
    IntakeScreening s = new IntakeScreening();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeScreening actual = target.fillScreening(s);
    // then
    // e.g. : verify(mocked).called();
    IntakeScreening expected = new IntakeScreening();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void fillScreening_Args__() throws Exception {
    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    IntakeScreening actual = target.fillScreening();
    // then
    // e.g. : verify(mocked).called();
    IntakeScreening expected = new IntakeScreening();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
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
    int expected = 164947504;
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

  // @Test
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

  @Test
  public void getThisParticipantId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisParticipantId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisParticipantId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String thisParticipantId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisParticipantId(thisParticipantId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getThisLegacyId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String thisLegacyId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisLegacyId(thisLegacyId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getScreeningId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getScreeningId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String screeningId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreeningId(screeningId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReference_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    Date endedAt = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setEndedAt(endedAt);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReferralId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String referralId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReferralId(referralId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getIncidentDate_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
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

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String screeningDecisionDetail = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setScreeningDecisionDetail(screeningDecisionDetail);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getOtherParticipantId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getOtherParticipantId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOtherParticipantId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String otherParticipantId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setOtherParticipantId(otherParticipantId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getOtherLegacyId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getOtherLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOtherLegacyId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String otherLegacyId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setOtherLegacyId(otherLegacyId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getBirthDt_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getBirthDt();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBirthDt_Args__Date() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    Date birthDt = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setBirthDt(birthDt);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getFirstName_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFirstName_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String firstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFirstName(firstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLastName_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastName_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String lastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastName(lastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getGender_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getGender();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setGender_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String gender = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setGender(gender);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getSsn_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getSsn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSsn_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String ssn = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setSsn(ssn);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRoles_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String[] actual = target.getRoles();
    // then
    // e.g. : verify(mocked).called();
    String[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRoles_Args__StringArray() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String[] roles = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRoles(roles);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void isFlgReporter_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.isFlgReporter();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgReporter_Args__boolean() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    boolean flgReporter = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFlgReporter(flgReporter);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void isFlgPerpetrator_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.isFlgPerpetrator();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgPerpetrator_Args__boolean() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    boolean flgPerpetrator = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFlgPerpetrator(flgPerpetrator);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void isFlgVictim_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.isFlgVictim();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgVictim_Args__boolean() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    boolean flgVictim = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFlgVictim(flgVictim);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAllegationId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String allegationId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationId(allegationId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationTypes_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAllegationTypes();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationTypes_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String allegationTypes = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationTypes(allegationTypes);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAddressId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAddressId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddressId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String addressId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAddressId(addressId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAddressType_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getAddressType();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddressType_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String addressType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAddressType(addressType);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStreetAddress_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getStreetAddress();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStreetAddress_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String streetAddress = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setStreetAddress(streetAddress);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCity_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCity();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCity_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String city = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCity(city);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getState_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getState();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setState_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String state = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setState(state);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getZip_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getZip();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZip_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String zip = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setZip(zip);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPhoneNumberId_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneNumberId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneNumberId_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String phoneNumberId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhoneNumberId(phoneNumberId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPhoneNumber_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneNumber();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneNumber_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String phoneNumber = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhoneNumber(phoneNumber);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPhoneType_Args__() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPhoneType();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneType_Args__String() throws Exception {

    EsIntakeScreening target = new EsIntakeScreening();
    // given
    String phoneType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPhoneType(phoneType);
    // then
    // e.g. : verify(mocked).called();
  }

}
