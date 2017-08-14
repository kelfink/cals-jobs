package gov.ca.cwds.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob extends BasePersonIndexerJob<ReplicatedClient, EsClientAddress>
    implements JobResultSetAware<EsClientAddress> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientIndexerJob.class);

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ClientIndexerJob(final ReplicatedClientDao clientDao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public EsClientAddress extract(ResultSet rs) throws SQLException {
    return EsClientAddress.extract(rs);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsClientAddress.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "MQT_CLIENT_ADDRESS";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.clt_identifier ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ");
    buf.append(dbSchemaName);
    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" x WHERE x.clt_identifier > ':fromId' AND ':toId' <= x.clt_identifier ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" AND x.CLT_SENSTV_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  protected void runJdbcThread(final Pair<String, String> p) {
    final int i = nextThreadNum.getAndIncrement();
    Thread.currentThread().setName("extract_" + i);
    LOGGER.info("BEGIN: Stage #1: extract " + i);

    try {
      Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      con.setReadOnly(true); // WARNING: fails with Postgres.

      // Linux MQT lacks ORDER BY clause. Must sort manually.
      // Either detect platform or force ORDER BY clause.
      final Pair<String, String> pair = getPartitionRanges().get(i);
      final String query = getInitialLoadQuery(getDBSchemaName())
          .replaceAll(":fromId", pair.getLeft()).replaceAll(":toId", pair.getRight());
      EsClientAddress m;

      enableParallelism(con);

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(15000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(100000);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        int cntr = 0;
        while (!fatalError && rs.next()) {
          // Hand the baton to the next runner ...
          JobLogUtils.logEvery(++cntr, "Retrieved", "recs");
          if ((m = extract(rs)) != null) {
            queueTransform.putLast(m);
          }
        }

        con.commit();
      } finally {
        // The statement closes automatically.
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.info("DONE: Extract " + i);
  }

  private Thread startExtractThread(final Pair<String, String> p) {
    Runnable runner = () -> { // NOSONAR
      runJdbcThread(p);
    };

    Thread t = new Thread(runner);
    t.start();
    return t;
  }

  private void waitOnThread(Thread t) { // NOSONAR
    try {
      t.join();
    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.warn("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    } finally {
      doneExtract = true;
    }
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.info("BEGIN: main extract");

    try {
      getPartitionRanges().stream().sequential().map(this::startExtractThread)
          .forEach(this::waitOnThread);
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: Stage #1: Extract");
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    // z/OS:
    ret.add(Pair.of(" ", "B3bMRWu8NV"));
    ret.add(Pair.of("B3bMRWu8NV", "DW5GzxJ30A"));
    ret.add(Pair.of("DW5GzxJ30A", "FNOBbaG6qq"));
    ret.add(Pair.of("FNOBbaG6qq", "HJf1EJe25X"));
    ret.add(Pair.of("HJf1EJe25X", "JCoyq0Iz36"));
    ret.add(Pair.of("JCoyq0Iz36", "LvijYcj01S"));
    ret.add(Pair.of("LvijYcj01S", "Npf4LcB3Lr"));
    ret.add(Pair.of("Npf4LcB3Lr", "PiJ6a0H49S"));
    ret.add(Pair.of("PiJ6a0H49S", "RbL4aAL34A"));
    ret.add(Pair.of("RbL4aAL34A", "S3qiIdg0BN"));
    ret.add(Pair.of("S3qiIdg0BN", "0Ltok9y5Co"));
    ret.add(Pair.of("0Ltok9y5Co", "2CFeyJd49S"));
    ret.add(Pair.of("2CFeyJd49S", "4w3QDw136B"));
    ret.add(Pair.of("4w3QDw136B", "6p9XaHC10S"));
    ret.add(Pair.of("6p9XaHC10S", "8jw5J580MQ"));
    ret.add(Pair.of("8jw5J580MQ", "9999999999"));

    return ret;
  }

  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    /**
     * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
     * sensitive flag must be deleted.
     */
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected List<ReplicatedClient> normalize(List<EsClientAddress> recs) {
    return EntityNormalizer.<ReplicatedClient, EsClientAddress>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ClientIndexerJob.class, args);
  }

}
