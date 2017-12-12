package gov.ca.cwds.neutron.rocket;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.neutron.enums.NeutronElasticsearchDefaults;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;

/**
 * Drop and creates ES indexes, if requested.
 * 
 * @author CWDS API Team
 */
public class IndexResetRocket
    extends BasePersonRocket<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(IndexResetRocket.class);

  /**
   * Construct rocket with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param mapper Jackson ObjectMapper
   * @param flightPlan command line options
   */
  @Inject
  public IndexResetRocket(final ReplicatedOtherAdultInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, final ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, flightPlan.getLastRunLoc(), mapper, flightPlan);
  }

  @Override
  public Date launch(Date lastRunDate) {
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
      final String documentType = esDao.getConfig().getElasticsearchDocType();

      final String settingFile = StringUtils.isNotBlank(esDao.getConfig().getIndexSettingFile())
          ? esDao.getConfig().getIndexSettingFile()
          : NeutronElasticsearchDefaults.ES_PEOPLE_INDEX_SETTINGS.getValue();

      final String mappingFile = StringUtils.isNotBlank(esDao.getConfig().getDocumentMappingFile())
          ? esDao.getConfig().getDocumentMappingFile()
          : NeutronElasticsearchDefaults.ES_PERSON_MAPPING.getValue();

      LOGGER.debug(
          "Create index if missing, effectiveIndexName: {}, settingFile: {}, mappingFile: {}",
          effectiveIndexName, settingFile, mappingFile);

      esDao.createIndexIfNeeded(effectiveIndexName, documentType, settingFile, mappingFile);
    } catch (Exception e) {
      JobLogs.checked(LOGGER, e, "ES INDEX MANAGEMENT ERROR! {}", e.getMessage());
    }

    return lastRunDate;
  }

}
