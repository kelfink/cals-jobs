package gov.ca.cwds.jobs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob extends BasePersonIndexerJob<ReplicatedClient, EsClientAddress>
    implements JobResultSetAware<EsClientAddress> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)\n" + "SELECT CLT.IDENTIFIER "
          + "FROM #SCHEMA#.CLIENT_T clt\n" + "WHERE CLT.IBMSNAP_LOGMARKER > ##TIMESTAMP##\n"
          + "UNION\n" + "SELECT CLT.IDENTIFIER " + "FROM #SCHEMA#.CLIENT_T clt\n"
          + "JOIN #SCHEMA#.CL_ADDRT cla ON clt.IDENTIFIER = cla.FKCLIENT_T \n"
          + "WHERE CLA.IBMSNAP_LOGMARKER > ##TIMESTAMP##\n" + "UNION\n" + "SELECT CLT.IDENTIFIER "
          + "FROM #SCHEMA#.CLIENT_T clt\n"
          + "JOIN #SCHEMA#.CL_ADDRT cla ON clt.IDENTIFIER = cla.FKCLIENT_T\n"
          + "JOIN #SCHEMA#.ADDRS_T  adr ON cla.FKADDRS_T  = adr.IDENTIFIER\n"
          + "WHERE ADR.IBMSNAP_LOGMARKER > ##TIMESTAMP##";

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
  public boolean useTransformThread() {
    return false;
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public boolean providesInitialKeyRanges() {
    return true;
  }

  @Override
  public EsClientAddress extract(ResultSet rs) throws SQLException {
    return EsClientAddress.extract(rs);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
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
    final StringBuilder buf = new StringBuilder();

    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.clt_identifier > ':fromId' AND x.clt_identifier <= ':toId' ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" AND x.CLT_SENSTV_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Send all recs for same client id to the index queue.
   * 
   * @param grpRecs recs for same client id
   */
  protected void normalizeAndQueueIndex(final List<EsClientAddress> grpRecs) {
    grpRecs.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential().sorted()
        .collect(Collectors.groupingBy(EsClientAddress::getCltId)).entrySet().stream()
        .map(e -> normalizeSingle(e.getValue())).forEach(this::addToIndexQueue);
  }

  /**
   * Read recs from a single partition.
   * 
   * @param p partition range to read
   */
  protected void pullRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.warn("BEGIN: extract thread {}", threadName);

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);

      final String query = getInitialLoadQuery(getDBSchemaName()).replaceAll(":fromId", p.getLeft())
          .replaceAll(":toId", p.getRight());
      LOGGER.info("query: {}", query);
      JobDB2Utils.enableParallelism(con);

      int cntr = 0;
      EsClientAddress m;
      Object lastId = new Object();
      final List<EsClientAddress> grpRecs = new ArrayList<>();

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(0);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        // NOTE: Assumes that records are sorted by group key.
        while (!isFailed() && rs.next() && (m = extract(rs)) != null) {
          JobLogs.logEvery(++cntr, "Retrieved", "recs");
          if (!lastId.equals(m.getNormalizationGroupKey()) && cntr > 1) {
            normalizeAndQueueIndex(grpRecs);
            grpRecs.clear(); // Single thread, re-use memory.
          }

          grpRecs.add(m);
          lastId = m.getNormalizationGroupKey();
        }

        con.commit();
      }

    } catch (Exception e) {
      markFailed();
      JobLogs.raiseError(LOGGER, e, "FAILED TO PULL RANGE! {}-{} : {}", p.getLeft(), p.getRight(),
          e.getMessage());
    }

    LOGGER.warn("DONE: Extract thread " + i);
  }

  /**
   * The "extract" part of ETL. Single producer, chained consumers.
   */
  @Override
  protected void threadRetrieveByJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");

    // This job normalizes **without** the transform thread.
    markTransformDone();

    try {
      final int maxThreads = JobJdbcUtils.calcReaderThreads(getOpts());
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
          String.valueOf(maxThreads));
      LOGGER.info("JDBC processors={}", maxThreads);
      getPartitionRanges().parallelStream().forEach(this::pullRange);
    } catch (Exception e) {
      markFailed();
      JobLogs.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      markRetrieveDone();
    }

    LOGGER.info("DONE: main extract thread");
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "B3bMRWu8NV"));
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

      ret = limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

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
  public List<ReplicatedClient> normalize(List<EsClientAddress> recs) {
    return EntityNormalizer.<ReplicatedClient, EsClientAddress>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(ClientIndexerJob.class, args);
  }

}
