package gov.ca.cwds.jobs;

import static com.jayway.awaitility.Awaitility.await;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StringType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.db2.jcc.am.DatabaseMetaData;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.schedule.LaunchScheduler;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class PersonJobTester<T extends PersistentObject, M extends ApiGroupNormalizer<?>> {

  protected static final ObjectMapper MAPPER = ObjectMapperUtils.createObjectMapper();

  protected static final ObjectMapper mapper = MAPPER;

  public static final String DEFAULT_CLIENT_ID = "abc1234567";

  public static final AtomicBoolean isRunwayClear = new AtomicBoolean(false);

  public static final Lock lock = new ReentrantLock();

  @BeforeClass
  public static void setupClass() {
    LaunchCommand.setTestMode(true);
    SimpleTestSystemCodeCache.init();
    ElasticTransformer.setMapper(MAPPER);
  }

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  public LaunchScheduler neutronScheduler;
  public ElasticsearchConfiguration esConfig;
  public ElasticsearchDao esDao;
  public Client client;
  public ElasticSearchPerson esp;

  public FlightPlan opts;
  public File tempFile;
  public File jobConfigFile;
  public File esConfileFile;
  public String lastJobRunTimeFilename;
  public java.util.Date lastRunTime = new java.util.Date();
  public FlightRecord track;
  public FlightRecorder jobHistory;

  public SessionFactory sessionFactory;
  public Session session;
  public EntityManager em;
  public SessionFactoryOptions sfo;
  public Transaction transaction;
  public StandardServiceRegistry reg;
  public ConnectionProvider cp;
  public Connection con;
  public Statement stmt;
  public ResultSet rs;
  public DatabaseMetaData meta;
  public NativeQuery<M> nq;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    System.setProperty("DB_CMS_SCHEMA", "CWSRS1");

    // Last run time:
    tempFile = tempFolder.newFile("tempFile.txt");
    jobConfigFile = tempFolder.newFile("jobConfigFile.yml");
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
    when(session.getTransaction()).thenReturn(transaction);
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
    final Settings settings = Settings.builder().put(mapSettings).build();
    when(client.settings()).thenReturn(settings);

    when(esConfig.getElasticsearchAlias()).thenReturn("people");
    when(esConfig.getElasticsearchDocType()).thenReturn("person");

    // Job options:
    esConfileFile = tempFolder.newFile("es.yml");
    opts = mock(FlightPlan.class);
    esp = new ElasticSearchPerson();

    when(opts.isLoadSealedAndSensitive()).thenReturn(false);
    when(opts.getEsConfigLoc()).thenReturn(esConfileFile.getAbsolutePath());

    nq = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(nq);
    when(nq.setString(any(String.class), any(String.class))).thenReturn(nq);
    when(nq.setParameter(any(String.class), any(String.class), any(StringType.class)))
        .thenReturn(nq);
    when(nq.setFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(nq.setHibernateFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(nq.setReadOnly(any(Boolean.class))).thenReturn(nq);
    when(nq.setCacheMode(any(CacheMode.class))).thenReturn(nq);
    when(nq.setFetchSize(any(Integer.class))).thenReturn(nq);
    when(nq.setCacheable(any(Boolean.class))).thenReturn(nq);

    // Job track:
    track = new FlightRecord();
    jobHistory = new FlightRecorder();
    neutronScheduler = mock(LaunchScheduler.class);

    markTestDone();
  }

  public Thread runKillThread(final BasePersonIndexerJob<T, M> target, long sleepMillis) {
    final Thread t = new Thread(() -> {
      try {
        lock.lockInterruptibly();
        await("kill thread").atMost(sleepMillis, TimeUnit.MILLISECONDS).untilTrue(isRunwayClear);
        target.done();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
      }
    });

    t.start();
    return t;
  }

  public Thread runKillThread(final BasePersonIndexerJob<T, M> target) {
    return runKillThread(target, 1100L);
  }

  public void markTestDone() {
    isRunwayClear.set(true);
  }

}
