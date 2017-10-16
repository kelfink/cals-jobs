package gov.ca.cwds.jobs.component;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;

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

  private static final int ES_BULK_SIZE = 5000;

  private static final int ES_BYTES_MB = 14;

  /**
   * Track job progress.
   */
  protected final JobProgressTrack track;

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
    return BulkProcessor.builder(esDao.getClient(), new NeutronBulkProcessorListener(this.track))
        .setBulkActions(ES_BULK_SIZE).setBulkSize(new ByteSizeValue(ES_BYTES_MB, ByteSizeUnit.MB))
        .setConcurrentRequests(1).setName("jobs_bp").build();
  }

}
