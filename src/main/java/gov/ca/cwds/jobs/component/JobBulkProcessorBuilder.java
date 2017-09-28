package gov.ca.cwds.jobs.component;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.JobProgressTrack;

public class JobBulkProcessorBuilder implements ApiMarker {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(JobBulkProcessorBuilder.class);

  /**
   * Track job progress.
   */
  protected final JobProgressTrack track;

  /**
   * Elasticsearch client DAO.
   */
  protected final ElasticsearchDao esDao;

  public JobBulkProcessorBuilder(final ElasticsearchDao esDao, final JobProgressTrack track) {
    this.esDao = esDao;
    this.track = track;
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
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
        track.getRecsBulkError().getAndIncrement();
        LOGGER.error("ERROR EXECUTING BULK", failure);
      }
    }).setBulkActions(JobFeatureHibernate.ES_BULK_SIZE)
        .setBulkSize(new ByteSizeValue(14, ByteSizeUnit.MB)).setConcurrentRequests(1)
        .setName("jobs_bp").build();
  }

}
