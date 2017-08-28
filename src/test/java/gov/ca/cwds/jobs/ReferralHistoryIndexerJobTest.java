package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.db2.jcc.am.DatabaseMetaData;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.SystemCodesLoaderJob.NsSystemCodeDao;
import gov.ca.cwds.jobs.config.JobOptionsTest;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;

public class ReferralHistoryIndexerJobTest {

  // ====================
  // TEST MEMBERS:
  // ====================

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
  SessionFactory sessionFactory;
  ReplicatedPersonReferralsDao dao;
  ElasticsearchDao esDao;
  File tempFile;
  String lastJobRunTimeFilename;
  ObjectMapper mapper = ElasticSearchPerson.MAPPER;
  ReferralHistoryIndexerJob target;

  @BeforeClass
  public static void setupClass() throws Exception {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);

    Transaction transaction = mock(Transaction.class);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.beginTransaction()).thenReturn(transaction);

    NsSystemCodeDao dao = new NsSystemCodeDao(sessionFactory);
    SimpleTestSystemCodeCache.init();
  }

  @Before
  public void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);

    dao = new ReplicatedPersonReferralsDao(sessionFactory);
    esDao = mock(ElasticsearchDao.class);
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();
    target =
        new ReferralHistoryIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(JobOptionsTest.makeGeneric());

    SessionFactoryOptions sfo = mock(SessionFactoryOptions.class);
    when(sessionFactory.getSessionFactoryOptions()).thenReturn(sfo);

    StandardServiceRegistry reg = mock(StandardServiceRegistry.class);
    when(sfo.getServiceRegistry()).thenReturn(reg);

    ConnectionProvider cp = mock(ConnectionProvider.class);
    when(reg.getService(ConnectionProvider.class)).thenReturn(cp);

    Connection con = mock(Connection.class);
    when(cp.getConnection()).thenReturn(con);

    DatabaseMetaData meta = mock(DatabaseMetaData.class);
    when(con.getMetaData()).thenReturn(meta);
    when(meta.getDatabaseMajorVersion()).thenReturn(11);
    when(meta.getDatabaseMinorVersion()).thenReturn(2);
    when(meta.getDatabaseProductName()).thenReturn("DB2");
    when(meta.getDatabaseProductVersion()).thenReturn("DSN11010");
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
  @Ignore
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_REFERRAL_HIST";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY CLIENT_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = "REFERL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    EsPersonReferral addMe = new EsPersonReferral();
    addMe.setClientId("qz11234567");
    recs.add(addMe);
    ReplicatedPersonReferrals actual = target.normalizeSingle(recs);
    ReplicatedPersonReferrals expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    List<ReplicatedPersonReferrals> actual = target.normalize(recs);
    List<ReplicatedPersonReferrals> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    UpdateRequest actual = target.prepareUpsertRequest(esp, referrals);
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals_T__IOException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    try {
      target.prepareUpsertRequest(esp, referrals);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }

  }

  @Test
  @Ignore
  public void extract_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    EsPersonReferral actual = target.extract(rs);
    EsPersonReferral expected = new EsPersonReferral();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    doThrow(new SQLException()).when(rs).getString(any());
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
    ReferralHistoryIndexerJob.main(args);
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    // String expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = null;
    String actual = target.getInitialLoadQuery(dbSchemaName);
    // String expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void pullRange_Args__Pair() throws Exception {
    Pair<String, String> p = mock(Pair.class);
    target.pullRange(p);
  }

  @Test
  @Ignore
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadExtractJdbc();
  }

  @Test
  @Ignore
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void getPartitionRanges_Args__() throws Exception {
    List actual = target.getPartitionRanges();
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
    ResultSet rs = mock(ResultSet.class);
    EsPersonReferral actual = target.extractReferral(rs);
    EsPersonReferral expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extractReferral_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any())).thenThrow(new SQLException());
    try {
      target.extractReferral(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void extractAllegation_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    EsPersonReferral actual = target.extractAllegation(rs);
    EsPersonReferral expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extractAllegation_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any())).thenThrow(new SQLException());
    try {
      target.extractAllegation(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

}
