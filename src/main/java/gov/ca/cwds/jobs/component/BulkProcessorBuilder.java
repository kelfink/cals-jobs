package gov.ca.cwds.jobs.component;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.launch.listener.NeutronBulkProcessorListener;

/**
 * Builds and executes Elasticsearch bulk processor for bulk loading.
 * 
 * @author CWDS API Team
 */
public class BulkProcessorBuilder implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private static final int ES_BULK_SIZE = 1000;

  private static final int ES_BYTES_MB = 5;

  /**
   * Track rocket progress.
   */
  protected final FlightLog flightLog;

  /**
   * Elasticsearch client DAO.
   */
  protected transient ElasticsearchDao esDao;

  /**
   * Constructor.
   * 
   * @param esDao ES DAO
   * @param flightLog progress tracker
   */
  public BulkProcessorBuilder(final ElasticsearchDao esDao, final FlightLog flightLog) {
    this.esDao = esDao;
    this.flightLog = flightLog;
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * <p>
   * ES BulkProcessor is technically thread safe, but you can safely construct an instance per
   * thread, if desired.
   * </p>
   * 
   * <p>
   * NEXT: make configurable.
   * </p>
   * 
   * @return an ES bulk processor
   */
  public BulkProcessor buildBulkProcessor() {
    return BulkProcessor
        .builder(esDao.getClient(), new NeutronBulkProcessorListener(this.flightLog))
        .setBulkActions(ES_BULK_SIZE).setBulkSize(new ByteSizeValue(ES_BYTES_MB, ByteSizeUnit.MB))
        .setConcurrentRequests(1).setName("jobs_bp") // disappears in ES 5.6.3
        .build();
  }

}
