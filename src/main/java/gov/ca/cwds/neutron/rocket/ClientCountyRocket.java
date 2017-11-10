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
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtil;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.atom.AtomValidateDocument;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientCountyRocket extends InitialLoadJdbcRocket<ReplicatedClient, EsClientAddress>
    implements NeutronRowMapper<EsClientAddress>, AtomValidateDocument {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(ClientCountyRocket.class);

  private static final String INSERT_CLIENT_INITIAL_LOAD =
      "INSERT INTO GT_ID (IDENTIFIER)\n" + "SELECT CLT.IDENTIFIER \nFROM CLIENT_T x\n"
          + "WHERE x.IDENTIFIER BETWEEN ':fromId' AND ':toId' ";

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param flightPlan command line options
   */
  @Inject
  public ClientCountyRocket(final ReplicatedClientDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastRunFile, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, sessionFactory, flightPlan);
  }

  @Override
  public EsClientAddress extract(ResultSet rs) throws SQLException {
    return EsClientAddress.extract(rs);
  }

  /**
   * NEXT: turn into a fixed rocket setting, not override method.
   */
  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsClientAddress.class;
  }

  /**
   * NEXT: turn into a fixed rocket setting, not override method.
   */
  @Override
  public String getInitialLoadViewName() {
    return "CLIENT_T";
  }

  /**
   * NEXT: turn into a fixed rocket setting, not override method.
   */
  @Override
  public String getMQTName() {
    return getInitialLoadViewName();
  }

  /**
   * NEXT: turn into a fixed rocket setting, not override method.
   */
  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.IDENTIFIER ";
  }

  /**
   * NEXT: Turn this method into a rocket setting.
   */
  @Override
  public boolean useTransformThread() {
    return false;
  }

  /**
   * NEXT: Turn this method into a rocket setting.
   */
  @Override
  public boolean isInitialLoadJdbc() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handleRangeResults(final ResultSet rs) throws SQLException {
    // Call the stored procedure once for all client id's inserted into the global temporary table.
    callProc();
  }

  /**
   * Proc call for initial load: {@code CALL CWSRSQ.GENCLNCNTY ('O', '', '', ?);}
   * 
   * <p>
   * Incremental changes are called by table trigger, not by batch.
   * </p>
   */
  protected void callProc() {
    if (LaunchCommand.isInitialMode()) { // initial mode only
      LOGGER.info("Call stored proc");
      final Session session = getJobDao().getSessionFactory().getCurrentSession();
      getOrCreateTransaction(); // HACK. move to base DAO.
      final String schema =
          (String) session.getSessionFactory().getProperties().get("hibernate.default_schema");

      try {
        final ProcedureCall proc = session.createStoredProcedureCall(schema + ".PRCCLNCNTY");
        proc.registerStoredProcedureParameter("PARM_CRUD", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("PARM_ID", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("PARM_TRIGTBL", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("RETCODE", Integer.class, ParameterMode.OUT);

        proc.setParameter("PARM_CRUD", "0");
        proc.setParameter("PARM_ID", "");
        proc.setParameter("PARM_TRIGTBL", "");
        proc.execute();

        final int retcode = ((Integer) proc.getOutputParameterValue("RETCODE")).intValue();
        LOGGER.info("client count proc: retcode: {}", retcode);

        if (retcode != 0) {
          LOGGER.error("FAILED TO CALL PROC! retcode: {}", retcode);
          throw new DaoException("FAILED TO CALL PROC! retcode: " + retcode);
        }
      } catch (DaoException h) {
        throw h; // just re-throw
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
  public List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtil.getCommonPartitionRanges64(this);
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
