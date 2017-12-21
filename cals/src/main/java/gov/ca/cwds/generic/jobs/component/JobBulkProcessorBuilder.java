package gov.ca.cwds.generic.jobs.component;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds and executes Elasticsearch bulk processor for bulk loading.
 * 
 * @author CWDS API Team
 */
public class JobBulkProcessorBuilder implements ApiMarker {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      JobBulkProcessorBuilder.class);

  private static final int ES_BULK_SIZE = 5000;

  private static final int ES_BYTES_MB = 14;

  /**
   * Track job progress.
   */
  protected transient JobProgressTrack track;

  /**
   * Elasticsearch client DAO.
   */
  protected transient ElasticsearchDao esDao;

  /**
   * Constructor.
   * 
   * @param esDao ES DAO
   * @param track progress tracker
   */
  public JobBulkProcessorBuilder(final ElasticsearchDao esDao, final JobProgressTrack track) {
    this.esDao = esDao;
    this.track = track;
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * <p>
   * ES BulkProcessor is technically thread safe, but you can safely construct an instance per
   * thread, if desired.
   * </p>
   * 
   * @return an ES bulk processor
   */
  public BulkProcessor buildBulkProcessor() {
    return BulkProcessor.builder(esDao.getClient(), new BulkProcessor.Listener() {

      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
        track.getRecsBulkBefore().getAndAdd(request.numberOfActions());
        LOGGER.debug("Ready to execute bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        track.getRecsBulkAfter().getAndAdd(request.numberOfActions());
        LOGGER.info("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        track.trackBulkError();
        LOGGER.error("ERROR EXECUTING BULK", failure);
      }
    }).setBulkActions(ES_BULK_SIZE).setBulkSize(new ByteSizeValue(ES_BYTES_MB, ByteSizeUnit.MB))
        .setConcurrentRequests(1).setName("jobs_bp").build();
  }

}
