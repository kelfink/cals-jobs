package gov.ca.cwds.neutron.launch.listener;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.neutron.flight.FlightLog;

public class NeutronBulkProcessorListener implements BulkProcessor.Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronBulkProcessorListener.class);

  /**
   * Track rocket's flight progress.
   */
  protected final FlightLog flightLog;

  public NeutronBulkProcessorListener(FlightLog flightLog) {
    this.flightLog = flightLog;
  }

  @Override
  public void beforeBulk(long executionId, BulkRequest request) {
    final int numActions = request.numberOfActions();
    flightLog.addToBulkBefore(numActions);
    LOGGER.debug("Ready to execute bulk of {} actions", numActions);
  }

  @Override
  public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
    final int numActions = request.numberOfActions();
    flightLog.addToBulkAfter(numActions);
    LOGGER.info("Executed bulk of {} actions", numActions);
  }

  @Override
  public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
    flightLog.trackBulkError();
    LOGGER.error("ERROR EXECUTING BULK", failure);
  }

}
