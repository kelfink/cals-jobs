package gov.ca.cwds.jobs.component;

import java.io.IOException;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.JobLogs;

public interface AtomValidateDocument {

  Logger getLogger();

  FlightLog getTrack();

  ElasticsearchDao getEsDao();

  default ElasticSearchPerson readPerson(String json) throws NeutronException {
    try {
      return ElasticSearchPerson.MAPPER.readValue(json, ElasticSearchPerson.class);
    } catch (IOException e) {
      throw JobLogs.checked(getLogger(), e, "FAILED TO READ PERSON DOC! {}", e.getMessage(), e);
    }
  }

  default void processDocumentHits(final SearchHits hits) throws NeutronException {
    int docId = 0;
    String json;
    ElasticSearchPerson person;
    try {
      for (SearchHit hit : hits.getHits()) {
        docId = hit.docId();
        json = hit.getSourceAsString();

        getLogger().info("docId: {}", docId);
        getLogger().trace("json: {}", json);

        person = ElasticSearchPerson.readPerson(json);
        getLogger().trace("person: {}", person);

        validateDocument(person);
      }
    } catch (IOException e) {
      throw JobLogs.checked(getLogger(), e, "ERROR READING DOCUMENT! doc id: {}", docId);
    }
  }

  default boolean validateDocument(final ElasticSearchPerson person) throws NeutronException {
    return true;
  }

  default void validateDocuments() throws NeutronException {
    final String[] docIds = getTrack().getAffectedDocumentIds();
    long totalHits = 0;

    if (docIds != null && docIds.length > 0) {
      final String[] affectedDocIds = getTrack().getAffectedDocumentIds();
      if (affectedDocIds != null && affectedDocIds.length > 0) {
        final Client esClient = getEsDao().getClient();
        final MultiSearchResponse multiResponse = esClient.prepareMultiSearch()
            .add(esClient.prepareSearch().setQuery(QueryBuilders.idsQuery().addIds(affectedDocIds)))
            .get();

        for (MultiSearchResponse.Item item : multiResponse.getResponses()) {
          final SearchHits hits = item.getResponse().getHits();
          totalHits += hits.getTotalHits();
          processDocumentHits(hits);
        }
      }
    }

    getLogger().info("total hits: {}", totalHits);
  }

}
