package gov.ca.cwds.jobs;

import java.util.concurrent.atomic.AtomicInteger;

import gov.ca.cwds.data.std.ApiObjectIdentity;

public class JobProgressTrack extends ApiObjectIdentity {

  private final AtomicInteger recsSentToIndexQueue = new AtomicInteger(0);

  private final AtomicInteger recsSentToBulkProcessor = new AtomicInteger(0);

  private final AtomicInteger rowsNormalized = new AtomicInteger(0);

  /**
   * Running count of records prepared for bulk indexing.
   */
  private final AtomicInteger recsBulkPrepared = new AtomicInteger(0);

  /**
   * Running count of records prepared for bulk deletion.
   */
  private final AtomicInteger recsBulkDeleted = new AtomicInteger(0);

  /**
   * Running count of records before bulk indexing.
   */
  private final AtomicInteger recsBulkBefore = new AtomicInteger(0);

  /**
   * Running count of records after bulk indexing.
   */
  private final AtomicInteger recsBulkAfter = new AtomicInteger(0);

  /**
   * Running count of errors during bulk indexing.
   */
  private final AtomicInteger recsBulkError = new AtomicInteger(0);

  public AtomicInteger getRecsSentToIndexQueue() {
    return recsSentToIndexQueue;
  }

  public AtomicInteger getRecsSentToBulkProcessor() {
    return recsSentToBulkProcessor;
  }

  public AtomicInteger getRowsNormalized() {
    return rowsNormalized;
  }

  public AtomicInteger getRecsBulkPrepared() {
    return recsBulkPrepared;
  }

  public AtomicInteger getRecsBulkDeleted() {
    return recsBulkDeleted;
  }

  public AtomicInteger getRecsBulkBefore() {
    return recsBulkBefore;
  }

  public AtomicInteger getRecsBulkAfter() {
    return recsBulkAfter;
  }

  public AtomicInteger getRecsBulkError() {
    return recsBulkError;
  }

}
