package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;

public class EsChildPersonCaseTest {

  @BeforeClass
  public static void setupTests() {
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(EsChildPersonCase.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    assertThat(target, notNullValue());
  }

  @Test
  public void getCaseId_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getCaseId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseId_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String caseId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCaseId(caseId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getFocusChildId_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFocusChildId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildId_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String focusChildId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFocusChildId(focusChildId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentId_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getParentId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentId_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String parentId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentId(parentId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getStartDate_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Date endDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setEndDate(endDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCounty_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Integer county = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCounty(county);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getServiceComponent_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getServiceComponent();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setServiceComponent_Args__Integer() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Integer serviceComponent = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setServiceComponent(serviceComponent);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getCaseLastUpdated_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getCaseLastUpdated();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseLastUpdated_Args__Date() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Date caseLastUpdated = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setCaseLastUpdated(caseLastUpdated);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getFocusChildFirstName_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFocusChildFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildFirstName_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String focusChildFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFocusChildFirstName(focusChildFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getFocusChildLastName_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFocusChildLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastName_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String focusChildLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFocusChildLastName(focusChildLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getFocusChildLastUpdated_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getFocusChildLastUpdated();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastUpdated_Args__Date() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Date focusChildLastUpdated = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setFocusChildLastUpdated(focusChildLastUpdated);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getWorkerId_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String workerLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setWorkerLastName(workerLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getWorkerLastUpdated_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getWorkerLastUpdated();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastUpdated_Args__Date() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Date workerLastUpdated = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setWorkerLastUpdated(workerLastUpdated);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentFirstName_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getParentFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentFirstName_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String parentFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentFirstName(parentFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentLastName_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getParentLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastName_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String parentLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentLastName(parentLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentRelationship_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Integer actual = target.getParentRelationship();
    // then
    // e.g. : verify(mocked).called();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentRelationship_Args__Integer() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Integer parentRelationship = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentRelationship(parentRelationship);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentLastUpdated_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getParentLastUpdated();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastUpdated_Args__Date() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Date parentLastUpdated = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentLastUpdated(parentLastUpdated);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getParentSourceTable_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getParentSourceTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentSourceTable_Args__String() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
    // given
    String parentSourceTable = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setParentSourceTable(parentSourceTable);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLimitedAccessCode_Args__() throws Exception {

    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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
    EsChildPersonCase target = new EsChildPersonCase();
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
    EsChildPersonCase target = new EsChildPersonCase();
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
    EsChildPersonCase target = new EsChildPersonCase();
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

    EsChildPersonCase target = new EsChildPersonCase();
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
    EsChildPersonCase target = new EsChildPersonCase();
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
    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Integer limitedAccessGovernmentEntityId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    // given
    Map<Object, ReplicatedPersonCases> map = new HashMap<Object, ReplicatedPersonCases>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedPersonCases actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    // ReplicatedPersonCases expected = null;
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getNormalizationGroupKey();
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
