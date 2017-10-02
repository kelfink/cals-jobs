package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.dao.cms.ReplicatedSafetyAlertsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsSafetyAlert;
import gov.ca.cwds.data.persistence.cms.ReplicatedSafetyAlerts;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class SafetyAlertIndexerJobTest {

  private static final ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

  SessionFactory sessionFactory;
  Session session;
  ElasticsearchDao esDao;
  ReplicatedSafetyAlertsDao clientDao;
  String lastJobRunTimeFilename = null;
  SafetyAlertIndexerJob target;
  JobOptions opts;
  ElasticsearchConfiguration esConfig;
  Transaction transaction;

  @Before
  public void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);
    esDao = mock(ElasticsearchDao.class);
    esConfig = mock(ElasticsearchConfiguration.class);
    opts = mock(JobOptions.class);
    transaction = mock(Transaction.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.beginTransaction()).thenReturn(transaction);
    when(opts.isLoadSealedAndSensitive()).thenReturn(false);
    when(esDao.getConfig()).thenReturn(esConfig);
    when(esConfig.getElasticsearchAlias()).thenReturn("people");
    when(esConfig.getElasticsearchDocType()).thenReturn("person");

    SimpleTestSystemCodeCache.init();

    target =
        new SafetyAlertIndexerJob(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(SafetyAlertIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsSafetyAlert.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_LST_SAFETY_ALERT";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy().trim();
    String expected = "ORDER BY CLIENT_ID";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = "SAF_ALRT";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = "CWSINT";
    String actual = target.getInitialLoadQuery(dbSchemaName).replaceAll("  ", " ").trim();
    String expected =
        "SELECT x.* FROM CWSINT.VW_LST_SAFETY_ALERT x WHERE x.CLIENT_SENSITIVITY_IND = 'N' ORDER BY CLIENT_ID FOR READ ONLY WITH UR";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__sealed() throws Exception {
    when(opts.isLoadSealedAndSensitive()).thenReturn(true);
    String dbSchemaName = "CWSINT";
    String actual = target.getInitialLoadQuery(dbSchemaName).replaceAll("  ", " ").trim();
    String expected =
        "SELECT x.* FROM CWSINT.VW_LST_SAFETY_ALERT x ORDER BY CLIENT_ID FOR READ ONLY WITH UR";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<EsSafetyAlert> recs = new ArrayList<EsSafetyAlert>();
    ReplicatedSafetyAlerts actual = target.normalizeSingle(recs);
    ReplicatedSafetyAlerts expected = new ReplicatedSafetyAlerts();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsSafetyAlert> recs = new ArrayList<EsSafetyAlert>();
    List<ReplicatedSafetyAlerts> actual = target.normalize(recs);
    List<ReplicatedSafetyAlerts> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedSafetyAlerts()
      throws Exception {
    ElasticSearchPerson esp = new ElasticSearchPerson();
    ReplicatedSafetyAlerts safetyAlerts = new ReplicatedSafetyAlerts();
    UpdateRequest actual = target.prepareUpsertRequest(esp, safetyAlerts);
    UpdateRequest expected = new UpdateRequest();
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedSafetyAlerts_T__IOException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedSafetyAlerts safetyAlerts = mock(ReplicatedSafetyAlerts.class);
    try {
      target.prepareUpsertRequest(esp, safetyAlerts);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final ResultSet rs = mock(ResultSet.class);
    final EsSafetyAlert actual = target.extract(rs);
    final EsSafetyAlert expected = new EsSafetyAlert();
    expected.setActivationReasonCode(0);
    expected.setActivationCountyCode(0);
    expected.setDeactivationCountyCode(0);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    final ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any(String.class))).thenThrow(new SQLException());
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    SafetyAlertIndexerJob.main(args);
  }

}
