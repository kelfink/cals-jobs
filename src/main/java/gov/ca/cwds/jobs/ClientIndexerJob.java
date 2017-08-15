package gov.ca.cwds.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
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

  /**
   * Hand off all recs for same client id at same time.
   * 
   * @param grpRecs recs for same client id
   */
  protected void handOff(List<EsClientAddress> grpRecs) {
    try {
      lock.readLock().unlock();
      lock.writeLock().lock();
      for (EsClientAddress cla : grpRecs) {
        LOGGER.trace("lock: queueTransform.putLast: client id {}", cla.getCltId());
        queueTransform.putLast(cla);
      }
      lock.readLock().lock();
    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.warn("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Read recs from a single partition.
   * 
   * @param p partition range to read
   */
  protected void extractPartitionRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    Thread.currentThread().setName("extract_" + i);
    LOGGER.warn("BEGIN: extract thread " + i);

    try {
      Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      con.setReadOnly(true); // WARNING: fails with Postgres.

      final String query = getInitialLoadQuery(getDBSchemaName()).replaceAll(":fromId", p.getLeft())
          .replaceAll(":toId", p.getRight());
      enableParallelism(con);

      int cntr = 0;
      EsClientAddress m;
      Object lastId = new Object();
      List<EsClientAddress> grpRecs = new ArrayList<>();

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(0);

        final ResultSet rs = stmt.executeQuery(query); // NOSONAR
        lock.readLock().lock();

        while (!fatalError && rs.next() && (m = extract(rs)) != null) {
          // Hand the baton to the next runner ...
          JobLogUtils.logEvery(++cntr, "Retrieved", "recs");
          // NOTE: Assumes that records are sorted by group key.
          if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1) {
            handOff(grpRecs);
            grpRecs.clear(); // Single thread, re-use memory.
          }

          grpRecs.add(m);
          lastId = m.getNormalizationGroupKey();
          // Thread.yield();
        }

        con.commit();
      } finally {
        // Statement closes automatically.
        lock.readLock().unlock();

        // Close connection, return to pool?
        jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
            .getService(ConnectionProvider.class).closeConnection(con);
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.warn("DONE: Extract thread " + i);
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");

    try {
      // final int maxThreads = Math.min(Runtime.getRuntime().availableProcessors(), 2);
      // System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
      // String.valueOf(maxThreads));
      // LOGGER.info("JDBC processors={}", maxThreads);

      // ForkJoinPool forkJoinPool = new ForkJoinPool(1);
      // forkJoinPool
      // .submit(() -> getPartitionRanges().parallelStream().forEach(this::extractPartitionRange));

      // CHALLENGE: parallel stream blocks the indexer thread somehow. Weird.
      // But this "roll your own" approach works fine. Go figure.
      final ForkJoinPool pool = new ForkJoinPool(1);
      for (Pair<String, String> pair : getPartitionRanges()) {
        LOGGER.info("submit partition pair: {},{}", pair.getLeft(), pair.getRight());
        pool.submit(() -> extractPartitionRange(pair));
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: main extract thread");
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    // z/OS:
    ret.add(Pair.of("aaaaaaaaaa", "IDPEFPYCqG"));
    ret.add(Pair.of("AecfYcR6ob", "Jazpdsz9Bz"));
    ret.add(Pair.of("AhToAin36B", "JGwYL7F6vU"));
    ret.add(Pair.of("AlLFYp730A", "JKkhRsT2PS"));
    ret.add(Pair.of("ApFkob8AAz", "Kc6lWFcDiO"));
    ret.add(Pair.of("Auw35HvJPy", "La3VJ8m6ec"));
    ret.add(Pair.of("AymVbqn5Cx", "L29oFXo0Z6"));
    ret.add(Pair.of("ACD6P5jA05", "L6XCNzNAkK"));
    ret.add(Pair.of("AG1q7l3JEF", "Ojz475ILTa"));
    ret.add(Pair.of("ALjSCgv9fX", "PcEJx6y4kN"));
    ret.add(Pair.of("AO00JuMBhB", "QJUe02kDpj"));
    ret.add(Pair.of("ASJpjKz15A", "QOdcBAH5Fe"));
    ret.add(Pair.of("AWT9oVI5Cz", "RazJ43h4co"));
    ret.add(Pair.of("A1uQSnF6cS", "SbpLwEj34A"));
    ret.add(Pair.of("A5jjC42BT5", "0cbHoIw2vI"));
    ret.add(Pair.of("A9hZQfK199", "1ay8aNAAEH"));
    ret.add(Pair.of("BdnnwH03hy", "1j03gkN1GK"));
    ret.add(Pair.of("Bg9wqxK3Qu", "2VUctLZ7vE"));
    ret.add(Pair.of("BkUDS2A3hi", "4aZ4n0D44S"));
    ret.add(Pair.of("BpQUJCnC8p", "4vyuDUPKIg"));
    ret.add(Pair.of("BtZ3fJm3F8", "5bYZ6uI196"));
    ret.add(Pair.of("Bx7c0W64nn", "6Tu8gB1LvU"));
    ret.add(Pair.of("BC1AUSX40S", "6XeM5XV36B"));
    ret.add(Pair.of("BGMTtQ9DpJ", "7dS8h5b5om"));
    ret.add(Pair.of("BKziS9T0Nn", "7JBYW3mCtD"));
    ret.add(Pair.of("BOIOd2i6ob", "8azzOhs5TH"));
    ret.add(Pair.of("BSt47iRGvb", "8xDApdEAIP"));
    ret.add(Pair.of("BXnKnpq5jU", "9a0dJR46Nh"));
    ret.add(Pair.of("B1yKivZ36B", "9zgAgBa8Ph"));
    ret.add(Pair.of("B5qgCXk3gw", "9DXBOFI5nJ"));
    ret.add(Pair.of("B95y3Lq10o", "9LRPMQs199"));
    ret.add(Pair.of("U5C2TMFCWB", "9999999999"));

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
