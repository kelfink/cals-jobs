package gov.ca.cwds.neutron.rocket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.ParameterMode;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.procedure.ProcedureCall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.atom.AtomValidateDocument;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientCountyRocket extends InitialLoadJdbcRocket<ReplicatedClient, EsClientAddress>
    implements NeutronRowMapper<EsClientAddress>, AtomValidateDocument {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(ClientCountyRocket.class);

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line options
   */
  @Inject
  public ClientCountyRocket(final ReplicatedClientDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory,
      FlightPlan opts) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, opts);
  }

  @Override
  public boolean useTransformThread() {
    return false;
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
  public String getMQTName() {
    return getInitialLoadViewName();
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.clt_identifier ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();

    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.clt_identifier BETWEEN ':fromId' AND ':toId' ");

    if (!getFlightPlan().isLoadSealedAndSensitive()) {
      buf.append(" AND x.CLT_SENSTV_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Example call: {@code CALL CWSRSQ.GENCLNCNTY ('O', '', '', ?);}
   */
  protected void callProc() {
    if (LaunchCommand.isInitialMode()) {
      LOGGER.info("Call stored proc");
      final Session session = getJobDao().getSessionFactory().getCurrentSession();
      getOrCreateTransaction();
      final String schema =
          (String) session.getSessionFactory().getProperties().get("hibernate.default_schema");

      try {
        final ProcedureCall proc = session.createStoredProcedureCall(schema + ".PRCCLNCNTY");
        proc.registerStoredProcedureParameter("MQTNAME", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("RETSTATUS", String.class, ParameterMode.OUT);
        proc.registerStoredProcedureParameter("RETMESSAG", String.class, ParameterMode.OUT);

        proc.setParameter("MQTNAME", getMQTName());
        proc.execute();

        final String returnStatus = (String) proc.getOutputParameterValue("RETSTATUS");
        final String returnMsg = (String) proc.getOutputParameterValue("RETMESSAG");

        LOGGER.info("refresh MQT proc: status: {}, msg: {}", returnStatus, returnMsg);

        if (returnStatus.charAt(0) != '0') {
          LOGGER.error("REFRESH MQT ERROR! {}", returnMsg);
          throw new DaoException("REFRESH MQT ERROR: " + returnMsg);
        }

      } catch (DaoException h) {
        throw h;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void threadRetrieveByJdbc() {
    bigRetrieveByJdbc();
  }

  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  public List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtils.getCommonPartitionRanges64(this);
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getFlightPlan().isLoadSealedAndSensitive();
  }

  @Override
  public List<ReplicatedClient> normalize(List<EsClientAddress> recs) {
    return EntityNormalizer.<ReplicatedClient, EsClientAddress>normalizeList(recs);
  }

  @Override
  public int nextThreadNumber() {
    return nextThreadNum.incrementAndGet();
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(ClientCountyRocket.class, args);
  }

}
