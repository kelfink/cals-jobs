package gov.ca.cwds.neutron.rocket;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.enums.NeutronElasticsearchDefaults;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;

/**
 * Drop and creates ES indexes, if requested.
 * 
 * @author CWDS API Team
 */
public class InitialLoadIndexRocket
    extends BasePersonRocket<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(InitialLoadIndexRocket.class);

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param flightPlan command line options
   */
  @Inject
  public InitialLoadIndexRocket(final ReplicatedOtherAdultInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightPlan flightPlan) {
    super(dao, esDao, flightPlan.getLastRunLoc(), mapper, sessionFactory, flightPlan);
  }

  @Override
  public Date executeJob(Date lastSuccessfulRunTime) {
    LOGGER.info("INDEX CHECK!");

    try {
      // If index name is provided, use it, else take alias from ES config.
      final String indexNameOverride = getFlightPlan().getIndexName();
      final String effectiveIndexName = StringUtils.isBlank(indexNameOverride)
          ? esDao.getConfig().getElasticsearchAlias() : indexNameOverride;
      getFlightPlan().setIndexName(effectiveIndexName); // WARNING: probably a bad idea.

      // Drop index first, if requested.
      if (getFlightPlan().isDropIndex()) {
        esDao.deleteIndex(effectiveIndexName);
      }

      // If the index is missing, create it.
      LOGGER.debug("Create index if missing, effectiveIndexName: {}", effectiveIndexName);
      final String documentType = esDao.getConfig().getElasticsearchDocType();
      esDao.createIndexIfNeeded(effectiveIndexName, documentType,
          NeutronElasticsearchDefaults.ES_PEOPLE_INDEX_SETTINGS.getValue(),
          NeutronElasticsearchDefaults.ES_PERSON_MAPPING.getValue());
    } catch (Exception e) {
      JobLogs.checked(LOGGER, e, "ES INDEX MANAGEMENT ERROR! {}", e.getMessage());
    }

    return lastSuccessfulRunTime;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.launchOneWayTrip(InitialLoadIndexRocket.class, args);
  }

}
