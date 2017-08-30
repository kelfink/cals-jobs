package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class EsPersonReferralTest {

  @BeforeClass
  public static void setupTests() {
    SimpleTestSystemCodeCache.init();
    final String[] args = {"-c", "config/local.yaml", "-l",
        "/Users/CWS-NS3/client_indexer_time.txt", "--thread-num=4", "-r", "1-20"};
    EsPersonReferral.setOpts(JobOptions.parseCommandLine(args));
  }

  @Test
  public void type() throws Exception {
    assertThat(EsPersonReferral.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Class<ReplicatedPersonReferrals> actual = target.getNormalizationClass();
    Class<ReplicatedPersonReferrals> expected = ReplicatedPersonReferrals.class;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void normalize_Args__Map() throws Exception {
  // EsPersonReferral target = new EsPersonReferral();
  // final Map<Object, ReplicatedPersonReferrals> map =
  // new HashMap<Object, ReplicatedPersonReferrals>();
  // final ReplicatedPersonReferrals actual = target.normalize(map);
  // final ReplicatedPersonReferrals expected = new ReplicatedPersonReferrals();
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Object actual = target.getNormalizationGroupKey();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void hashCode_Args__() throws Exception {
  // EsPersonReferral target = new EsPersonReferral();
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
    EsPersonReferral target = new EsPersonReferral();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.toString();
    String expected = new EsPersonReferral().toString();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    target.setClientId("xyz789");
    target.setAllegationId("abc1234");
    target.setReferralId("ddusicnz7");
    Map<Object, ReplicatedPersonReferrals> map = new HashMap<Object, ReplicatedPersonReferrals>();
    ReplicatedPersonReferrals actual = target.normalize(map);
    ReplicatedPersonReferrals expected = new ReplicatedPersonReferrals("xyz789");
    ElasticSearchPersonAllegation allegation = new ElasticSearchPersonAllegation();
    allegation.setId("abc1234");
    allegation.setLegacyId("abc1234");
    allegation.setLegacyDescriptor(
        ElasticTransformer.createLegacyDescriptor("abc1234", null, LegacyTable.ALLEGATION));
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
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChange_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date actual = target.getLastChange();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date lastChange = mock(Date.class);
    target.setLastChange(lastChange);
  }

  @Test
  public void getClientId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getClientId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String clientId = null;
    target.setClientId(clientId);
  }

  @Test
  public void getReferralId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getReferralId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String referralId = null;
    target.setReferralId(referralId);
  }

  @Test
  public void getStartDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date actual = target.getStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date startDate = mock(Date.class);
    target.setStartDate(startDate);
  }

  @Test
  public void getEndDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date actual = target.getEndDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date endDate = mock(Date.class);
    target.setEndDate(endDate);
  }

  @Test
  public void getReferralResponseType_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer actual = target.getReferralResponseType();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralResponseType_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer referralResponseType = null;
    target.setReferralResponseType(referralResponseType);
  }

  @Test
  public void getCounty_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer actual = target.getCounty();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer county = null;
    target.setCounty(county);
  }

  @Test
  public void getReporterId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getReporterId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String reporterId = null;
    target.setReporterId(reporterId);
  }

  @Test
  public void getReporterFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getReporterFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String reporterFirstName = null;
    target.setReporterFirstName(reporterFirstName);
  }

  @Test
  public void getReporterLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getReporterLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String reporterLastName = null;
    target.setReporterLastName(reporterLastName);
  }

  @Test
  public void getWorkerId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getWorkerId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String workerId = null;
    target.setWorkerId(workerId);
  }

  @Test
  public void getWorkerFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getWorkerFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String workerFirstName = null;
    target.setWorkerFirstName(workerFirstName);
  }

  @Test
  public void getWorkerLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getWorkerLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String workerLastName = null;
    target.setWorkerLastName(workerLastName);
  }

  @Test
  public void getAllegationId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getAllegationId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String allegationId = null;
    target.setAllegationId(allegationId);
  }

  @Test
  public void getAllegationDisposition_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer actual = target.getAllegationDisposition();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationDisposition_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer allegationDisposition = null;
    target.setAllegationDisposition(allegationDisposition);
  }

  @Test
  public void getAllegationType_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer actual = target.getAllegationType();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationType_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer allegationType = null;
    target.setAllegationType(allegationType);
  }

  @Test
  public void getVictimId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getVictimId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String victimId = null;
    target.setVictimId(victimId);
  }

  @Test
  public void getVictimFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getVictimFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String victimFirstName = null;
    target.setVictimFirstName(victimFirstName);
  }

  @Test
  public void getVictimLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getVictimLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String victimLastName = null;
    target.setVictimLastName(victimLastName);
  }

  @Test
  public void getPerpetratorId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getPerpetratorId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String perpetratorId = null;
    target.setPerpetratorId(perpetratorId);
  }

  @Test
  public void getPerpetratorFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getPerpetratorFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String perpetratorFirstName = null;
    target.setPerpetratorFirstName(perpetratorFirstName);
  }

  @Test
  public void getPerpetratorLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getPerpetratorLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String perpetratorLastName = null;
    target.setPerpetratorLastName(perpetratorLastName);
  }

  @Test
  public void getLimitedAccessCode_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getLimitedAccessCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessCode_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String limitedAccessCode = null;
    target.setLimitedAccessCode(limitedAccessCode);
  }

  @Test
  public void getLimitedAccessDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date actual = target.getLimitedAccessDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Date limitedAccessDate = mock(Date.class);
    target.setLimitedAccessDate(limitedAccessDate);
  }

  @Test
  public void getLimitedAccessDescription_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String actual = target.getLimitedAccessDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDescription_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    String limitedAccessDescription = null;
    target.setLimitedAccessDescription(limitedAccessDescription);
  }

  @Test
  public void getLimitedAccessGovernmentEntityId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer actual = target.getLimitedAccessGovernmentEntityId();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessGovernmentEntityId_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    Integer limitedAccessGovernmentEntityId = null;
    target.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
  }

  @Test
  @Ignore
  public void hashCode_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    int actual = target.hashCode();
    int expected = -262837655;
    assertThat(actual, is(equalTo(expected)));
  }

}

