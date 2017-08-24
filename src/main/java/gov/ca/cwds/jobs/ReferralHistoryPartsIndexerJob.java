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
public class ReferralHistoryPartsIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral>
    implements JobResultSetAware<EsPersonReferral> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ReferralHistoryPartsIndexerJob.class);

  private static final String INSERT_CLIENT =
      "INSERT INTO #SCHEMA#.GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "SELECT rc.FKREFERL_T, rc.FKCLIENT_T, rc.SENSTV_IND\n"
          + "FROM #SCHEMA#.VW_REFR_CLT rc\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ?";

  private static final String SELECT_CLIENT =
      "SELECT FKCLIENT_T, FKREFERL_T, SENSTV_IND FROM #SCHEMA#.GT_REFR_CLT";

  private static final String SELECT_REFERRAL =
      "SELECT vw.* FROM #SCHEMA#.VW_MQT_REFRL_ONLY vw FOR READ ONLY WITH UR";

  private static final String SELECT_ALLEGATION =
      "SELECT vw.* FROM #SCHEMA#.VW_MQT_ALGTN_ONLY vw FOR READ ONLY WITH UR";

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

  /**
   * Allocate memory once and reuse for multiple key ranges. Avoid repeated, expensive memory
   * allocation of large containers.
   */
  private final ThreadLocal<Map<String, EsPersonReferral>> allocReferrals = new ThreadLocal<>();

  private final ThreadLocal<Map<String, EsPersonReferral>> allocAllegations = new ThreadLocal<>();

  private AtomicInteger rowsRead = new AtomicInteger(0);

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
  public ReferralHistoryPartsIndexerJob(ReplicatedPersonReferralsDao clientDao,
      ElasticsearchDao esDao, @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
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
      enableParallelism(con);

      int cntr = 0;
      EsPersonReferral m;

      // Allocate memory once for this thread and reuse it per key range.
      if (allocReferrals.get() == null) {
        allocReferrals.set(new HashMap<>(50000));
        allocAllegations.set(new HashMap<>(125000));
      }

      final Map<String, EsPersonReferral> mapReferrals = allocReferrals.get();
      final Map<String, EsPersonReferral> mapAllegations = allocAllegations.get();
      mapReferrals.clear();
      mapAllegations.clear();

      final List<MinClientReferral> listClientReferral = new ArrayList<>(30000);

      try (
          final PreparedStatement stmtInsClient =
              con.prepareStatement(INSERT_CLIENT.replaceAll("#SCHEMA#", getDBSchemaName()));
          final PreparedStatement stmtSelClient =
              con.prepareStatement(SELECT_CLIENT.replaceAll("#SCHEMA#", getDBSchemaName()));
          final PreparedStatement stmtSelReferral =
              con.prepareStatement(getInitialLoadQuery(getDBSchemaName()));
          final PreparedStatement stmtSelAllegation =
              con.prepareStatement(SELECT_ALLEGATION.replaceAll("#SCHEMA#", getDBSchemaName()))) {

        // Prepare client list.
        stmtInsClient.setMaxRows(0);
        stmtInsClient.setQueryTimeout(0);
        stmtInsClient.setString(1, p.getLeft());
        stmtInsClient.setString(2, p.getRight());

        final int cntInsClientReferral = stmtInsClient.executeUpdate();
        LOGGER.debug("bundle client/referrals: {}", cntInsClientReferral);

        // Prepare retrieval.
        stmtSelClient.setMaxRows(0);
        stmtSelClient.setQueryTimeout(0);
        stmtSelClient.setFetchSize(5000);

        stmtSelReferral.setMaxRows(0);
        stmtSelReferral.setQueryTimeout(0);
        stmtSelReferral.setFetchSize(5000);

        stmtSelAllegation.setMaxRows(0);
        stmtSelAllegation.setQueryTimeout(0);
        stmtSelAllegation.setFetchSize(5000);

        {
          final ResultSet rs = stmtSelClient.executeQuery(); // NOSONAR
          MinClientReferral mx;
          while (!fatalError && rs.next() && (mx = MinClientReferral.extract(rs)) != null) {
            listClientReferral.add(mx);
          }
        }

        final Map<String, List<MinClientReferral>> mapReferralByClient =
            listClientReferral.stream().sorted((e1, e2) -> e1.clientId.compareTo(e2.clientId))
                .collect(Collectors.groupingBy(MinClientReferral::getClientId));

        final Map<String, List<MinClientReferral>> mapClientByReferral =
            listClientReferral.stream().sorted((e1, e2) -> e1.referralId.compareTo(e2.referralId))
                .collect(Collectors.groupingBy(MinClientReferral::getReferralId));

        {
          final ResultSet rs = stmtSelReferral.executeQuery(); // NOSONAR
          while (!fatalError && rs.next() && (m = extractReferral(rs)) != null) {
            JobLogUtils.logEvery(++cntr, "read", "bundle referral");
            JobLogUtils.logEvery(LOGGER, 50000, rowsRead.incrementAndGet(), "Total read",
                "referrals");
            mapReferrals.put(m.getReferralId(), m);
          }
        }

        {
          final ResultSet rs = stmtSelAllegation.executeQuery(); // NOSONAR
          while (!fatalError && rs.next() && (m = extractAllegation(rs)) != null) {
            JobLogUtils.logEvery(++cntr, "read", "bundle allegation");
            JobLogUtils.logEvery(LOGGER, 50000, rowsRead.incrementAndGet(), "Total read",
                "allegations");
            mapAllegations.put(m.getReferralId(), m);
          }
        }

        con.commit();
      } finally {
        // Statement and connection close automatically.
      }

      LOGGER.warn("sort, group, normalize, and send to index queue");
      // unsorted.stream().sorted((e1, e2) -> e1.compare(e1, e2)).sequential()

      // listReferrals.stream().sorted().collect(Collectors.groupingBy(EsPersonReferral::getClientId))
      // .entrySet().stream().map(e -> normalizeSingle(e.getValue()))
      // .forEach(this::addToIndexQueue);

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.warn("DONE");
  }

  /**
   * The "extract" part of ETL. Parallel stream produces runs partition ranges in separate threads.
   */
  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract_main");
    LOGGER.info("BEGIN: main extract thread");

    try {
      // This job normalizes **without** the transform thread.
      doneTransform = true;
      final List<ForkJoinTask<?>> tasks = new ArrayList<>();

      // Set thread pool size.
      final int cntReaderThreads =
          getOpts().getThreadCount() != 0L ? (int) getOpts().getThreadCount()
              : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
      LOGGER.warn(">>>>>>>> EXTRACT THREADS: {} <<<<<<<<", cntReaderThreads);
      ForkJoinPool forkJoinPool = new ForkJoinPool(cntReaderThreads);

      // Queue execution.
      for (Pair<String, String> p : getPartitionRanges()) {
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
      LOGGER.info("extracted {} ES referral rows", this.rowsRead.get());
      doneExtract = true;
    }

    LOGGER.info("DONE: main extract thread");
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
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(ifNull(rs.getString("REFERRAL_ID")));

    referral.setStartDate(rs.getDate("START_DATE"));
    referral.setEndDate(rs.getDate("END_DATE"));
    referral.setLastChange(rs.getDate("LAST_CHG"));
    referral.setCounty(rs.getInt("REFERRAL_COUNTY"));
    referral.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    referral.setReferralLastUpdated(rs.getTimestamp("REFERRAL_LAST_UPDATED"));

    referral.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    referral.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    referral.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    referral.setReporterLastUpdated(rs.getTimestamp("REPORTER_LAST_UPDATED"));

    referral.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    referral.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    referral.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    referral.setWorkerLastUpdated(rs.getTimestamp("WORKER_LAST_UPDATED"));

    referral.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    referral.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    referral.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    referral.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return referral;
  }

  protected EsPersonReferral extractAllegation(ResultSet rs) throws SQLException {
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(ifNull(rs.getString("REFERRAL_ID")));

    referral.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    referral.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    referral.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    referral.setAllegationLastUpdated(rs.getTimestamp("ALLEGATION_LAST_UPDATED"));

    referral.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    referral.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    referral.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    referral.setPerpetratorLastUpdated(rs.getTimestamp("PERPETRATOR_LAST_UPDATED"));
    referral.setPerpetratorSensitivityIndicator(rs.getString("PERPETRATOR_SENSITIVITY_IND"));

    referral.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    referral.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    referral.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    referral.setVictimLastUpdated(rs.getTimestamp("VICTIM_LAST_UPDATED"));
    referral.setVictimSensitivityIndicator(rs.getString("VICTIM_SENSITIVITY_IND"));

    return referral;
  }

  @Override
  public EsPersonReferral extract(ResultSet rs) throws SQLException {
    EsPersonReferral referral = new EsPersonReferral();

    referral.setReferralId(ifNull(rs.getString("REFERRAL_ID")));
    referral.setClientId(ifNull(rs.getString("CLIENT_ID")));

    referral.setStartDate(rs.getDate("START_DATE"));
    referral.setEndDate(rs.getDate("END_DATE"));
    referral.setLastChange(rs.getDate("LAST_CHG"));
    referral.setCounty(rs.getInt("REFERRAL_COUNTY"));
    referral.setReferralResponseType(rs.getInt("REFERRAL_RESPONSE_TYPE"));
    referral.setReferralLastUpdated(rs.getTimestamp("REFERRAL_LAST_UPDATED"));

    referral.setAllegationId(ifNull(rs.getString("ALLEGATION_ID")));
    referral.setAllegationType(rs.getInt("ALLEGATION_TYPE"));
    referral.setAllegationDisposition(rs.getInt("ALLEGATION_DISPOSITION"));
    referral.setAllegationLastUpdated(rs.getTimestamp("ALLEGATION_LAST_UPDATED"));

    referral.setPerpetratorId(ifNull(rs.getString("PERPETRATOR_ID")));
    referral.setPerpetratorFirstName(ifNull(rs.getString("PERPETRATOR_FIRST_NM")));
    referral.setPerpetratorLastName(ifNull(rs.getString("PERPETRATOR_LAST_NM")));
    referral.setPerpetratorLastUpdated(rs.getTimestamp("PERPETRATOR_LAST_UPDATED"));
    referral.setPerpetratorSensitivityIndicator(rs.getString("PERPETRATOR_SENSITIVITY_IND"));

    referral.setReporterId(ifNull(rs.getString("REPORTER_ID")));
    referral.setReporterFirstName(ifNull(rs.getString("REPORTER_FIRST_NM")));
    referral.setReporterLastName(ifNull(rs.getString("REPORTER_LAST_NM")));
    referral.setReporterLastUpdated(rs.getTimestamp("REPORTER_LAST_UPDATED"));

    referral.setVictimId(ifNull(rs.getString("VICTIM_ID")));
    referral.setVictimFirstName(ifNull(rs.getString("VICTIM_FIRST_NM")));
    referral.setVictimLastName(ifNull(rs.getString("VICTIM_LAST_NM")));
    referral.setVictimLastUpdated(rs.getTimestamp("VICTIM_LAST_UPDATED"));
    referral.setVictimSensitivityIndicator(rs.getString("VICTIM_SENSITIVITY_IND"));

    referral.setWorkerId(ifNull(rs.getString("WORKER_ID")));
    referral.setWorkerFirstName(ifNull(rs.getString("WORKER_FIRST_NM")));
    referral.setWorkerLastName(ifNull(rs.getString("WORKER_LAST_NM")));
    referral.setWorkerLastUpdated(rs.getTimestamp("WORKER_LAST_UPDATED"));

    referral.setLimitedAccessCode(ifNull(rs.getString("LIMITED_ACCESS_CODE")));
    referral.setLimitedAccessDate(rs.getDate("LIMITED_ACCESS_DATE"));
    referral.setLimitedAccessDescription(ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION")));
    referral.setLimitedAccessGovernmentEntityId(rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT"));

    return referral;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReferralHistoryPartsIndexerJob.class, args);
  }

}
