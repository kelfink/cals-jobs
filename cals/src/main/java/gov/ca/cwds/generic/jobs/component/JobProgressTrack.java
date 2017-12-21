package gov.ca.cwds.generic.jobs.component;

import gov.ca.cwds.data.std.ApiObjectIdentity;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Track job progress and record counts.
 * 
 * @author CWDS API Team
 */
public class JobProgressTrack extends ApiObjectIdentity {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Official start time.
   */
  private final long startTime = System.currentTimeMillis();

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

  private final List<Pair<String, String>> initialLoadRangesStarted = new ArrayList<>();

  private final List<Pair<String, String>> initialLoadRangesCompleted = new ArrayList<>();

  public AtomicInteger getRecsSentToIndexQueue() {
    return recsSentToIndexQueue;
  }

  public AtomicInteger getRecsSentToBulkProcessor() {
    return recsSentToBulkProcessor;
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

  public int trackQueuedToIndex() {
    return this.getRecsSentToIndexQueue().incrementAndGet();
  }

  public int trackNormalized() {
    return this.rowsNormalized.incrementAndGet();
  }

  public int trackBulkDeleted() {
    return this.getRecsBulkDeleted().getAndIncrement();
  }

  public int trackBulkPrepared() {
    return this.getRecsBulkPrepared().getAndIncrement();
  }

  public int trackBulkError() {
    return this.getRecsBulkError().getAndIncrement();
  }

  public void trackRangeStart(final Pair<String, String> pair) {
    initialLoadRangesStarted.add(pair);
  }

  public void trackRangeComplete(final Pair<String, String> pair) {
    initialLoadRangesCompleted.add(pair);
  }

  @Override
  public String toString() {
    final long endTime = System.currentTimeMillis();
    return MessageFormat.format(
        "STATS: \nindexed:  {0}\ndeleted: {1}\nbulk before: {2}\nbulk after:  {3}\nbulk error:  {4}\nELAPSED TIME:  {5} SECONDS",
        getRecsBulkPrepared(), getRecsBulkDeleted(), getRecsBulkBefore(), getRecsBulkAfter(),
        getRecsBulkError(), ((endTime - startTime) / 1000));
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }
}
