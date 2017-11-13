package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.rep.EmbeddableAccessLimitation;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableStaffWorker;
import gov.ca.cwds.jobs.Goddard;

public class EsPersonCaseTest extends Goddard {

  private static final class TestTarget extends EsPersonCase {
    private String id;

    private TestTarget() {
      super();
    }

    private TestTarget(String id) {
      super();
      this.id = id;
    }

    @Override
    public String getNormalizationGroupKey() {
      return id;
    }

    @Override
    public ReplicatedPersonCases normalize(Map<Object, ReplicatedPersonCases> map) {
      return new ReplicatedPersonCases(this.id);
    }

  }

  EsPersonCase target;

  @Before
  @Override
  public void setup() throws Exception {
    super.setup();
    target = new TestTarget(DEFAULT_CLIENT_ID);
  }

  @Test
  public void type() throws Exception {
    assertThat(EsPersonCase.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<ReplicatedPersonCases> actual = target.getNormalizationClass();
    Class<ReplicatedPersonCases> expected = ReplicatedPersonCases.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCaseId_Args__() throws Exception {
    String actual = target.getCaseId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseId_Args__String() throws Exception {
    String caseId = null;
    target.setCaseId(caseId);
  }

  @Test
  public void getFocusChildId_Args__() throws Exception {
    String actual = target.getFocusChildId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildId_Args__String() throws Exception {
    String focusChildId = null;
    target.setFocusChildId(focusChildId);
  }

  @Test
  public void getParentId_Args__() throws Exception {
    String actual = target.getParentId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentId_Args__String() throws Exception {
    String parentId = null;
    target.setParentId(parentId);
  }

  @Test
  public void getStartDate_Args__() throws Exception {
    Date actual = target.getStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setStartDate_Args__Date() throws Exception {
    Date startDate = mock(Date.class);
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
    Date endDate = mock(Date.class);
    target.setEndDate(endDate);
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
  public void getServiceComponent_Args__() throws Exception {
    Integer actual = target.getServiceComponent();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setServiceComponent_Args__Integer() throws Exception {
    Integer serviceComponent = null;
    target.setServiceComponent(serviceComponent);
  }

  @Test
  public void getCaseLastUpdated_Args__() throws Exception {
    Date actual = target.getCaseLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCaseLastUpdated_Args__Date() throws Exception {
    Date caseLastUpdated = mock(Date.class);
    target.setCaseLastUpdated(caseLastUpdated);
  }

  @Test
  public void getFocusChildFirstName_Args__() throws Exception {
    String actual = target.getFocusChildFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildFirstName_Args__String() throws Exception {
    String focusChildFirstName = null;
    target.setFocusChildFirstName(focusChildFirstName);
  }

  @Test
  public void getFocusChildLastName_Args__() throws Exception {
    String actual = target.getFocusChildLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastName_Args__String() throws Exception {
    String focusChildLastName = null;
    target.setFocusChildLastName(focusChildLastName);
  }

  @Test
  public void getFocusChildLastUpdated_Args__() throws Exception {
    Date actual = target.getFocusChildLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildLastUpdated_Args__Date() throws Exception {
    Date focusChildLastUpdated = mock(Date.class);
    target.setFocusChildLastUpdated(focusChildLastUpdated);
  }

  @Test
  public void getParentFirstName_Args__() throws Exception {
    String actual = target.getParentFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentFirstName_Args__String() throws Exception {
    String parentFirstName = null;
    target.setParentFirstName(parentFirstName);
  }

  @Test
  public void getParentLastName_Args__() throws Exception {
    String actual = target.getParentLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastName_Args__String() throws Exception {
    String parentLastName = null;
    target.setParentLastName(parentLastName);
  }

  @Test
  public void getParentRelationship_Args__() throws Exception {
    Integer actual = target.getParentRelationship();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentRelationship_Args__Integer() throws Exception {
    Integer parentRelationship = null;
    target.setParentRelationship(parentRelationship);
  }

  @Test
  public void getParentLastUpdated_Args__() throws Exception {
    Date actual = target.getParentLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentLastUpdated_Args__Date() throws Exception {
    Date parentLastUpdated = mock(Date.class);
    target.setParentLastUpdated(parentLastUpdated);
  }

  @Test
  public void getParentSourceTable_Args__() throws Exception {
    String actual = target.getParentSourceTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentSourceTable_Args__String() throws Exception {
    String parentSourceTable = null;
    target.setParentSourceTable(parentSourceTable);
  }

  @Test
  public void getFocusChildSensitivityIndicator_Args__() throws Exception {
    String actual = target.getFocusChildSensitivityIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setFocusChildSensitivityIndicator_Args__String() throws Exception {
    String focusChildSensitivityIndicator = null;
    target.setFocusChildSensitivityIndicator(focusChildSensitivityIndicator);
  }

  @Test
  public void getParentSensitivityIndicator_Args__() throws Exception {
    String actual = target.getParentSensitivityIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setParentSensitivityIndicator_Args__String() throws Exception {
    String parentSensitivityIndicator = null;
    target.setParentSensitivityIndicator(parentSensitivityIndicator);
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    Map<Object, ReplicatedPersonCases> map = new HashMap<Object, ReplicatedPersonCases>();
    ReplicatedPersonCases actual = target.normalize(map);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getWorker_Args__() throws Exception {
    EmbeddableStaffWorker actual = target.getWorker();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setWorker_Args__EmbeddableStaffWorker() throws Exception {
    EmbeddableStaffWorker worker = mock(EmbeddableStaffWorker.class);
    target.setWorker(worker);
  }

  @Test
  public void getAccessLimitation_Args__() throws Exception {
    EmbeddableAccessLimitation actual = target.getAccessLimitation();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setAccessLimitation_Args__EmbeddableAccessLimitation() throws Exception {
    EmbeddableAccessLimitation accessLimitation = mock(EmbeddableAccessLimitation.class);
    target.setAccessLimitation(accessLimitation);
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
    Date limitedAccessDate = mock(Date.class);
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
  public void getWorkerLastUpdated_Args__() throws Exception {
    Date actual = target.getWorkerLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setWorkerLastUpdated_Args__Date() throws Exception {
    Date workerLastUpdated = mock(Date.class);
    target.setWorkerLastUpdated(workerLastUpdated);
  }

  @Test
  public void toString_Args__() throws Exception {
    String actual = target.toString();
    assertThat(actual, is(notNullValue()));
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

}
