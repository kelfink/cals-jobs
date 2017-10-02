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

import gov.ca.cwds.data.es.ElasticSearchCounty;
import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;

public class EsSafetyAlertTest {

  @BeforeClass
  public static void initClass() {
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(EsSafetyAlert.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Class<ReplicatedSafetyAlerts> actual = target.getNormalizationClass();
    Class<ReplicatedSafetyAlerts> expected = ReplicatedSafetyAlerts.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Map<Object, ReplicatedSafetyAlerts> map = new HashMap<Object, ReplicatedSafetyAlerts>();
    ReplicatedSafetyAlerts actual = target.normalize(map);

    ReplicatedSafetyAlerts expected = new ReplicatedSafetyAlerts();
    ElasticSearchSafetyAlert esSafetyAlert = new ElasticSearchSafetyAlert();

    ElasticSearchSafetyAlert.Activation activation = new ElasticSearchSafetyAlert.Activation();
    esSafetyAlert.setActivation(activation);
    activation.setActivationCounty(new ElasticSearchCounty());

    ElasticSearchSafetyAlert.Deactivation deactivation =
        new ElasticSearchSafetyAlert.Deactivation();
    esSafetyAlert.setDeactivation(deactivation);
    deactivation.setDeactivationCounty(new ElasticSearchCounty());
    expected.addSafetyAlert(esSafetyAlert);

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getNormalizationGroupKey();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChanged_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date actual = target.getLastChanged();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChanged_Args__Date() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date lastChanged = mock(Date.class);
    target.setLastChanged(lastChanged);
  }

  @Test
  public void getClientId_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getClientId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientId_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String clientId = null;
    target.setClientId(clientId);
  }

  @Test
  public void getAlertId_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getAlertId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAlertId_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String alertId = null;
    target.setAlertId(alertId);
  }

  @Test
  public void getActivationReasonCode_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer actual = target.getActivationReasonCode();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setActivationReasonCode_Args__Integer() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer activationReasonCode = null;
    target.setActivationReasonCode(activationReasonCode);
  }

  @Test
  public void getActivationDate_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date actual = target.getActivationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setActivationDate_Args__Date() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date activationDate = mock(Date.class);
    target.setActivationDate(activationDate);
  }

  @Test
  public void getActivationCountyCode_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer actual = target.getActivationCountyCode();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setActivationCountyCode_Args__Integer() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer activationCountyCode = null;
    target.setActivationCountyCode(activationCountyCode);
  }

  @Test
  public void getActivationExplanation_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getActivationExplanation();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setActivationExplanation_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String activationExplanation = null;
    target.setActivationExplanation(activationExplanation);
  }

  @Test
  public void getDeactivationDate_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date actual = target.getDeactivationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDeactivationDate_Args__Date() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date deactivationDate = mock(Date.class);
    target.setDeactivationDate(deactivationDate);
  }

  @Test
  public void getDeactivationCountyCode_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer actual = target.getDeactivationCountyCode();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDeactivationCountyCode_Args__Integer() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Integer deactivationCountyCode = null;
    target.setDeactivationCountyCode(deactivationCountyCode);
  }

  @Test
  public void getDeactivationExplanation_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getDeactivationExplanation();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDeactivationExplanation_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String deactivationExplanation = null;
    target.setDeactivationExplanation(deactivationExplanation);
  }

  @Test
  public void getLastUpdatedId_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getLastUpdatedId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastUpdatedId_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String lastUpdatedId = null;
    target.setLastUpdatedId(lastUpdatedId);
  }

  @Test
  public void getLastUpdatedTimestamp_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date actual = target.getLastUpdatedTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastUpdatedTimestamp_Args__Date() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date lastUpdatedTimestamp = mock(Date.class);
    target.setLastUpdatedTimestamp(lastUpdatedTimestamp);
  }

  @Test
  public void getLastUpdatedOperation_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String actual = target.getLastUpdatedOperation();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastUpdatedOperation_Args__String() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    String lastUpdatedOperation = null;
    target.setLastUpdatedOperation(lastUpdatedOperation);
  }

  @Test
  public void getReplicationTimestamp_Args__() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date actual = target.getReplicationTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReplicationTimestamp_Args__Date() throws Exception {
    EsSafetyAlert target = new EsSafetyAlert();
    Date replicationTimestamp = mock(Date.class);
    target.setReplicationTimestamp(replicationTimestamp);
  }

}

