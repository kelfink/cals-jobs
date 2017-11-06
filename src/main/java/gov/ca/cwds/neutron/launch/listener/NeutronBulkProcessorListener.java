package gov.ca.cwds.neutron.launch.listener;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.component.FlightRecord;

public class NeutronBulkProcessorListener implements BulkProcessor.Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronBulkProcessorListener.class);

  /**
   * Track job progress.
   */
  protected final FlightRecord track;

  public NeutronBulkProcessorListener(FlightRecord track) {
    this.track = track;
  }

  @Override
  public void beforeBulk(long executionId, BulkRequest request) {
    final int numActions = request.numberOfActions();
    track.addToBulkBefore(numActions);
    LOGGER.debug("Ready to execute bulk of {} actions", numActions);
  }

  @Override
  public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
    final int numActions = request.numberOfActions();
    track.addToBulkAfter(numActions);
    LOGGER.info("Executed bulk of {} actions", numActions);
  }

  @Override
  public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
    track.trackBulkError();
    LOGGER.error("ERROR EXECUTING BULK", failure);
  }

}
