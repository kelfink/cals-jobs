package gov.ca.cwds.jobs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
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
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.component.NeutronIntegerDefaults;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class PersonJobTester<T extends PersistentObject, M extends ApiGroupNormalizer<?>> {

  protected static final ObjectMapper MAPPER = ObjectMapperUtils.createObjectMapper();

  public static final String DEFAULT_CLIENT_ID = "abc1234567";

  @BeforeClass
  public static void setupClass() {
    JobRunner.setTestMode(true);
    SimpleTestSystemCodeCache.init();
    ElasticTransformer.setMapper(MAPPER);
  }

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  ElasticsearchConfiguration esConfig;
  ElasticsearchDao esDao;
  Client client;
  ElasticSearchPerson esp;

  JobOptions opts;
  File tempFile;
  File esConfileFile;
  String lastJobRunTimeFilename;
  java.util.Date lastRunTime = new java.util.Date();

  protected SessionFactory sessionFactory;
  protected Session session;
  protected EntityManager em;
  protected SessionFactoryOptions sfo;
  protected Transaction transaction;
  StandardServiceRegistry reg;
  ConnectionProvider cp;
  protected Connection con;
  protected Statement stmt;
  protected ResultSet rs;
  DatabaseMetaData meta;

  @Before
  public void setup() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRS1");

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
    em = mock(EntityManager.class);
    client = mock(Client.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(sessionFactory.createEntityManager()).thenReturn(em);
    when(session.beginTransaction()).thenReturn(transaction);
    when(sessionFactory.getSessionFactoryOptions()).thenReturn(sfo);
    when(sfo.getServiceRegistry()).thenReturn(reg);
    when(reg.getService(ConnectionProvider.class)).thenReturn(cp);
    when(cp.getConnection()).thenReturn(con);
    when(con.getMetaData()).thenReturn(meta);
    when(con.createStatement()).thenReturn(stmt);
    when(stmt.executeQuery(any())).thenReturn(rs);

    // Result set:
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getString(any())).thenReturn("abc123456789");
    when(rs.getString(contains("IBMSNAP_OPERATION"))).thenReturn("I");
    when(rs.getString("LIMITED_ACCESS_CODE")).thenReturn("N");
    when(rs.getInt(any())).thenReturn(0);

    final java.util.Date date = new java.util.Date();
    final Timestamp ts = new Timestamp(date.getTime());
    when(rs.getDate(any())).thenReturn(new Date(date.getTime()));
    when(rs.getTimestamp("LIMITED_ACCESS_CODE")).thenReturn(ts);
    when(rs.getTimestamp(any())).thenReturn(ts);

    // DB2 platform and version:
    when(meta.getDatabaseMajorVersion()).thenReturn(11);
    when(meta.getDatabaseMinorVersion()).thenReturn(2);
    when(meta.getDatabaseProductName()).thenReturn("DB2");
    when(meta.getDatabaseProductVersion()).thenReturn("DSN11010");

    // Elasticsearch:
    esDao = mock(ElasticsearchDao.class);
    esConfig = mock(ElasticsearchConfiguration.class);

    when(esDao.getConfig()).thenReturn(esConfig);
    when(esDao.getClient()).thenReturn(client);

    final Map<String, String> mapSettings = new HashMap<>();
    final Settings settings = Settings.builder().put(mapSettings).build();;
    when(client.settings()).thenReturn(settings);

    when(esConfig.getElasticsearchAlias()).thenReturn("people");
    when(esConfig.getElasticsearchDocType()).thenReturn("person");

    // Job options:
    esConfileFile = tempFolder.newFile("es.yml");
    opts = mock(JobOptions.class);
    esp = new ElasticSearchPerson();

    when(opts.isLoadSealedAndSensitive()).thenReturn(false);
    when(opts.getEsConfigLoc()).thenReturn(esConfileFile.getAbsolutePath());
  }

  public void runKillThread(final BasePersonIndexerJob<T, M> target) {
    new Thread(() -> {
      try {
        Thread.sleep(1100); // NOSONAR
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        target.markJobDone();
      }

    }).start();
  }

  public void sleepItOff() {
    try {
      Thread.yield();
      Thread.sleep(NeutronIntegerDefaults.SLEEP_MILLIS.getValue()); // NOSONAR
    } catch (Exception e) {
    }
  }

}
