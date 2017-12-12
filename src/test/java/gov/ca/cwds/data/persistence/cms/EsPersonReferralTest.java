package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class EsPersonReferralTest extends Goddard {

  EsPersonReferral target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    EsPersonReferral.setOpts(flightPlan);
    target = new EsPersonReferral();
    target.setClientId(DEFAULT_CLIENT_ID);
  }

  @Test
  public void type() throws Exception {
    assertThat(EsPersonReferral.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<ReplicatedPersonReferrals> actual = target.getNormalizationClass();
    Class<ReplicatedPersonReferrals> expected = ReplicatedPersonReferrals.class;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void normalize_Args__Map() throws Exception {
  //
  // final Map<Object, ReplicatedPersonReferrals> map =
  // new HashMap<Object, ReplicatedPersonReferrals>();
  // final ReplicatedPersonReferrals actual = target.normalize(map);
  // final ReplicatedPersonReferrals expected = new ReplicatedPersonReferrals();
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    Object actual = target.getNormalizationGroupKey();
    Object expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void hashCode_Args__() throws Exception {
  //
  //
  //
  //
  // int actual = target.hashCode();
  //
  //
  // int expected = 0;
  // assertThat(actual, is(equalTo(expected)));
  // }

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

  @Test
  public void normalize_Args__Map() throws Exception {
    target.setClientId("xyz789");
    target.setAllegationId("abc1234");
    target.setReferralId("ddusicnz7");
    Map<Object, ReplicatedPersonReferrals> map = new HashMap<Object, ReplicatedPersonReferrals>();
    ReplicatedPersonReferrals actual = target.normalize(map);
    ReplicatedPersonReferrals expected = new ReplicatedPersonReferrals("xyz789");
    ElasticSearchPersonAllegation allegation = new ElasticSearchPersonAllegation();
    allegation.setId("abc1234");
    allegation.setLegacyId("abc1234");
    allegation.setLegacyDescriptor(gov.ca.cwds.neutron.util.transform.ElasticTransformer
        .createLegacyDescriptor("abc1234", null, LegacyTable.ALLEGATION));
    ElasticSearchPersonReferral referral = new ElasticSearchPersonReferral();
    referral.setId("ddusicnz7");
    referral.setLegacyId("ddusicnz7");
    referral.setLegacyDescriptor(
        ElasticTransformer.createLegacyDescriptor("ddusicnz7", null, LegacyTable.REFERRAL));
    expected.addReferral(referral, allegation);
    // Value is a literal "null"? Is this right?
    // expected.geReferrals().get(0).getAccessLimitation().setLimitedAccessGovernmentEntityId("null");
    // referral.setCountyId("null");
    // referral.setResponseTimeId("null");
    // allegation.setDispositionId("null");
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getLastChange_Args__() throws Exception {
    final Date expected = new Date();
    target.setLastChange(expected);
    final Date actual = target.getLastChange();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {
    Date lastChange = new Date();
    target.setLastChange(lastChange);
  }

  @Test
  public void getClientId_Args__() throws Exception {
    String actual = target.getClientId();
    String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientId_Args__String() throws Exception {
    String clientId = null;
    target.setClientId(clientId);
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
  public void getStartDate_Args__() throws Exception {
    Date actual = target.getStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartDate_Args__Date() throws Exception {
    Date startDate = new Date();
    target.setStartDate(startDate);
  }

  @Test
  public void getEndDate_Args__() throws Exception {
    Date actual = target.getEndDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndDate_Args__Date() throws Exception {
    Date endDate = new Date();
    target.setEndDate(endDate);
  }

  @Test
  public void getReferralResponseType_Args__() throws Exception {
    Integer actual = target.getReferralResponseType();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralResponseType_Args__Integer() throws Exception {
    Integer referralResponseType = null;
    target.setReferralResponseType(referralResponseType);
  }

  @Test
  public void getCounty_Args__() throws Exception {
    Integer actual = target.getCounty();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__Integer() throws Exception {
    Integer county = null;
    target.setCounty(county);
  }

  @Test
  public void getReporterId_Args__() throws Exception {
    String actual = target.getReporterId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterId_Args__String() throws Exception {
    String reporterId = null;
    target.setReporterId(reporterId);
  }

  @Test
  public void getReporterFirstName_Args__() throws Exception {
    String actual = target.getReporterFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterFirstName_Args__String() throws Exception {
    String reporterFirstName = null;
    target.setReporterFirstName(reporterFirstName);
  }

  @Test
  public void getReporterLastName_Args__() throws Exception {
    String actual = target.getReporterLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterLastName_Args__String() throws Exception {
    String reporterLastName = null;
    target.setReporterLastName(reporterLastName);
  }

  @Test
  public void getWorkerId_Args__() throws Exception {
    String actual = target.getWorkerId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerId_Args__String() throws Exception {
    String workerId = null;
    target.setWorkerId(workerId);
  }

  @Test
  public void getWorkerFirstName_Args__() throws Exception {
    String actual = target.getWorkerFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerFirstName_Args__String() throws Exception {
    String workerFirstName = null;
    target.setWorkerFirstName(workerFirstName);
  }

  @Test
  public void getWorkerLastName_Args__() throws Exception {
    String actual = target.getWorkerLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastName_Args__String() throws Exception {
    String workerLastName = null;
    target.setWorkerLastName(workerLastName);
  }

  @Test
  public void getAllegationId_Args__() throws Exception {
    String actual = target.getAllegationId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationId_Args__String() throws Exception {
    String allegationId = null;
    target.setAllegationId(allegationId);
  }

  @Test
  public void getAllegationDisposition_Args__() throws Exception {
    Integer actual = target.getAllegationDisposition();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationDisposition_Args__Integer() throws Exception {
    Integer allegationDisposition = null;
    target.setAllegationDisposition(allegationDisposition);
  }

  @Test
  public void getAllegationType_Args__() throws Exception {
    Integer actual = target.getAllegationType();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationType_Args__Integer() throws Exception {
    Integer allegationType = null;
    target.setAllegationType(allegationType);
  }

  @Test
  public void getVictimId_Args__() throws Exception {
    String actual = target.getVictimId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimId_Args__String() throws Exception {
    String victimId = null;
    target.setVictimId(victimId);
  }

  @Test
  public void getVictimFirstName_Args__() throws Exception {
    String actual = target.getVictimFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimFirstName_Args__String() throws Exception {
    String victimFirstName = null;
    target.setVictimFirstName(victimFirstName);
  }

  @Test
  public void getVictimLastName_Args__() throws Exception {
    String actual = target.getVictimLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimLastName_Args__String() throws Exception {
    String victimLastName = null;
    target.setVictimLastName(victimLastName);
  }

  @Test
  public void getPerpetratorId_Args__() throws Exception {
    String actual = target.getPerpetratorId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorId_Args__String() throws Exception {
    String perpetratorId = null;
    target.setPerpetratorId(perpetratorId);
  }

  @Test
  public void getPerpetratorFirstName_Args__() throws Exception {
    String actual = target.getPerpetratorFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorFirstName_Args__String() throws Exception {
    String perpetratorFirstName = null;
    target.setPerpetratorFirstName(perpetratorFirstName);
  }

  @Test
  public void getPerpetratorLastName_Args__() throws Exception {
    String actual = target.getPerpetratorLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorLastName_Args__String() throws Exception {
    String perpetratorLastName = null;
    target.setPerpetratorLastName(perpetratorLastName);
  }

  @Test
  public void getLimitedAccessCode_Args__() throws Exception {
    String actual = target.getLimitedAccessCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessCode_Args__String() throws Exception {
    String limitedAccessCode = null;
    target.setLimitedAccessCode(limitedAccessCode);
  }

  @Test
  public void getLimitedAccessDate_Args__() throws Exception {
    Date actual = target.getLimitedAccessDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDate_Args__Date() throws Exception {
    Date limitedAccessDate = new Date();
    target.setLimitedAccessDate(limitedAccessDate);
  }

  @Test
  public void getLimitedAccessDescription_Args__() throws Exception {
    String actual = target.getLimitedAccessDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDescription_Args__String() throws Exception {
    String limitedAccessDescription = null;
    target.setLimitedAccessDescription(limitedAccessDescription);
  }

  @Test
  public void getLimitedAccessGovernmentEntityId_Args__() throws Exception {
    Integer actual = target.getLimitedAccessGovernmentEntityId();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessGovernmentEntityId_Args__Integer() throws Exception {
    Integer limitedAccessGovernmentEntityId = null;
    target.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void extractAllegation_Args__ResultSet() throws Exception {
    EsPersonReferral actual = EsPersonReferral.extractAllegation(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void extractAllegation_Args__ResultSet_T__SQLException() throws Exception {
    try {
      doThrow(new SQLException()).when(rs).getString(any());
      EsPersonReferral.extractAllegation(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void extractReferral_Args__ResultSet() throws Exception {
    EsPersonReferral actual = EsPersonReferral.extractReferral(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void extractReferral_Args__ResultSet_T__SQLException() throws Exception {
    try {
      doThrow(new SQLException()).when(rs).getString(any());
      EsPersonReferral.extractReferral(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void mergeClientReferralInfo_Args__String__EsPersonReferral() throws Exception {
    String clientId = null;
    EsPersonReferral ref = new EsPersonReferral();
    target.mergeClientReferralInfo(clientId, ref);
  }

  @Test
  public void getReferralLastUpdated_Args__() throws Exception {
    Date actual = target.getReferralLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralLastUpdated_Args__Date() throws Exception {
    Date referralLastUpdated = new Date();
    target.setReferralLastUpdated(referralLastUpdated);
  }

  @Test
  public void getReporterLastUpdated_Args__() throws Exception {
    Date actual = target.getReporterLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterLastUpdated_Args__Date() throws Exception {
    Date reporterLastUpdated = new Date();
    target.setReporterLastUpdated(reporterLastUpdated);
  }

  @Test
  public void getWorkerLastUpdated_Args__() throws Exception {
    Date actual = target.getWorkerLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastUpdated_Args__Date() throws Exception {
    Date workerLastUpdated = new Date();
    target.setWorkerLastUpdated(workerLastUpdated);
  }

  @Test
  public void getAllegationLastUpdated_Args__() throws Exception {
    Date actual = target.getAllegationLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationLastUpdated_Args__Date() throws Exception {
    Date allegationLastUpdated = new Date();
    target.setAllegationLastUpdated(allegationLastUpdated);
  }

  @Test
  public void getVictimLastUpdated_Args__() throws Exception {
    Date actual = target.getVictimLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimLastUpdated_Args__Date() throws Exception {
    Date victimLastUpdated = new Date();
    target.setVictimLastUpdated(victimLastUpdated);
  }

  @Test
  public void getPerpetratorLastUpdated_Args__() throws Exception {
    Date actual = target.getPerpetratorLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorLastUpdated_Args__Date() throws Exception {
    Date perpetratorLastUpdated = new Date();
    target.setPerpetratorLastUpdated(perpetratorLastUpdated);
  }

  @Test
  public void getVictimSensitivityIndicator_Args__() throws Exception {
    String actual = target.getVictimSensitivityIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimSensitivityIndicator_Args__String() throws Exception {
    String victimSensitivityIndicator = null;
    target.setVictimSensitivityIndicator(victimSensitivityIndicator);
  }

  @Test
  public void getPerpetratorSensitivityIndicator_Args__() throws Exception {
    String actual = target.getPerpetratorSensitivityIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorSensitivityIndicator_Args__String() throws Exception {
    String perpetratorSensitivityIndicator = null;
    target.setPerpetratorSensitivityIndicator(perpetratorSensitivityIndicator);
  }

  @Test
  public void getClientSensitivity_Args__() throws Exception {
    String actual = target.getClientSensitivity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientSensitivity_Args__String() throws Exception {
    String clientSensitivity = null;
    target.setClientSensitivity(clientSensitivity);
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    FlightPlan opts = mock(FlightPlan.class);
    EsPersonReferral.setOpts(opts);
  }

  @Test
  public void compare_Args__EsPersonReferral__EsPersonReferral() throws Exception {
    EsPersonReferral o1 = new EsPersonReferral();
    o1.setClientId(DEFAULT_CLIENT_ID);
    EsPersonReferral o2 = new EsPersonReferral();
    o2.setClientId(DEFAULT_CLIENT_ID);
    int actual = target.compare(o1, o2);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void compareTo_Args__EsPersonReferral() throws Exception {
    EsPersonReferral o1 = new EsPersonReferral();
    o1.setClientId(DEFAULT_CLIENT_ID);
    int actual = target.compareTo(o1);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

}
