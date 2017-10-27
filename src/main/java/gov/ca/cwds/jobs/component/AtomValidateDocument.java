package gov.ca.cwds.jobs.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.util.JobLogs;

public interface AtomValidateDocument {

  Logger getLog();

  JobProgressTrack getTrack();

  ElasticsearchDao getEsDao();

  default List<ElasticSearchPerson> validate() throws NeutronException {
    List<ElasticSearchPerson> persons = new ArrayList<>();
    final Client esClient = getEsDao().getClient();
    final MultiSearchResponse multiResponse =
        esClient.prepareMultiSearch()
            .add(esClient.prepareSearch()
                .setQuery(QueryBuilders.idsQuery().addIds(getTrack().getAffectedDocumentIds())))
            .get();

    long totalHits = 0;
    for (MultiSearchResponse.Item item : multiResponse.getResponses()) {
      final SearchResponse response = item.getResponse();
      final SearchHits hits = response.getHits();
      totalHits += hits.getTotalHits();

      int docId = 0;
      String json;
      ElasticSearchPerson person;
      try {
        for (SearchHit hit : hits.getHits()) {
          docId = hit.docId();
          json = hit.getSourceAsString();

          getLog().info("docId: {}", docId);
          getLog().trace("json: {}", json);

          person = ElasticSearchPerson.readPerson(json);
          getLog().info("person: {}", person);
        }
      } catch (IOException e) {
        throw JobLogs.buildCheckedException(getLog(), e, "ERROR READING DOCUMENT! doc id: {}",
            docId);
      }
    }

    getLog().info("total hits: {}", totalHits);
    return persons;
  }

}
