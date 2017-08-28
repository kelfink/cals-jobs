package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral>
    implements JobResultSetAware<EsPersonReferral> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralHistoryIndexerJob.class);

  private static final String INSERT_CLIENT =
      "INSERT INTO #SCHEMA#.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "SELECT rc.FKREFERL_T, rc.FKCLIENT_T, rc.SENSTV_IND\n"
          + "FROM #SCHEMA#.VW_REFR_CLT rc\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  private static final String SELECT_CLIENT =
      "SELECT FKCLIENT_T, FKREFERL_T, SENSTV_IND FROM #SCHEMA#.GT_REFR_CLT";

  private static final String SELECT_ALLEGATION =
      "SELECT vw.* FROM #SCHEMA#.VW_MQT_ALGTN_ONLY vw FOR READ ONLY WITH UR";

  private static final int FETCH_SIZE = 5000;

  private static class MinClientReferral implements ApiMarker {
    String clientId;
    String referralId;
    String sensitivity;

    MinClientReferral(String clientId, String referralId, String sensitivity) {
      this.clientId = clientId;
      this.referralId = referralId;
      this.sensitivity = sensitivity;
    }

    protected static MinClientReferral extract(ResultSet rs) throws SQLException {
      return new MinClientReferral(rs.getString("FKCLIENT_T"), rs.getString("FKREFERL_T"),
          rs.getString("SENSTV_IND"));
    }

    String getClientId() {
      return clientId;
    }

    void setClientId(String clientId) {
      this.clientId = clientId;
    }

    String getReferralId() {
      return referralId;
    }

    void setReferralId(String referralId) {
      this.referralId = referralId;
    }

    String getSensitivity() {
      return sensitivity;
    }

    void setSensitivity(String sensitivity) {
      this.sensitivity = sensitivity;
    }
  }

  private final ThreadLocal<List<EsPersonReferral>> allocAllegations = new ThreadLocal<>();

  private final ThreadLocal<Map<String, EsPersonReferral>> allocReferrals = new ThreadLocal<>();

  private final ThreadLocal<List<MinClientReferral>> allocClientReferralKeys = new ThreadLocal<>();

  private AtomicInteger rowsReadReferrals = new AtomicInteger(0);

  private AtomicInteger rowsReadAllegations = new AtomicInteger(0);

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ReferralHistoryIndexerJob(ReplicatedPersonReferralsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsPersonReferral.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_REFRL_ONLY";
  }

  @Override
  public String getJdbcOrderBy() {
    return " "; // sort manually cuz DB2 might not optimize the sort.
  }

  @Override
  protected String getLegacySourceTable() {
    return "REFERL_T";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT vw.* FROM ");
    buf.append(dbSchemaName);
    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" vw ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE vw.LIMITED_ACCESS_CODE = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString().replaceAll("\\s+", " ");
  }

  /**
   * Read recs from a single partition. Must sort results because the database won't do it for us.
   * 
   * <p>
   * Each call of this method may run in its own thread.
   * </p>
   * 
   * @param p partition range to read
   */
  protected void pullRange(final Pair<String, String> p) {
    final int i = nextThreadNum.incrementAndGet();
    final String threadName = "extract_" + i + "_" + p.getLeft() + "_" + p.getRight();
    Thread.currentThread().setName(threadName);
    LOGGER.info("BEGIN");

    try (Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
        .getService(ConnectionProvider.class).getConnection()) {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      con.setReadOnly(true);
      enableParallelism(con);

      // ((DB2Connection)con).
      // ((com.ibm.db2.jcc.DB2BaseDataSource) ds).setDeferPrepares ((true));
      // ((com.ibm.db2.jcc.DB2BaseDataSource) ds).sendDataAsIs = true;

      // DB2SystemMonitor monitor = ((DB2Connection) con).getDB2SystemMonitor();
      // monitor.enable(true);
      // monitor.start(DB2SystemMonitor.RESET_TIMES);
      int cntr = 0;

      // Allocate memory once for this thread and reuse it per key range.
      if (allocAllegations.get() == null) {
        allocAllegations.set(new ArrayList<>(150000));
        allocReferrals.set(new HashMap<>(99881));
        allocClientReferralKeys.set(new ArrayList<>(12000));
      }

      final List<EsPersonReferral> listAllegations = allocAllegations.get();
      final Map<String, EsPersonReferral> mapReferrals = allocReferrals.get();
      final List<MinClientReferral> listClientReferralKeys = allocClientReferralKeys.get();

      listAllegations.clear();
      mapReferrals.clear();
      listClientReferralKeys.clear();

      final String schema = getDBSchemaName();

      try (
          final PreparedStatement stmtInsClient =
              con.prepareStatement(INSERT_CLIENT.replaceAll("#SCHEMA#", schema));
          final PreparedStatement stmtSelClient =
              con.prepareStatement(SELECT_CLIENT.replaceAll("#SCHEMA#", schema));
          final PreparedStatement stmtSelReferral =
              con.prepareStatement(getInitialLoadQuery(schema));
          final PreparedStatement stmtSelAllegation =
              con.prepareStatement(SELECT_ALLEGATION.replaceAll("#SCHEMA#", schema))) {

        // Prepare client list.
        stmtInsClient.setMaxRows(0);
        stmtInsClient.setQueryTimeout(0);
        stmtInsClient.setString(1, p.getLeft());
        stmtInsClient.setString(2, p.getRight());

        final int cntInsClientReferral = stmtInsClient.executeUpdate();
        LOGGER.info("bundle client/referrals: {}", cntInsClientReferral);

        // Prepare retrieval.
        stmtSelClient.setMaxRows(0);
        stmtSelClient.setQueryTimeout(0);
        stmtSelClient.setFetchSize(FETCH_SIZE);

        stmtSelReferral.setMaxRows(0);
        stmtSelReferral.setQueryTimeout(0);
        stmtSelReferral.setFetchSize(FETCH_SIZE);

        stmtSelAllegation.setMaxRows(0);
        stmtSelAllegation.setQueryTimeout(0);
        stmtSelAllegation.setFetchSize(FETCH_SIZE);

        {
          LOGGER.info("pull client/referral keys");
          final ResultSet rs = stmtSelClient.executeQuery(); // NOSONAR
          MinClientReferral mx;
          while (!fatalError && rs.next() && (mx = MinClientReferral.extract(rs)) != null) {
            listClientReferralKeys.add(mx);
          }
        }

        {
          cntr = 0;
          EsPersonReferral m;
          LOGGER.info("pull referrals");
          final ResultSet rs = stmtSelReferral.executeQuery(); // NOSONAR
          while (!fatalError && rs.next() && (m = extractReferral(rs)) != null) {
            JobLogUtils.logEvery(++cntr, "read", "bundle referral");
            JobLogUtils.logEvery(LOGGER, 10000, rowsReadReferrals.incrementAndGet(), "Total read",
                "referrals");
            mapReferrals.put(m.getReferralId(), m);
          }
        }

        {
          cntr = 0;
          EsPersonReferral m;
          LOGGER.info("pull allegations");
          final ResultSet rs = stmtSelAllegation.executeQuery(); // NOSONAR
          while (!fatalError && rs.next() && (m = extractAllegation(rs)) != null) {
            JobLogUtils.logEvery(++cntr, "read", "bundle allegation");
            JobLogUtils.logEvery(LOGGER, 15000, rowsReadAllegations.incrementAndGet(), "Total read",
                "allegations");
            listAllegations.add(m);
          }
        }

        // monitor.stop();
        // LOGGER.info("Server elapsed time (microseconds)=" + monitor.getServerTimeMicros());
        // LOGGER.info("Network I/O elapsed time (microseconds)=" +
        // monitor.getNetworkIOTimeMicros());
        // LOGGER.info("Core driver elapsed time (microseconds)=" +
        // monitor.getCoreDriverTimeMicros());
        // LOGGER
        // .info("Application elapsed time (milliseconds)=" + monitor.getApplicationTimeMillis());

        // C'mon IBM. Where are the constants for method DB2SystemMonitor.moreData()??
        // Not supported on z/OS?
        // LOGGER.info("NUMBER of the NETWORK_TRIPS =" +
        // systemMonitor.moreData(NUMBER_NETWORK_TRIPS));

        con.commit();
      } finally {
        // Connection and statements close automatically.
      }

      final Map<String, List<MinClientReferral>> mapReferralByClient = listClientReferralKeys
          .stream().sorted((e1, e2) -> e1.getClientId().compareTo(e2.getClientId()))
          .collect(Collectors.groupingBy(MinClientReferral::getClientId));

      final Map<String, List<EsPersonReferral>> mapAllegationByReferral = listAllegations.stream()
          .sorted((e1, e2) -> e1.getReferralId().compareTo(e2.getReferralId()))
          .collect(Collectors.groupingBy(EsPersonReferral::getReferralId));

      final List<EsPersonReferral> readyToNorm = new ArrayList<>();

      // TODO: convert to stream instead of nested for loops.
      // For each client:
      for (Map.Entry<String, List<MinClientReferral>> rc : mapReferralByClient.entrySet()) {

        // Loop referrals for this client:
        final String clientId = rc.getKey();
        if (StringUtils.isNotBlank(clientId)) {
          readyToNorm.clear();
          for (MinClientReferral rc1 : rc.getValue()) {
            final String referralId = rc1.referralId;
            final EsPersonReferral ref = mapReferrals.get(referralId);

            // Sealed and sensitive may be excluded.
            if (ref != null) {
              // Loop allegations for this referral:
              if (mapAllegationByReferral.containsKey(referralId)) {
                for (EsPersonReferral alg : mapAllegationByReferral.get(referralId)) {
                  alg.mergeClientReferralInfo(clientId, ref);
                  readyToNorm.add(alg);
                }
              } else {
                readyToNorm.add(ref);
              }
            } else {
              LOGGER.trace("sensitive referral? ref id={}, client id={}", referralId, clientId);
            }
          }

          final ReplicatedPersonReferrals repl = normalizeSingle(readyToNorm);
          if (repl != null) {
            repl.setClientId(clientId);
            addToIndexQueue(repl);
          } else {
            LOGGER.trace("null normalized? sensitive? client id={}", clientId);
          }
        } else {
          LOGGER.trace("empty client? client id={}", clientId);
        }
      }

      // unsorted.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential()
      // listReferrals.stream().sorted().collect(Collectors.groupingBy(EsPersonReferral::getClientId))
      // .entrySet().stream().map(e -> normalizeSingle(e.getValue()))
      // .forEach(this::addToIndexQueue);

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    // Good time to *request* garbage collection. GC runs in another thread anyway.
    // SonarQube disagrees.
    // System.gc();
    LOGGER.info("DONE");
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("read_main");
    LOGGER.info("BEGIN: main read thread");

    try {
      // This job normalizes **without** the transform thread.
      doneTransform = true;
      final List<ForkJoinTask<?>> tasks = new ArrayList<>();

      // Set thread pool size.
      final int cntReaderThreads =
          getOpts().getThreadCount() != 0L ? (int) getOpts().getThreadCount()
              : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
      LOGGER.warn(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", cntReaderThreads);
      ForkJoinPool forkJoinPool = new ForkJoinPool(cntReaderThreads);

      final List<Pair<String, String>> ranges = getPartitionRanges();
      LOGGER.warn(">>>>>>>> # OF RANGES: {} <<<<<<<<", ranges);

      // Queue execution.
      for (Pair<String, String> p : ranges) {
        tasks.add(forkJoinPool.submit(() -> pullRange(p)));
      }

      // Join threads. Don't return from method until they complete.
      for (ForkJoinTask<?> task : tasks) {
        task.join();
      }

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      LOGGER.info("read {} ES referral rows", this.rowsReadReferrals.get());
      doneExtract = true;
    }

    LOGGER.info("DONE: main read thread");
  }

  @Override
  protected boolean useTransformThread() {
    return false;
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected List<ReplicatedPersonReferrals> normalize(List<EsPersonReferral> recs) {
    return EntityNormalizer.<ReplicatedPersonReferrals, EsPersonReferral>normalizeList(recs);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return new ReferralJobRanges().getPartitionRanges(this);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp,
      ReplicatedPersonReferrals referrals) throws IOException {
    StringBuilder buf = new StringBuilder();
    buf.append("{\"referrals\":[");

    List<ElasticSearchPersonReferral> esPersonReferrals = referrals.getReferrals();
    esp.setReferrals(esPersonReferrals);

    if (esPersonReferrals != null && !esPersonReferrals.isEmpty()) {
      try {
        buf.append(esPersonReferrals.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.error("ERROR SERIALIZING REFERRALS", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String updateJson = buf.toString();
    final String insertJson = mapper.writeValueAsString(esp);
    LOGGER.trace("insertJson: {}", insertJson);
    LOGGER.trace("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  protected EsPersonReferral extractReferral(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    // IBM strongly recommends retrieving column results by position, not by column name.
    int columnIndex = 0;
    // REFERRAL_ID,
    // START_DATE,
    // END_DATE,
    // REFERRAL_RESPONSE_TYPE,
    // LIMITED_ACCESS_CODE,
    // LIMITED_ACCESS_DATE,
    // LIMITED_ACCESS_DESCRIPTION,
    // LIMITED_ACCESS_GOVERNMENT_ENT,
    // REFERRAL_LAST_UPDATED,
    // REPORTER_ID,
    // REPORTER_FIRST_NM,
    // REPORTER_LAST_NM,
    // REPORTER_LAST_UPDATED,
    // WORKER_ID,
    // WORKER_FIRST_NM,
    // WORKER_LAST_NM,
    // WORKER_LAST_UPDATED,
    // REFERRAL_COUNTY,
    // LAST_CHG

    ret.setReferralId(rs.getString(++columnIndex));
    ret.setStartDate(rs.getDate(++columnIndex));
    ret.setEndDate(rs.getDate(++columnIndex));
    ret.setReferralResponseType(rs.getInt(++columnIndex));

    ret.setLimitedAccessCode(ifNull(rs.getString(++columnIndex)));
    ret.setLimitedAccessDate(rs.getDate(++columnIndex));
    ret.setLimitedAccessDescription(ifNull(rs.getString(++columnIndex)));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt(++columnIndex));
    ret.setReferralLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setReporterId(ifNull(rs.getString(++columnIndex)));
    ret.setReporterFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setReporterLastName(ifNull(rs.getString(++columnIndex)));
    ret.setReporterLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setWorkerId(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerLastName(ifNull(rs.getString(++columnIndex)));
    ret.setWorkerLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setCounty(rs.getInt(++columnIndex));
    ret.setLastChange(rs.getDate(++columnIndex));

    return ret;
  }

  protected EsPersonReferral extractAllegation(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    // IBM strongly recommends retrieving column results by position, not by column name.
    int columnIndex = 0;
    // REFERRAL_ID,
    // ALLEGATION_ID,
    // ALLEGATION_DISPOSITION,
    // ALLEGATION_TYPE,
    // ALLEGATION_LAST_UPDATED,
    // PERPETRATOR_ID,
    // PERPETRATOR_SENSITIVITY_IND,
    // PERPETRATOR_FIRST_NM,
    // PERPETRATOR_LAST_NM,
    // PERPETRATOR_LAST_UPDATED,
    // VICTIM_ID,
    // VICTIM_SENSITIVITY_IND,
    // VICTIM_FIRST_NM,
    // VICTIM_LAST_NM,
    // VICTIM_LAST_UPDATED,
    // LAST_CHG

    ret.setReferralId(ifNull(rs.getString(++columnIndex)));
    ret.setAllegationId(ifNull(rs.getString(++columnIndex)));
    ret.setAllegationDisposition(rs.getInt(++columnIndex));
    ret.setAllegationType(rs.getInt(++columnIndex));
    ret.setAllegationLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setPerpetratorId(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorSensitivityIndicator(rs.getString(++columnIndex));
    ret.setPerpetratorFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorLastName(ifNull(rs.getString(++columnIndex)));
    ret.setPerpetratorLastUpdated(rs.getTimestamp(++columnIndex));

    ret.setVictimId(ifNull(rs.getString(++columnIndex)));
    ret.setVictimSensitivityIndicator(rs.getString(++columnIndex));
    ret.setVictimFirstName(ifNull(rs.getString(++columnIndex)));
    ret.setVictimLastName(ifNull(rs.getString(++columnIndex)));
    ret.setVictimLastUpdated(rs.getTimestamp(++columnIndex));

    return ret;
  }

  @Override
  public EsPersonReferral extract(ResultSet rs) throws SQLException {
    EsPersonReferral ret = new EsPersonReferral();

    ret.setReferralId(ifNull(rs.getString("REFERRAL_ID")));
    ret.setStartDate(rs.getDate("START_DATE"));
    ret.setEndDate(rs.getDate("END_DATE"));
    ret.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    ret.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    ret.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    ret.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    ret.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));
    ret.setReferralLastUpdated(rs.getTimestamp("REFERRAL_LAST_UPDATED"));

    ret.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    ret.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    ret.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    ret.setAllegationLastUpdated(rs.getTimestamp("ALLEGATION_LAST_UPDATED"));

    ret.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    ret.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    ret.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    ret.setPerpetratorLastUpdated(rs.getTimestamp("PERPETRATOR_LAST_UPDATED"));
    ret.setPerpetratorSensitivityIndicator(rs.getString("PERPETRATOR_SENSITIVITY_IND"));

    ret.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    ret.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    ret.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    ret.setReporterLastUpdated(rs.getTimestamp("REPORTER_LAST_UPDATED"));

    ret.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    ret.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    ret.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    ret.setVictimLastUpdated(rs.getTimestamp("VICTIM_LAST_UPDATED"));
    ret.setVictimSensitivityIndicator(rs.getString("VICTIM_SENSITIVITY_IND"));

    ret.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    ret.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    ret.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    ret.setWorkerLastUpdated(rs.getTimestamp("WORKER_LAST_UPDATED"));

    ret.setCounty(rs.getInt("REFERRAL_COUNTY"));
    ret.setLastChange(rs.getDate("LAST_CHG"));

    return ret;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReferralHistoryIndexerJob.class, args);
  }

}
