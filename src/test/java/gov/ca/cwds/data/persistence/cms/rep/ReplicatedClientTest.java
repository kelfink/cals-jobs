package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonAka;
import gov.ca.cwds.data.es.ElasticSearchRaceAndEthnicity;
import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.jobs.Goddard;

public class ReplicatedClientTest extends Goddard<ReplicatedClient, EsClientAddress> {
  ReplicatedClient target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new ReplicatedClient();
    target.setId(DEFAULT_CLIENT_ID);
  }

  @Test
  public void testReplicationOperation() throws Exception {
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClient.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getClientAddresses_Args__() throws Exception {
    Set<ReplicatedClientAddress> actual = target.getClientAddresses();
    Set<ReplicatedClientAddress> expected = new HashSet<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientAddresses_Args__Set() throws Exception {
    Set<ReplicatedClientAddress> clientAddresses = mock(Set.class);
    target.setClientAddresses(clientAddresses);
  }

  @Test
  public void addClientAddress_Args__ReplicatedClientAddress() throws Exception {
    ReplicatedClientAddress clientAddress = mock(ReplicatedClientAddress.class);
    target.addClientAddress(clientAddress);
  }

  @Test
  public void getAddresses_Args__() throws Exception {
    List<ElasticSearchPersonAddress> actual = target.getElasticSearchPersonAddresses();
    List<ElasticSearchPersonAddress> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPhones_Args__() throws Exception {
    ApiPhoneAware[] actual = target.getPhones();
    ApiPhoneAware[] expected = new ApiPhoneAware[0];
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    String actual = target.getLegacyId();
    String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
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

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    Date lastUpdatedTime = new Date();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getClientCounty_Args__() throws Exception {
    Short actual = target.getClientCounty();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientCounty_Args__Short() throws Exception {
    Short clinetCountyId = null;
    target.setClientCounty(clinetCountyId);
  }

  @Test
  public void getClientRaces_Args__() throws Exception {
    List<Short> actual = target.getClientRaces();
    List<Short> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientRaces_Args__List() throws Exception {
    List<Short> clientRaces = new ArrayList<>();
    target.setClientRaces(clientRaces);
  }

  @Test
  public void addClientRace_Args__Short() throws Exception {
    Short clientRace = null;
    target.addClientRace(clientRace);
  }

  @Test
  public void getReplicatedEntity_Args__() throws Exception {
    EmbeddableCmsReplicatedEntity actual = target.getReplicatedEntity();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getRaceAndEthnicity_Args__() throws Exception {
    final List<Short> clientRaces = new ArrayList<>();
    clientRaces.add((short) 825);
    clientRaces.add((short) 824);
    clientRaces.add((short) 3164);
    target.setClientRaces(clientRaces);
    ElasticSearchRaceAndEthnicity actual = target.getRaceAndEthnicity();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getAkas_Args__() throws Exception {
    Map<String, ElasticSearchPersonAka> actual = target.getAkas();
    Map<String, ElasticSearchPersonAka> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkas_Args__Map() throws Exception {
    Map<String, ElasticSearchPersonAka> akas = new HashMap<>();
    target.setAkas(akas);
  }

  @Test
  public void addAka_Args__ElasticSearchPersonAka() throws Exception {
    ElasticSearchPersonAka aka = mock(ElasticSearchPersonAka.class);
    target.addAka(aka);
  }

  @Test
  public void getSafetyAlerts_Args__() throws Exception {
    Map<String, ElasticSearchSafetyAlert> actual = target.getSafetyAlerts();
    Map<String, ElasticSearchSafetyAlert> expected = new HashMap<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlerts_Args__Map() throws Exception {
    Map<String, ElasticSearchSafetyAlert> safetyAlerts = new HashMap<>();
    target.setSafetyAlerts(safetyAlerts);
  }

  @Test
  public void addSafetyAlert_Args__ElasticSearchSafetyAlert() throws Exception {
    ElasticSearchSafetyAlert safetyAlert = mock(ElasticSearchSafetyAlert.class);
    target.addSafetyAlert(safetyAlert);
  }

  @Test
  public void getOpenCaseId_Args__() throws Exception {
    String actual = target.getOpenCaseId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOpenCaseId_Args__String() throws Exception {
    String openCaseId = null;
    target.setOpenCaseId(openCaseId);
  }

  @Test
  public void getOtherClientNames_Args__() throws Exception {
    List<ElasticSearchPersonAka> actual = target.getOtherClientNames();
    List<ElasticSearchPersonAka> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClientSafetyAlerts_Args__() throws Exception {
    List<ElasticSearchSafetyAlert> actual = target.getClientSafetyAlerts();
    List<ElasticSearchSafetyAlert> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

}
