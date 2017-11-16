package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;

public class ReplicatedSafetyAlertsTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedSafetyAlerts.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    assertThat(target, notNullValue());
  }

  @Test
  public void addSafetyAlert_Args__ElasticSearchSafetyAlert() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    ElasticSearchSafetyAlert safetyAlert = mock(ElasticSearchSafetyAlert.class);
    target.addSafetyAlert(safetyAlert);
  }

  @Test
  public void getSafetyAlerts_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    List<ElasticSearchSafetyAlert> actual = target.getSafetyAlerts();
    List<ElasticSearchSafetyAlert> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlerts_Args__List() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    List<ElasticSearchSafetyAlert> safetyAlerts = new ArrayList<ElasticSearchSafetyAlert>();
    target.setSafetyAlerts(safetyAlerts);
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    ReplicatedSafetyAlerts target = new ReplicatedSafetyAlerts();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
