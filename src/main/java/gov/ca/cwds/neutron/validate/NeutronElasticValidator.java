package gov.ca.cwds.neutron.validate;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;

import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientAddressDao;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedClientRelationshipDao;
import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.component.AtomValidateDocument;
import gov.ca.cwds.jobs.component.FlightLog;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.NeutronRocket;
import gov.ca.cwds.neutron.log.JetPackLogger;

public class NeutronElasticValidator implements AtomValidateDocument {

  private static final Logger LOGGER = new JetPackLogger(NeutronRocket.class);

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

  public List<ElasticSearchPerson> fetchPersonDocuments(String... docIds) throws NeutronException {
    List<ElasticSearchPerson> ret = new ArrayList<>();

    final Client esClient = this.esDao.getClient();
    final MultiSearchResponse sr = esClient.prepareMultiSearch()
        .add(esClient.prepareSearch().setQuery(QueryBuilders.idsQuery().addIds(docIds))).get();

    long totalHits = 0;
    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchHits hits = item.getResponse().getHits();
      totalHits += hits.getTotalHits();

      for (SearchHit hit : hits.getHits()) {
        final String json = hit.getSourceAsString();
        LOGGER.trace("json: {}", json);
        final ElasticSearchPerson person = readPerson(json);
        LOGGER.info("person: {}", person);
      }
    }

    LOGGER.info("total hits: {}", totalHits);
    return ret;
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
  public FlightLog getTrack() {
    return null;
  }

}
