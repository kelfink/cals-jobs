package gov.ca.cwds.jobs.component;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.exception.NeutronException;

public interface AtomValidateDocument {

  Logger getLog();

  JobProgressTrack getTrack();

  ElasticsearchDao getEsDao();

  default void validate() throws NeutronException {
    getTrack().getAffectedDocumentIds();
    final Client esClient = getEsDao().getClient();

    final SearchRequestBuilder srb2 = esClient.prepareSearch().setQuery(QueryBuilders
        .multiMatchQuery("N6dhOan15A", "cases.focus_child.legacy_descriptor.legacy_id"));
    final MultiSearchResponse sr =
        esClient.prepareMultiSearch()
            .add(esClient.prepareSearch().setQuery(QueryBuilders.idsQuery().addIds("Ahr3T2S0BN",
                "Bn0LhX6aah", "DUy4ET400b", "AkxX6G50Ki", "E5pf1dg0Py", "CtMFii209X")))
            .add(srb2).get();

    long totalHits = 0;
    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchResponse response = item.getResponse();
      final SearchHits hits = response.getHits();
      totalHits += hits.getTotalHits();

      try {
        for (SearchHit hit : hits.getHits()) {
          final String json = hit.getSourceAsString();
          getLog().info("json: {}", json);
          final ElasticSearchPerson person = readPerson(json);
          getLog().info("person: {}", person);
        }
      } catch (NeutronException e) {
        getLog().warn("whatever", e);
      }
    }

    // getLog().info("es host: {}", validator.getEsDao().getConfig().getElasticsearchHost());
    getLog().info("total hits: {}", totalHits);
  }

}
