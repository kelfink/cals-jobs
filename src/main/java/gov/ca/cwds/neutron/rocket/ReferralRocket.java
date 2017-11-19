package gov.ca.cwds.neutron.rocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.ReferralHistoryIndexerJob;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.inject.annotation.LastRunFile;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.rocket.referral.MinClientReferral;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;

/**
 * Rocket indexes person referrals from CMS into ElasticSearch.
 * 
 * <p>
 * <strong>NEW APPROACH:</strong> re-index the *Client* document with referral elements.
 * </p>
 * 
 * @author CWDS API Team
 */
public class ReferralRocket extends ReferralHistoryIndexerJob
    implements NeutronRowMapper<EsPersonReferral> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(ReferralRocket.class);

  protected transient ThreadLocal<Map<String, ElasticSearchPerson>> allocPersonDocByClientId =
      new ThreadLocal<>();

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
  public ReferralRocket(ReplicatedPersonReferralsDao dao, ElasticsearchDao esDao,
      @LastRunFile String lastRunFile, ObjectMapper mapper, FlightPlan flightPlan) {
    super(dao, esDao, lastRunFile, mapper, flightPlan);
  }

  @Override
  protected void allocateThreadMemory() {
    super.allocateThreadMemory();
    if (allocPersonDocByClientId.get() == null) {
      allocPersonDocByClientId.set(new HashMap<>(10709)); // Prime
    }
  }

  @SuppressWarnings("unchecked")
  protected Map<String, ElasticSearchPerson> retrieveClientDocuments(
      final List<MinClientReferral> listClientReferralKeys) throws NeutronException {
    LOGGER.info("Retrieve client documents");
    long totalHits = 0;

    final Client client = this.esDao.getClient();
    final List<String> clientIds = listClientReferralKeys.stream()
        .map(MinClientReferral::getClientId).distinct().collect(Collectors.toList());
    final Map<String, ElasticSearchPerson> personDocByClientId = allocPersonDocByClientId.get();
    personDocByClientId.clear();

    final MultiSearchResponse sr = client.prepareMultiSearch().add(client.prepareSearch()
        .setQuery(QueryBuilders.idsQuery().addIds(clientIds.toArray(new String[0])))).get();

    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchResponse response = item.getResponse();
      final SearchHits hits = response.getHits();
      totalHits += hits.getTotalHits();

      try {
        for (SearchHit hit : hits.getHits()) {
          final ElasticSearchPerson person = readPerson(hit.getSourceAsString());
          LOGGER.trace("person: {}", () -> person);
          personDocByClientId.put(person.getId(), person);
        }
      } catch (NeutronException e) {
        LOGGER.error("ERROR READING DOCUMENTS!", e);
        throw e;
      }
    }

    LOGGER.info("total hits: {}", totalHits);
    return personDocByClientId;
  }

  /**
   * <strong>NEW APPROACH:</strong> re-index the *Client* document with referral elements.
   * 
   * @param bp ES bulk processor
   * @param norm normalized referrals
   */
  @Override
  protected void prepareDocument(final BulkProcessor bp, ReplicatedPersonReferrals t)
      throws IOException {
    Arrays.stream(ElasticTransformer.buildElasticSearchPersons(t))
        .map(p -> prepareUpsertRequestNoChecked(p, t)).forEach(x -> { // NOSONAR
          ElasticTransformer.pushToBulkProcessor(flightLog, bp, x);
        });
  }

  @Override
  protected int normalizeClientReferrals(int cntr, MinClientReferral rc1, final String clientId,
      final Map<String, EsPersonReferral> mapReferrals,
      final List<EsPersonReferral> listReadyToNorm,
      final Map<String, List<EsPersonReferral>> mapAllegationByReferral) {
    int ret = cntr;
    final String referralId = rc1.getReferralId();
    final EsPersonReferral ref = mapReferrals.get(referralId);

    // Sealed and sensitive may be excluded.
    if (ref != null) {
      // Loop allegations for this referral:
      if (mapAllegationByReferral.containsKey(referralId)) {
        for (EsPersonReferral alg : mapAllegationByReferral.get(referralId)) {
          alg.mergeClientReferralInfo(clientId, ref);
          listReadyToNorm.add(alg);
        }
      } else {
        listReadyToNorm.add(ref);
      }
    }

    final ReplicatedPersonReferrals repl = normalizeSingle(listReadyToNorm);
    if (repl != null) {
      ++ret;
      repl.setClientId(clientId);

      // NEXT: retrieve doc, modify, index.
      addToIndexQueue(repl); // CHANGE THIS
    }

    return ret;
  }

  /**
   * Rocket entry point.
   * 
   * @param args command line arguments
   * @throws Exception on launch error
   */
  public static void main(String... args) throws Exception {
    LaunchCommand.launchOneWayTrip(ReferralRocket.class, args);
  }

}
