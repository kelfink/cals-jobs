package gov.ca.cwds.neutron.rocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.ReferralHistoryIndexerJob;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;

/**
 * Rocket to load sealed and sensitive person referrals from CMS into ElasticSearch.
 * 
 * <p>
 * Turn-around time for database objects is too long. Embed SQL in Java instead.
 * </p>
 * 
 * @author CWDS API Team
 */
public class RestrictedReferralRocket extends ReferralHistoryIndexerJob
    implements NeutronRowMapper<EsPersonReferral> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RestrictedReferralRocket.class);

  protected static final String INSERT_CLIENT_RESTRICTED_ONLY =
      "INSERT INTO GT_REFR_CLT (FKREFERL_T, FKCLIENT_T, SENSTV_IND)\n"
          + "\nSELECT rc.FKREFERL_T, rc.FKCLIENT_T, c.SENSTV_IND\nFROM REFR_CLT rc\n"
          + "\nJOIN CLIENT_T c on c.IDENTIFIER = rc.FKCLIENT_T\n"
          + "\nWHERE rc.FKCLIENT_T > ? AND rc.FKCLIENT_T <= ? "
          + "AND rc.FKREFERL_T IN (SELECT RFL.IDENTIFIER FROM REFERL_T RFL WHERE RFL.LMT_ACSSCD IN ('S', 'R'))";

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastRunFile last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public RestrictedReferralRocket(ReplicatedPersonReferralsDao dao, ElasticsearchDao esDao,
      @LastRunFile String lastRunFile, ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
  }

  /**
   * Roll your own SQL. Turn-around on DB2 objects from other teams takes too long.
   */
  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append(SELECT_REFERRAL).append(" WHERE RFL.LMT_ACSSCD IN ('S', 'R') ")
        .append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    final String ret = buf.toString().replaceAll("\\s+", " ");
    LOGGER.info("RESTRICTED REFERRAL SQL: {}", ret);
    return ret;
  }

  @Override
  protected String getClientSeedQuery() {
    return INSERT_CLIENT_RESTRICTED_ONLY;
  }

  /**
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(RestrictedReferralRocket.class, args);
  }

}
