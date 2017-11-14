package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.std.ApiPhoneAware.PhoneType;
import gov.ca.cwds.jobs.Goddard;

public class EsIntakeScreeningTest extends Goddard {
  public static final String DEFAULT_SCREENING_ID = "scr1234567";

  private static final class TestEsIntakeScreening extends EsIntakeScreening {
    @Override
    public void addParticipantRoles(IntakeScreening s, IntakeParticipant otherPartc) {
      super.addParticipantRoles(s, otherPartc);
    }

    @Override
    public IntakeParticipant handleOtherParticipant(IntakeScreening s) {
      return super.handleOtherParticipant(s);
    }

    @Override
    public IntakeAllegation makeAllegation(IntakeScreening s) {
      return super.makeAllegation(s);
    }

    @Override
    public void handleAllegations(String thisPartcId, IntakeScreening s,
        IntakeParticipant otherPartc) {
      super.handleAllegations(thisPartcId, s, otherPartc);
    }

  }

  EsIntakeScreening target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new TestEsIntakeScreening();
    target.setThisLegacyId(DEFAULT_CLIENT_ID);
    target.setThisParticipantId("1");
    target.setScreeningId(DEFAULT_SCREENING_ID);
  }

  @Test
  public void type() throws Exception {
    assertThat(EsIntakeScreening.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<IntakeParticipant> actual = target.getNormalizationClass();
    Class<IntakeParticipant> expected = IntakeParticipant.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void fillParticipant_Args__IntakeParticipant__boolean() throws Exception {
    final IntakeParticipant p = new IntakeParticipant();
    p.setId("1");
    p.setLegacyId(Goddard.DEFAULT_CLIENT_ID);
    boolean isOther = false;
    final IntakeParticipant actual = target.fillParticipant(p, isOther);
    final IntakeParticipant expected = new IntakeParticipant();
    expected.setId("1");
    expected.setLegacyId(Goddard.DEFAULT_CLIENT_ID);
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  public void fillParticipant_Args__boolean() throws Exception {
    boolean isOther = false;
    IntakeParticipant actual = target.fillParticipant(isOther);
    assertThat(actual, notNullValue());
  }

  @Test
  public void fillScreening_Args__IntakeScreening() throws Exception {
    IntakeScreening s = new IntakeScreening();
    target.setStartedAt(new Date());
    target.setEndedAt(new Date());
    IntakeScreening actual = target.fillScreening(s);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void fillScreening_Args__() throws Exception {
    IntakeScreening actual = target.fillScreening();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    Map<Object, IntakeParticipant> map = new HashMap<Object, IntakeParticipant>();
    IntakeParticipant p = new IntakeParticipant();
    p.setId(DEFAULT_CLIENT_ID);
    p.setLegacyId(DEFAULT_CLIENT_ID);
    map.put(DEFAULT_SCREENING_ID, p);
    IntakeScreening s = new IntakeScreening();
    s.setId(DEFAULT_SCREENING_ID);
    s.setEndedAt(new Date());
    s.setStartedAt(new Date());
    p.addScreening(s);
    IntakeAllegation alg = new IntakeAllegation();
    alg.setId(DEFAULT_CLIENT_ID);
    List<String> types = new ArrayList<>();
    types.add("abuse");
    types.add("neglect");
    types.add("greed");
    alg.setAllegationTypes(types);
    s.addAllegation(alg);
    target.setOtherParticipantId("xyz1234567");
    target.setAllegationId(DEFAULT_SCREENING_ID);
    target.setAddressId("2");
    target.setAddressType("Home");
    target.setZip("12345");
    target.setCity("Nowhere");
    target.setStreetAddress("1523 Main Street");
    target.setPhoneNumberId("3");
    target.setPhoneNumber("9164408791");
    target.setPhoneType("Home");
    target.setFirstName("Joseph");
    target.setLastName("Muller");
    target.setFlgPerpetrator(true);
    target.setFlgVictim(true);
    target.setFlgReporter(true);
    IntakeParticipant actual = target.normalize(map);
    assertThat(actual, notNullValue());
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    Object actual = target.getNormalizationGroupKey();
    Object expected = Goddard.DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChange_Args__() throws Exception {
    Date actual = target.getLastChange();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {
    Date lastChange = mock(Date.class);
    target.setLastChange(lastChange);
  }

  @Test
  public void toString_Args__() throws Exception {
    final String actual = target.toString();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getThisParticipantId_Args__() throws Exception {
    final String actual = target.getThisParticipantId();
    String expected = "1";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisParticipantId_Args__String() throws Exception {
    String thisParticipantId = null;
    target.setThisParticipantId(thisParticipantId);
  }

  @Test
  public void getThisLegacyId_Args__() throws Exception {
    final String actual = target.getThisLegacyId();
    final String expected = Goddard.DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyId_Args__String() throws Exception {
    String thisLegacyId = null;
    target.setThisLegacyId(thisLegacyId);
  }

  @Test
  public void getScreeningId_Args__() throws Exception {
    final String actual = target.getScreeningId();
    String expected = DEFAULT_SCREENING_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningId_Args__String() throws Exception {
    String screeningId = "scr1234567";
    target.setScreeningId(screeningId);
  }

  @Test
  public void getReference_Args__() throws Exception {
    final String actual = target.getReference();
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
  public void getReferralId_Args__() throws Exception {
    final String actual = target.getReferralId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {
    String referralId = null;
    target.setReferralId(referralId);
  }

  @Test
  public void getIncidentDate_Args__() throws Exception {
    final String actual = target.getIncidentDate();
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
    final String actual = target.getLocationType();
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
    final String actual = target.getCommunicationMethod();
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
    final String actual = target.getScreeningName();
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
    final String actual = target.getScreeningDecision();
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
    final String actual = target.getIncidentCounty();
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
    final String actual = target.getReportNarrative();
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
    final String actual = target.getAssignee();
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
    final String actual = target.getAdditionalInformation();
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
    final String actual = target.getScreeningDecisionDetail();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setScreeningDecisionDetail_Args__String() throws Exception {
    String screeningDecisionDetail = null;
    target.setScreeningDecisionDetail(screeningDecisionDetail);
  }

  @Test
  public void getOtherParticipantId_Args__() throws Exception {
    final String actual = target.getOtherParticipantId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOtherParticipantId_Args__String() throws Exception {
    String otherParticipantId = null;
    target.setOtherParticipantId(otherParticipantId);
  }

  @Test
  public void getOtherLegacyId_Args__() throws Exception {
    final String actual = target.getOtherLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOtherLegacyId_Args__String() throws Exception {
    String otherLegacyId = null;
    target.setOtherLegacyId(otherLegacyId);
  }

  @Test
  public void getBirthDt_Args__() throws Exception {
    Date actual = target.getBirthDt();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBirthDt_Args__Date() throws Exception {
    Date birthDt = mock(Date.class);
    target.setBirthDt(birthDt);
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    final String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFirstName_Args__String() throws Exception {
    String firstName = null;
    target.setFirstName(firstName);
  }

  @Test
  public void getLastName_Args__() throws Exception {
    final String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastName_Args__String() throws Exception {
    String lastName = null;
    target.setLastName(lastName);
  }

  @Test
  public void getGender_Args__() throws Exception {
    final String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setGender_Args__String() throws Exception {
    String gender = null;
    target.setGender(gender);
  }

  @Test
  public void getSsn_Args__() throws Exception {
    final String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSsn_Args__String() throws Exception {
    String ssn = null;
    target.setSsn(ssn);
  }

  @Test
  public void getRoles_Args__() throws Exception {
    final String[] actual = target.getRoles();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setRoles_Args__StringArray() throws Exception {
    String[] roles = new String[] {};
    target.setRoles(roles);
  }

  @Test
  public void isFlgReporter_Args__() throws Exception {
    boolean actual = target.isFlgReporter();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgReporter_Args__boolean() throws Exception {
    boolean flgReporter = false;
    target.setFlgReporter(flgReporter);
  }

  @Test
  public void isFlgPerpetrator_Args__() throws Exception {
    boolean actual = target.isFlgPerpetrator();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgPerpetrator_Args__boolean() throws Exception {
    boolean flgPerpetrator = false;
    target.setFlgPerpetrator(flgPerpetrator);
  }

  @Test
  public void isFlgVictim_Args__() throws Exception {
    boolean actual = target.isFlgVictim();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFlgVictim_Args__boolean() throws Exception {
    boolean flgVictim = false;
    target.setFlgVictim(flgVictim);
  }

  @Test
  public void getAllegationId_Args__() throws Exception {
    final String actual = target.getAllegationId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationId_Args__String() throws Exception {
    String allegationId = null;
    target.setAllegationId(allegationId);
  }

  @Test
  public void getAllegationTypes_Args__() throws Exception {
    final String actual = target.getAllegationTypes();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationTypes_Args__String() throws Exception {
    String allegationTypes = null;
    target.setAllegationTypes(allegationTypes);
  }

  @Test
  public void getAddressId_Args__() throws Exception {
    final String actual = target.getAddressId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddressId_Args__String() throws Exception {
    String addressId = null;
    target.setAddressId(addressId);
  }

  @Test
  public void getAddressType_Args__() throws Exception {
    final String actual = target.getAddressType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAddressType_Args__String() throws Exception {
    String addressType = null;
    target.setAddressType(addressType);
  }

  @Test
  public void getStreetAddress_Args__() throws Exception {
    final String actual = target.getStreetAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStreetAddress_Args__String() throws Exception {
    String streetAddress = null;
    target.setStreetAddress(streetAddress);
  }

  @Test
  public void getCity_Args__() throws Exception {
    final String actual = target.getCity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCity_Args__String() throws Exception {
    String city = null;
    target.setCity(city);
  }

  @Test
  public void getState_Args__() throws Exception {
    final String actual = target.getState();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setState_Args__String() throws Exception {
    String state = null;
    target.setState(state);
  }

  @Test
  public void getZip_Args__() throws Exception {
    final String actual = target.getZip();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setZip_Args__String() throws Exception {
    String zip = null;
    target.setZip(zip);
  }

  @Test
  public void getPhoneNumberId_Args__() throws Exception {
    final String actual = target.getPhoneNumberId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneNumberId_Args__String() throws Exception {
    String phoneNumberId = null;
    target.setPhoneNumberId(phoneNumberId);
  }

  @Test
  public void getPhoneNumber_Args__() throws Exception {
    final String actual = target.getPhoneNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneNumber_Args__String() throws Exception {
    String phoneNumber = null;
    target.setPhoneNumber(phoneNumber);
  }

  @Test
  public void getPhoneType_Args__() throws Exception {
    final String actual = target.getPhoneType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPhoneType_Args__String() throws Exception {
    final String phoneType = PhoneType.Cell.name();
    target.setPhoneType(phoneType);
  }

  @Test
  public void addParticipantRoles_Args__IntakeScreening__IntakeParticipant() throws Exception {
    final IntakeScreening s = new IntakeScreening();
    s.setId(DEFAULT_SCREENING_ID);

    IntakeParticipant otherPartc = new IntakeParticipant();
    otherPartc.setId(DEFAULT_CLIENT_ID);
    otherPartc.addScreening(s);

    final String[] roles = {"county supervisor", "social worker"};
    target.setRoles(roles);
    target.addParticipantRoles(s, otherPartc);
  }

  @Test
  public void handleOtherParticipant_Args__IntakeScreening() throws Exception {
    final IntakeScreening s = new IntakeScreening();
    s.setId(DEFAULT_SCREENING_ID);

    IntakeParticipant actual = target.handleOtherParticipant(s);
    IntakeParticipant expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handleOtherParticipant_Args__IntakeScreening__2() throws Exception {
    final IntakeScreening s = new IntakeScreening();
    s.setId("7xA1234567");

    IntakeParticipant actual = target.handleOtherParticipant(s);
    IntakeParticipant expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void makeAllegation_Args__IntakeScreening() throws Exception {
    final IntakeScreening s = new IntakeScreening();
    s.setId(DEFAULT_SCREENING_ID);

    IntakeAllegation alg = new IntakeAllegation();
    alg.setId("cyz1234567");
    s.addAllegation(alg);

    IntakeAllegation actual = target.makeAllegation(s);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void makeAllegation_Args__IntakeScreening__2() throws Exception {
    final IntakeScreening s = new IntakeScreening();
    s.setId(DEFAULT_SCREENING_ID);

    IntakeAllegation alg = new IntakeAllegation();
    alg.setId("cyz1234567");
    s.addAllegation(alg);

    target.setAllegationId("cyz1234567");
    IntakeAllegation actual = target.makeAllegation(s);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void handleAllegations_Args__String__IntakeScreening__IntakeParticipant()
      throws Exception {
    String thisPartcId = DEFAULT_CLIENT_ID;
    final IntakeScreening s = new IntakeScreening();

    final IntakeParticipant otherPartc = new IntakeParticipant();
    otherPartc.setId(DEFAULT_CLIENT_ID);
    otherPartc.addScreening(s);

    target.handleAllegations(thisPartcId, s, otherPartc);
  }

}
