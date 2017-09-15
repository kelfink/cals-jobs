package gov.ca.cwds.jobs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.db2.jcc.am.DatabaseMetaData;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class PersonJobTester {

  protected static final ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

  @BeforeClass
  public static void setupClass() {
    BasePersonIndexerJob.setTestMode(true);
  }

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  ElasticsearchConfiguration esConfig;
  ElasticsearchDao esDao;

  JobOptions opts;
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

  @Before
  public void setup() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSINT");

    // Last run time:
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();

    // JDBC:
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
    when(stmt.executeQuery(any())).thenReturn(rs);

    // DB2 platform and version:
    when(meta.getDatabaseMajorVersion()).thenReturn(11);
    when(meta.getDatabaseMinorVersion()).thenReturn(2);
    when(meta.getDatabaseProductName()).thenReturn("DB2");
    when(meta.getDatabaseProductVersion()).thenReturn("DSN11010");

    // Elasticsearch:
    esDao = mock(ElasticsearchDao.class);
    esConfig = mock(ElasticsearchConfiguration.class);

    when(esDao.getConfig()).thenReturn(esConfig);
    when(esConfig.getElasticsearchAlias()).thenReturn("people");
    when(esConfig.getElasticsearchDocType()).thenReturn("person");

    // Job options:
    opts = mock(JobOptions.class);
    when(opts.isLoadSealedAndSensitive()).thenReturn(false);

    SimpleTestSystemCodeCache.init();
  }

}
