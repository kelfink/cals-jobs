package gov.ca.cwds.jobs.component;

import java.util.EnumMap;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.schedule.FlightStatus;

public class FlightSummary implements ApiMarker {

  private static long serialVersionUID = 1L;

  /**
   * Runtime rocket name. Distinguish this rocket's threads from other running threads.
   */
  private String rocketName;

  private EnumMap<FlightStatus, Integer> status = new EnumMap<>(FlightStatus.class);

  private int totalRuns;

  private int recsSentToIndexQueue;

  private int recsSentToBulkProcessor;

  private int rowsNormalized;

  /**
   * Running count of records prepared for bulk indexing.
   */
  private int recsBulkPrepared;

  /**
   * Running count of records prepared for bulk deletion.
   */
  private int recsBulkDeleted;

  /**
   * Running count of records before bulk indexing.
   */
  private int recsBulkBefore;

  /**
   * Running count of records after bulk indexing.
   */
  private int recsBulkAfter;

  /**
   * Running count of errors during bulk indexing.
   */
  private int recsBulkError;

  public FlightSummary() {
    // default, no-op.
  }

  public synchronized void accumulate(final FlightLog flightLog) {
    totalRuns++;
    this.recsBulkDeleted = flightLog.getCurrentBulkDeleted();
    this.recsBulkPrepared = flightLog.getCurrentBulkPrepared();
    this.recsBulkDeleted = flightLog.getCurrentBulkDeleted();
    this.rowsNormalized = flightLog.getCurrentNormalized();

    if (status.containsKey(flightLog.getStatus())) {
      status.put(flightLog.getStatus(), new Integer(status.get(flightLog.getStatus()) + 1));
    } else {
      status.put(flightLog.getStatus(), 1);
    }
  }

}
