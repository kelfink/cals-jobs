package gov.ca.cwds.jobs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientAddressDao;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedClientRelationshipDao;
import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.component.AtomValidateDocument;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.schedule.NeutronRocket;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class NeutronElasticValidator implements AtomValidateDocument {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronRocket.class);

  private final ElasticsearchDao esDao;

  private final ReplicatedClientDao repClientDao;
  private final ReplicatedClientAddressDao repClientAddressDao;

  private final ReplicatedReporterDao repReporterDao;
  private final ReplicatedCollateralIndividualDao repCollateralIndividualDao;

  private final ReplicatedOtherClientNameDao repOtherClientNameDao;
  private final ReplicatedClientRelationshipDao repClientRelationshipDao;

  @Inject
  public NeutronElasticValidator(final ElasticsearchDao esDao,
      final ReplicatedClientDao repClientDao, final ReplicatedClientAddressDao repClientAddressDao,
      final ReplicatedCollateralIndividualDao repCollateralIndividualDao,
      final ReplicatedReporterDao repReporterDao,
      final ReplicatedOtherClientNameDao repOtherClientNameDao,
      final ReplicatedClientRelationshipDao repClientRelationshipDao) {
    this.esDao = esDao;
    this.repClientDao = repClientDao;
    this.repClientAddressDao = repClientAddressDao;
    this.repCollateralIndividualDao = repCollateralIndividualDao;
    this.repReporterDao = repReporterDao;
    this.repOtherClientNameDao = repOtherClientNameDao;
    this.repClientRelationshipDao = repClientRelationshipDao;
  }

  public String fetchESPerson(String docId) {
    final ElasticsearchConfiguration config = esDao.getConfig();
    return esDao.searchIndexByQuery(config.getElasticsearchAlias(), "",
        config.getElasticsearchHost(), Integer.parseInt(config.getElasticsearchPort()),
        config.getElasticsearchDocType());
  }

  @Override
  public ElasticsearchDao getEsDao() {
    return esDao;
  }

  public ReplicatedClientDao getRepClientDao() {
    return repClientDao;
  }

  public ReplicatedClientAddressDao getRepClientAddressDao() {
    return repClientAddressDao;
  }

  public ReplicatedReporterDao getRepReporterDao() {
    return repReporterDao;
  }

  public ReplicatedCollateralIndividualDao getRepCollateralIndividualDao() {
    return repCollateralIndividualDao;
  }

  public ReplicatedOtherClientNameDao getRepOtherClientNameDao() {
    return repOtherClientNameDao;
  }

  public ReplicatedClientRelationshipDao getRepClientRelationshipDao() {
    return repClientRelationshipDao;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }

  @Override
  public FlightRecord getTrack() {
    // TODO Auto-generated method stub
    return null;
  }

}
