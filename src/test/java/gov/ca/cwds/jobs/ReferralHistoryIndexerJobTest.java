package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.db2.jcc.DB2SystemMonitor;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.config.JobOptionsTest;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;

public class ReferralHistoryIndexerJobTest
    extends PersonJobTester<ReplicatedPersonReferrals, EsPersonReferral> {

  public static class TestReferralHistoryIndexerJob extends ReferralHistoryIndexerJob {

    private boolean fakePull = true;
    private boolean throwOnRanges = false;

    public TestReferralHistoryIndexerJob(ReplicatedPersonReferralsDao clientDao,
        ElasticsearchDao esDao, String lastJobRunTimeFilename, ObjectMapper mapper,
        SessionFactory sessionFactory) {
      super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    }

    public static DB2SystemMonitor monitorStart(final Connection con) {
      return new TestDB2SystemMonitor();
    }

    @Override
    protected int pullRange(Pair<String, String> p) {
      return fakePull ? 0 : super.pullRange(p);
    }

    @Override
    protected List<Pair<String, String>> getPartitionRanges() {
      if (throwOnRanges) {
        throw new JobsException("test");
      }
      return super.getPartitionRanges();
    }

  }

  public static final String DEFAULT_REFERRAL_ID = "ref1234567";

  public static final String DEFAULT_ALLEGATION_ID = "alg1234567";

  // ====================
  // TEST MEMBERS:
  // ====================

  ReplicatedPersonReferralsDao dao;
  TestReferralHistoryIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedPersonReferralsDao(sessionFactory);
    target = new TestReferralHistoryIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory);
    target.setOpts(JobOptionsTest.makeGeneric());
  }

  @Test
  public void type() throws Exception {
    assertThat(ReferralHistoryIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsPersonReferral.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_REFRL_ONLY";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy().trim();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = "REFERL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    EsPersonReferral addMe = new EsPersonReferral();
    addMe.setClientId("qz11234567");
    addMe.setReferralId("abc1234567");
    recs.add(addMe);
    ReplicatedPersonReferrals actual = target.normalizeSingle(recs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    List<ReplicatedPersonReferrals> actual = target.normalize(recs);
    List<ReplicatedPersonReferrals> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals()
      throws Exception {
    final ReplicatedPersonReferrals referrals = new ReplicatedPersonReferrals(DEFAULT_CLIENT_ID);

    final ElasticSearchPersonReferral ref = new ElasticSearchPersonReferral();
    ref.setId(DEFAULT_CLIENT_ID);

    final ElasticSearchPersonAllegation allegation = new ElasticSearchPersonAllegation();
    allegation.setId(DEFAULT_ALLEGATION_ID);

    referrals.addReferral(ref, allegation);
    UpdateRequest actual = target.prepareUpsertRequest(esp, referrals);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals_T__Exception()
      throws Exception {
    final ReplicatedPersonReferrals referrals = new ReplicatedPersonReferrals(DEFAULT_CLIENT_ID);
    when(esDao.getConfig().getElasticsearchAlias()).thenThrow(new JobsException("test"));
    try {
      target.prepareUpsertRequest(esp, referrals);
      fail("Expected exception was not thrown!");
    } catch (Exception e) {
    }
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final EsPersonReferral actual = target.extract(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    doThrow(new SQLException()).when(rs).getString(any());
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = "CWSRS1";
    String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  public void getInitialLoadQuery_Args__no_sealed() throws Exception {
    String dbSchemaName = "CWSRS1";
    target.getOpts().setLoadSealedAndSensitive(false);
    String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  public void testNormalizeQueryResults() throws Exception {
    final Map<String, EsPersonReferral> mapReferrals = new HashMap<>();
    final List<EsPersonReferral> listReadyToNorm = new ArrayList<>();
    final Map<String, List<MinClientReferral>> mapReferralByClient = new HashMap<>();
    final Map<String, List<EsPersonReferral>> mapAllegationByReferral = new HashMap<>();

    List<EsPersonReferral> allegations = new ArrayList<>();
    List<MinClientReferral> minClientReferrals = new ArrayList<>();

    MinClientReferral minClRef = new MinClientReferral(DEFAULT_CLIENT_ID, DEFAULT_REFERRAL_ID, "N");
    minClientReferrals.add(minClRef);
    mapReferralByClient.put(DEFAULT_CLIENT_ID, minClientReferrals);

    EsPersonReferral ref = new EsPersonReferral();
    ref.setClientId(DEFAULT_CLIENT_ID);
    ref.setReferralId(DEFAULT_REFERRAL_ID);
    ref.setAllegationId("alg1111111");
    ref.setAllegationType(4);
    listReadyToNorm.add(ref);
    allegations.add(ref);
    mapReferrals.put(DEFAULT_REFERRAL_ID, ref);

    ref = new EsPersonReferral();
    ref.setClientId(DEFAULT_CLIENT_ID);
    ref.setReferralId(DEFAULT_REFERRAL_ID);
    ref.setAllegationId("alg2222222");
    ref.setAllegationType(3);
    listReadyToNorm.add(ref);
    allegations.add(ref);
    mapAllegationByReferral.put(ref.getReferralId(), allegations);

    target.normalizeQueryResults(mapReferrals, listReadyToNorm, mapReferralByClient,
        mapAllegationByReferral);
  }

  @Test
  public void testReadClients() throws Exception {
    final PreparedStatement stmtInsClient = mock(PreparedStatement.class);
    final PreparedStatement stmtSelClient = mock(PreparedStatement.class);
    when(stmtSelClient.executeQuery()).thenReturn(rs);

    final List<MinClientReferral> listClientReferralKeys = new ArrayList<>();

    when(rs.getString("FKCLIENT_T")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("FKREFERL_T")).thenReturn(DEFAULT_REFERRAL_ID);
    when(rs.getString("SENSTV_IND")).thenReturn("N");

    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.readClients(stmtInsClient, stmtSelClient, listClientReferralKeys, p);
  }

  @Test
  public void testReadAllegations() throws Exception {
    final PreparedStatement stmtSelAllegation = mock(PreparedStatement.class);
    when(stmtSelAllegation.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true).thenReturn(false);

    final List<EsPersonReferral> listAllegations = new ArrayList<>();
    target.readAllegations(stmtSelAllegation, listAllegations);
  }

  @Test
  public void testMonitorStopAndReport() throws Exception {
    JobDB2Utils.monitorStopAndReport(new TestDB2SystemMonitor());
  }

  @Test
  public void testCalcThreads() throws Exception {
    JobJdbcUtils.calcReaderThreads(target.getOpts());
  }

  @Test
  public void testReadReferrals() throws Exception {
    final PreparedStatement stmtSelReferral = mock(PreparedStatement.class);
    when(stmtSelReferral.executeQuery()).thenReturn(rs);

    final Map<String, EsPersonReferral> mapReferrals = new HashMap<>();
    target.readReferrals(stmtSelReferral, mapReferrals);
  }

  @Test
  public void pullRange_Args__Pair() throws Exception {
    final String schema = target.getDBSchemaName();

    final PreparedStatement stmtInsClient = mock(PreparedStatement.class);
    final PreparedStatement stmtSelClient = mock(PreparedStatement.class);
    final PreparedStatement stmtSelReferral = mock(PreparedStatement.class);
    final PreparedStatement stmtSelAllegation = mock(PreparedStatement.class);

    final ResultSet rsInsClient = mock(ResultSet.class);
    final ResultSet rsSelClient = mock(ResultSet.class);
    final ResultSet rsSelReferral = mock(ResultSet.class);
    final ResultSet rsSelAllegation = mock(ResultSet.class);

    final String sqlInsClient =
        target.INSERT_CLIENT_FULL.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim();
    final String sqlSelClient =
        target.SELECT_CLIENT.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim();
    final String sqlSelReferral = target.getInitialLoadQuery(schema).replaceAll("\\s+", " ").trim();
    final String selAllegation =
        target.SELECT_ALLEGATION.replaceAll("#SCHEMA#", schema).replaceAll("\\s+", " ").trim();

    when(con.prepareStatement(sqlInsClient)).thenReturn(stmtInsClient);
    when(con.prepareStatement(sqlSelClient)).thenReturn(stmtSelClient);
    when(con.prepareStatement(sqlSelReferral)).thenReturn(stmtSelReferral);
    when(con.prepareStatement(selAllegation)).thenReturn(stmtSelAllegation);

    when(stmtInsClient.executeQuery()).thenReturn(rsInsClient);
    when(stmtSelClient.executeQuery()).thenReturn(rsSelClient);
    when(stmtSelReferral.executeQuery()).thenReturn(rsSelReferral);
    when(stmtSelAllegation.executeQuery()).thenReturn(rsSelAllegation);

    when(rsInsClient.next()).thenReturn(true).thenReturn(false);
    when(rsSelClient.next()).thenReturn(true).thenReturn(false);
    when(rsSelReferral.next()).thenReturn(false);
    when(rsSelAllegation.next()).thenReturn(false);

    when(rsSelClient.getString("FKCLIENT_T")).thenReturn(DEFAULT_CLIENT_ID);
    when(rsSelClient.getString("FKREFERL_T")).thenReturn(DEFAULT_REFERRAL_ID);
    when(rsSelClient.getString("SENSTV_IND")).thenReturn("N");

    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.fakePull = false;
    target.pullRange(p);
  }

  @Test(expected = JobsException.class)
  public void pullRange_Args__Pair_throw() throws Exception {
    final String schema = target.getDBSchemaName();
    when(con.prepareStatement(any())).thenThrow(JobsException.class);

    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.fakePull = false;
    target.pullRange(p);
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadRetrieveByJdbc();
  }

  @Test(expected = JobsException.class)
  public void threadExtractJdbc_Args__throw() throws Exception {
    target.throwOnRanges = true;
    target.threadRetrieveByJdbc();
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final List actual = target.getPartitionRanges();
    assertThat(actual, notNullValue());
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractReferral_Args__ResultSet() throws Exception {
    EsPersonReferral actual = target.extractReferral(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void extractReferral_Args__ResultSet_T__SQLException() throws Exception {
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any(Integer.class))).thenThrow(new SQLException());
    try {
      target.extractReferral(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void extractAllegation_Args__ResultSet() throws Exception {
    EsPersonReferral actual = target.extractAllegation(rs);
    assertThat(actual, notNullValue());
  }

  @Test(expected = SQLException.class)
  public void extractAllegation_Args__ResultSet_T__SQLException() throws Exception {
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any(Integer.class))).thenThrow(new SQLException());
    target.extractAllegation(rs);
  }

  @Test
  public void isRangeSelfManaging_Args__() throws Exception {
    boolean actual = target.providesInitialKeyRanges();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getConnection_Args__() throws Exception {
    Connection actual = target.getConnection();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getConnection_Args___T__SQLException() throws Exception {
    when(cp.getConnection()).thenThrow(SQLException.class);
    try {
      target.getConnection();
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getConnection_Args___T__InterruptedException() throws Exception {
    when(cp.getConnection()).thenThrow(InterruptedException.class);
    try {
      target.getConnection();
      fail("Expected exception was not thrown!");
    } catch (InterruptedException e) {
    }
  }

  @Test
  public void allocateThreadMemory_Args__() throws Exception {
    target.allocateThreadMemory();
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    ReferralHistoryIndexerJob.main(args);
  }

  @Test
  public void getPrepLastChangeSQL_Args__() throws Exception {
    String actual = target.getPrepLastChangeSQL();
    assertThat(actual, notNullValue());
  }

}
