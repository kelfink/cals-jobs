package gov.ca.cwds.jobs.component;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.schedule.FlightStatus;

public class FlightSummary implements ApiMarker {

  private static long serialVersionUID = 1L;

  /**
   * Runtime rocket name. Distinguish this rocket's threads from other running threads.
   */
  private String rocketName;

  private Date firstStart = new Date();

  private Date lastEnd = new Date();

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
    this.recsBulkDeleted += flightLog.getCurrentBulkDeleted();
    this.recsBulkPrepared += flightLog.getCurrentBulkPrepared();
    this.recsBulkDeleted += flightLog.getCurrentBulkDeleted();
    this.rowsNormalized += flightLog.getCurrentNormalized();

    final Date startTime = new Date(flightLog.getStartTime());
    if (firstStart.before(startTime)) {
      firstStart = startTime;
    }

    final Date endTime = new Date(flightLog.getEndTime());
    if (lastEnd.before(endTime)) {
      lastEnd = endTime;
    }

    if (status.containsKey(flightLog.getStatus())) {
      status.put(flightLog.getStatus(), new Integer(status.get(flightLog.getStatus()) + 1));
    } else {
      status.put(flightLog.getStatus(), 1);
    }
  }

  public String getRocketName() {
    return rocketName;
  }

  public void setRocketName(String rocketName) {
    this.rocketName = rocketName;
  }

  public Map<FlightStatus, Integer> getStatus() {
    return status;
  }

  public void setStatus(EnumMap<FlightStatus, Integer> status) {
    this.status = status;
  }

  public int getTotalRuns() {
    return totalRuns;
  }

  public void setTotalRuns(int totalRuns) {
    this.totalRuns = totalRuns;
  }

  public int getRecsSentToIndexQueue() {
    return recsSentToIndexQueue;
  }

  public void setRecsSentToIndexQueue(int recsSentToIndexQueue) {
    this.recsSentToIndexQueue = recsSentToIndexQueue;
  }

  public int getRecsSentToBulkProcessor() {
    return recsSentToBulkProcessor;
  }

  public void setRecsSentToBulkProcessor(int recsSentToBulkProcessor) {
    this.recsSentToBulkProcessor = recsSentToBulkProcessor;
  }

  public int getRowsNormalized() {
    return rowsNormalized;
  }

  public void setRowsNormalized(int rowsNormalized) {
    this.rowsNormalized = rowsNormalized;
  }

  public int getRecsBulkPrepared() {
    return recsBulkPrepared;
  }

  public void setRecsBulkPrepared(int recsBulkPrepared) {
    this.recsBulkPrepared = recsBulkPrepared;
  }

  public int getRecsBulkDeleted() {
    return recsBulkDeleted;
  }

  public void setRecsBulkDeleted(int recsBulkDeleted) {
    this.recsBulkDeleted = recsBulkDeleted;
  }

  public int getRecsBulkBefore() {
    return recsBulkBefore;
  }

  public void setRecsBulkBefore(int recsBulkBefore) {
    this.recsBulkBefore = recsBulkBefore;
  }

  public int getRecsBulkAfter() {
    return recsBulkAfter;
  }

  public void setRecsBulkAfter(int recsBulkAfter) {
    this.recsBulkAfter = recsBulkAfter;
  }

  public int getRecsBulkError() {
    return recsBulkError;
  }

  public void setRecsBulkError(int recsBulkError) {
    this.recsBulkError = recsBulkError;
  }

  public Date getFirstStart() {
    return firstStart;
  }

  public void setFirstStart(Date earliestStart) {
    this.firstStart = earliestStart;
  }

  public Date getLastEnd() {
    return lastEnd;
  }

  public void setLastEnd(Date lastEnd) {
    this.lastEnd = lastEnd;
  }

}
