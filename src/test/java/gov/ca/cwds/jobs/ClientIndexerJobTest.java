package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ClientIndexerJobTest {

  private static final ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

  @BeforeClass
  public static void setupClass() {
    BasePersonIndexerJob.setTestMode(true);
    System.setProperty("DB_CMS_SCHEMA", "CWSINT");
  }

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  ReplicatedClientDao dao;
  ElasticsearchDao esDao;
  JobOptions opts;
  ElasticsearchConfiguration esConfig;
  File tempFile;
  String lastJobRunTimeFilename;

  SessionFactory sessionFactory;
  Session session;
  SessionFactoryOptions sfo;
  Transaction transaction;
  StandardServiceRegistry reg;
  ConnectionProvider cp;
  Connection con;
  Statement stmt;
  ResultSet rs;
  DatabaseMetaData meta;

  ClientIndexerJob target;

  @Before
  public void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();

    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);
    transaction = mock(Transaction.class);
    sfo = mock(SessionFactoryOptions.class);
    reg = mock(StandardServiceRegistry.class);
    cp = mock(ConnectionProvider.class);
    con = mock(Connection.class);
    rs = mock(ResultSet.class);
    meta = mock(DatabaseMetaData.class);
    stmt = mock(Statement.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.beginTransaction()).thenReturn(transaction);
    when(sessionFactory.getSessionFactoryOptions()).thenReturn(sfo);
    when(sfo.getServiceRegistry()).thenReturn(reg);
    when(reg.getService(ConnectionProvider.class)).thenReturn(cp);
    when(cp.getConnection()).thenReturn(con);
    when(con.getMetaData()).thenReturn(meta);
    when(con.createStatement()).thenReturn(stmt);
    when(meta.getDatabaseMajorVersion()).thenReturn(11);
    when(meta.getDatabaseMinorVersion()).thenReturn(2);
    when(meta.getDatabaseProductName()).thenReturn("DB2");
    when(meta.getDatabaseProductVersion()).thenReturn("DSN11010");
    when(stmt.executeQuery(any())).thenReturn(rs);

    opts = mock(JobOptions.class);
    esDao = mock(ElasticsearchDao.class);
    esConfig = mock(ElasticsearchConfiguration.class);
    when(opts.isLoadSealedAndSensitive()).thenReturn(false);
    when(esDao.getConfig()).thenReturn(esConfig);
    when(esConfig.getElasticsearchAlias()).thenReturn("people");
    when(esConfig.getElasticsearchDocType()).thenReturn("person");

    SimpleTestSystemCodeCache.init();

    dao = new ReplicatedClientDao(sessionFactory);
    target = new ClientIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(ClientIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    target = new ClientIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args$StringArray() throws Exception {
    final String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__StringArray__t_je() throws Exception {
    String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__bucket_range() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "21-22", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__bucket_range_not_digit() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "abc-xyz", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final EsClientAddress actual = target.extract(rs);
    final EsClientAddress expected = new EsClientAddress();

    final short s = (short) 0;
    expected.setCltBirthCountryCodeType(s);
    expected.setCltBirthStateCodeType(s);
    expected.setCltDriverLicenseStateCodeType(s);
    expected.setCltImmigrationCountryCodeType(s);
    expected.setCltMaritalStatusType(s);
    expected.setCltNameType(s);
    expected.setCltPrimaryEthnicityType(s);
    expected.setCltPrimaryLanguageType(s);
    expected.setCltSecondaryLanguageType(s);
    expected.setCltReligionType(s);
    expected.setClaAddressType(s);
    expected.setClaAddressType(s);
    expected.setClaAddressType(s);
    expected.setAdrEmergencyExtension(0);
    expected.setAdrPrimaryExtension(0);
    expected.setAdrState(s);
    expected.setAdrZip4(s);
    expected.setAdrUnitDesignationCd(s);
    // expected.setAdrPostDirCd(0);

    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsClientAddress.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CLIENT_ADDRESS";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsClientAddress> recs = new ArrayList<EsClientAddress>();
    List<ReplicatedClient> actual = target.normalize(recs);
    List<ReplicatedClient> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CLIENT_ADDRESS";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    final String actual = target.getJdbcOrderBy().trim().toUpperCase();
    final String expected = "ORDER BY X.CLT_IDENTIFIER";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    final String actual =
        target.getInitialLoadQuery("CWSINT").trim().replace("\\s{2,}", " ").replaceAll("  ", " ");
    final String expected =
        "SELECT x.* FROM CWSINT.MQT_CLIENT_ADDRESS x WHERE x.clt_identifier > ':fromId' AND x.clt_identifier <= ':toId' AND x.CLT_SENSTV_IND = 'N' ORDER BY x.clt_identifier FOR READ ONLY WITH UR";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handOff_Args__List() throws Exception {
    List<EsClientAddress> grpRecs = new ArrayList<EsClientAddress>();
    target.handOff(grpRecs);
  }

  @Test
  public void pullRange_Args__Pair() throws Exception {
    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.pullRange(p);
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadExtractJdbc();
  }

  @Test
  public void getPartitionRanges_Args() throws Exception {
    final List actual = target.getPartitionRanges();
    final List expected = new ArrayList<>();
    expected.add(Pair.of("aaaaaaaaaa", "9999999999"));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
