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
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class EsPersonReferralTest {

  @BeforeClass
  public static void setupTests() {
    SimpleTestSystemCodeCache.init();
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
  // // given
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // int actual = target.hashCode();
  // // then
  // // e.g. : verify(mocked).called();
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
    EsPersonReferral target = new EsPersonReferral();
    // given
    Date lastChange = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLastChange(lastChange);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getClientId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getClientId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String clientId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setClientId(clientId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReferralId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
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
    EsPersonReferral target = new EsPersonReferral();
    // given
    String referralId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReferralId(referralId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStartDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getStartDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Date startDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setStartDate(startDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getEndDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getEndDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Date endDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setEndDate(endDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReferralResponseType_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getReferralResponseType();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReferralResponseType_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Integer referralResponseType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReferralResponseType(referralResponseType);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCounty_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getCounty();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Integer county = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCounty(county);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReporterId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReporterId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String reporterId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReporterId(reporterId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReporterFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReporterFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String reporterFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReporterFirstName(reporterFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReporterLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getReporterLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReporterLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String reporterLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReporterLastName(reporterLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getWorkerId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getWorkerId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String workerId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setWorkerId(workerId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getWorkerFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getWorkerFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String workerFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setWorkerFirstName(workerFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getWorkerLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getWorkerLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String workerLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setWorkerLastName(workerLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
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
    EsPersonReferral target = new EsPersonReferral();
    // given
    String allegationId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationId(allegationId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationDisposition_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getAllegationDisposition();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationDisposition_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Integer allegationDisposition = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationDisposition(allegationDisposition);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getAllegationType_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getAllegationType();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAllegationType_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Integer allegationType = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setAllegationType(allegationType);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getVictimId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getVictimId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String victimId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setVictimId(victimId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getVictimFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getVictimFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String victimFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setVictimFirstName(victimFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getVictimLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getVictimLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setVictimLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String victimLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setVictimLastName(victimLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPerpetratorId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPerpetratorId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorId_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String perpetratorId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPerpetratorId(perpetratorId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPerpetratorFirstName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPerpetratorFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorFirstName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String perpetratorFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPerpetratorFirstName(perpetratorFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPerpetratorLastName_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getPerpetratorLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPerpetratorLastName_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String perpetratorLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setPerpetratorLastName(perpetratorLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLimitedAccessCode_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLimitedAccessCode();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessCode_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String limitedAccessCode = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLimitedAccessCode(limitedAccessCode);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLimitedAccessDate_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getLimitedAccessDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDate_Args__Date() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Date limitedAccessDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLimitedAccessDate(limitedAccessDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLimitedAccessDescription_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLimitedAccessDescription();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDescription_Args__String() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    String limitedAccessDescription = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLimitedAccessDescription(limitedAccessDescription);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLimitedAccessGovernmentEntityId_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getLimitedAccessGovernmentEntityId();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessGovernmentEntityId_Args__Integer() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    Integer limitedAccessGovernmentEntityId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void hashCode_Args__() throws Exception {
    EsPersonReferral target = new EsPersonReferral();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = -262837655;
    assertThat(actual, is(equalTo(expected)));
  }

}
