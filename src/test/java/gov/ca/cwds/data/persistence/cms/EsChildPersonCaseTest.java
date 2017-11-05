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
    String actual = target.getCaseId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseId_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String caseId = null;
    target.setCaseId(caseId);
  }

  @Test
  public void getFocusChildId_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getFocusChildId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildId_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String focusChildId = null;
    target.setFocusChildId(focusChildId);
  }

  @Test
  public void getParentId_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getParentId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentId_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String parentId = null;
    target.setParentId(parentId);
  }

  @Test
  public void getStartDate_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartDate_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date startDate = mock(Date.class);
    target.setStartDate(startDate);
  }

  @Test
  public void getEndDate_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getEndDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setEndDate_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date endDate = mock(Date.class);
    target.setEndDate(endDate);
  }

  @Test
  public void getCounty_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer actual = target.getCounty();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCounty_Args__Integer() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer county = null;
    target.setCounty(county);
  }

  @Test
  public void getServiceComponent_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer actual = target.getServiceComponent();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setServiceComponent_Args__Integer() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer serviceComponent = null;
    target.setServiceComponent(serviceComponent);
  }

  @Test
  public void getCaseLastUpdated_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getCaseLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseLastUpdated_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date caseLastUpdated = mock(Date.class);
    target.setCaseLastUpdated(caseLastUpdated);
  }

  @Test
  public void getFocusChildFirstName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getFocusChildFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildFirstName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String focusChildFirstName = null;
    target.setFocusChildFirstName(focusChildFirstName);
  }

  @Test
  public void getFocusChildLastName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getFocusChildLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String focusChildLastName = null;
    target.setFocusChildLastName(focusChildLastName);
  }

  @Test
  public void getFocusChildLastUpdated_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getFocusChildLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastUpdated_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date focusChildLastUpdated = mock(Date.class);
    target.setFocusChildLastUpdated(focusChildLastUpdated);
  }

  @Test
  public void getWorkerId_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getWorker().getWorkerId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerId_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String workerId = null;
    target.getWorker().setWorkerId(workerId);
  }

  @Test
  public void getWorkerFirstName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getWorker().getWorkerFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerFirstName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String workerFirstName = null;
    target.getWorker().setWorkerFirstName(workerFirstName);
  }

  @Test
  public void getWorkerLastName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getWorker().getWorkerLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String workerLastName = null;
    target.getWorker().setWorkerLastName(workerLastName);
  }

  @Test
  public void getWorkerLastUpdated_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getWorker().getWorkerLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastUpdated_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date workerLastUpdated = mock(Date.class);
    target.getWorker().setWorkerLastUpdated(workerLastUpdated);
  }

  @Test
  public void getParentFirstName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getParentFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentFirstName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String parentFirstName = null;
    target.setParentFirstName(parentFirstName);
  }

  @Test
  public void getParentLastName_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getParentLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastName_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String parentLastName = null;
    target.setParentLastName(parentLastName);
  }

  @Test
  public void getParentRelationship_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer actual = target.getParentRelationship();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentRelationship_Args__Integer() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer parentRelationship = null;
    target.setParentRelationship(parentRelationship);
  }

  @Test
  public void getParentLastUpdated_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getParentLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastUpdated_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date parentLastUpdated = mock(Date.class);
    target.setParentLastUpdated(parentLastUpdated);
  }

  @Test
  public void getParentSourceTable_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getParentSourceTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentSourceTable_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String parentSourceTable = null;
    target.setParentSourceTable(parentSourceTable);
  }

  @Test
  public void getLimitedAccessCode_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getLimitedAccessCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessCode_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String limitedAccessCode = null;
    target.setLimitedAccessCode(limitedAccessCode);
  }

  @Test
  public void getLimitedAccessDate_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date actual = target.getLimitedAccessDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDate_Args__Date() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Date limitedAccessDate = mock(Date.class);
    target.setLimitedAccessDate(limitedAccessDate);
  }

  @Test
  public void getLimitedAccessDescription_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String actual = target.getLimitedAccessDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessDescription_Args__String() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    String limitedAccessDescription = null;
    target.setLimitedAccessDescription(limitedAccessDescription);
  }

  @Test
  public void getLimitedAccessGovernmentEntityId_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer actual = target.getLimitedAccessGovernmentEntityId();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLimitedAccessGovernmentEntityId_Args__Integer() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Integer limitedAccessGovernmentEntityId = null;
    target.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Map<Object, ReplicatedPersonCases> map = new HashMap<Object, ReplicatedPersonCases>();
    ReplicatedPersonCases actual = target.normalize(map);
    // ReplicatedPersonCases expected = null;
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsChildPersonCase target = new EsChildPersonCase();
    Object actual = target.getNormalizationGroupKey();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
