package gov.ca.cwds.jobs.cals.util.elastic;

import gov.ca.cwds.cals.Identifiable;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import gov.ca.cwds.generic.jobs.util.JobWriter;
import gov.ca.cwds.jobs.cals.CalsElasticsearchIndexerDao;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author CWDS TPT-2
 *
 * @param <T> persistence class type
 */
public class ElasticJobWriter<T extends Identifiable<String>> implements JobWriter<T> {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ElasticJobWriter.class);
  protected CalsElasticsearchIndexerDao elasticsearchDao;
  protected BulkProcessor bulkProcessor;
  protected ObjectMapper objectMapper;

  /**
   * Constructor.
   * 
   * @param elasticsearchDao ES DAO
   * @param objectMapper Jackson object mapper
   */
  public ElasticJobWriter(CalsElasticsearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
    this.elasticsearchDao = elasticsearchDao;
    this.objectMapper = objectMapper;
    bulkProcessor =
        BulkProcessor.builder(elasticsearchDao.getClient(), new BulkProcessor.Listener() {
          @Override
          public void beforeBulk(long executionId, BulkRequest request) {
            LOGGER.warn("Ready to execute bulk of {} actions", request.numberOfActions());
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            LOGGER.warn("Executed bulk of {} actions", request.numberOfActions());
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            LOGGER.error("ERROR EXECUTING BULK", failure);
          }
        }).build();
  }

  @Override
  public void write(List<T> items) {
    items.stream().map(item -> {
      try {
        return elasticsearchDao.bulkAdd(objectMapper, item.getId(), item);
      } catch (JsonProcessingException e) {
        throw new JobsException(e);
      }
    }).forEach(bulkProcessor::add);
    bulkProcessor.flush();
  }

  @Override
  public void destroy() {
    try {
      try {
        bulkProcessor.awaitClose(3000, TimeUnit.MILLISECONDS);
      } finally {
        elasticsearchDao.close();
      }
    } catch (IOException |InterruptedException e) {
      throw new JobsException(e);
    }
  }
}
