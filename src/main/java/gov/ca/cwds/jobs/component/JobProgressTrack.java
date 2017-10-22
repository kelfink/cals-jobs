package gov.ca.cwds.jobs.component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.schedule.NeutronJobExecutionStatus;

/**
 * Track job progress and record counts.
 * 
 * @author CWDS API Team
 */
public class JobProgressTrack implements ApiMarker {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Official start time.
   */
  private final long startTime = System.currentTimeMillis();

  private long endTime;

  private boolean initialLoad;

  private Date lastChangeSince;

  private NeutronJobExecutionStatus status = NeutronJobExecutionStatus.NOT_STARTED;

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
    return this.getRecsBulkDeleted().incrementAndGet();
  }

  public int trackBulkPrepared() {
    return this.getRecsBulkPrepared().incrementAndGet();
  }

  public int trackBulkError() {
    return this.getRecsBulkError().incrementAndGet();
  }

  public void trackRangeStart(final Pair<String, String> pair) {
    initialLoadRangesStarted.add(pair);
  }

  public void trackRangeComplete(final Pair<String, String> pair) {
    initialLoadRangesCompleted.add(pair);
  }

  public void start() {
    this.status = NeutronJobExecutionStatus.RUNNING;
  }

  public void fail() {
    this.status = NeutronJobExecutionStatus.FAILED;
  }

  public void done() {
    this.endTime = System.currentTimeMillis();

    if (this.status != NeutronJobExecutionStatus.FAILED) {
      this.status = NeutronJobExecutionStatus.SUCCEEDED;
    }
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  public List<Pair<String, String>> getInitialLoadRangesStarted() {
    return initialLoadRangesStarted;
  }

  public List<Pair<String, String>> getInitialLoadRangesCompleted() {
    return initialLoadRangesCompleted;
  }

  private String pad(Integer padme) {
    return StringUtils.leftPad(new DecimalFormat("###,###,###").format(padme.intValue()), 8, ' ');
  }

  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder();
    buf.append("[\n    JOB STATUS: ").append(status);

    if (initialLoad) {
      buf.append("\n\n    INITIAL LOAD:\n\tranges started:  ")
          .append(pad(initialLoadRangesStarted.size())).append("\n\tranges completed:")
          .append(pad(initialLoadRangesCompleted.size()));
    } else {
      buf.append("\n\n    LAST CHANGE:\n\tchanged since:          ").append(this.lastChangeSince);
    }

    buf.append("\n\n    RUN TIME:\n\tstart:                  ").append(new Date(startTime))
        .append("\n\tend:                    ").append(new Date(endTime))
        .append("\n\telapsed (seconds):      ").append((endTime - startTime) / 1000)
        .append("\n\n    ELASTICSEARCH:").append("\n\tdenormalized:    ")
        .append(pad(this.getRecsSentToIndexQueue().get())).append("\n\tnormalized:      ")
        .append(pad(rowsNormalized.get())).append("\n\tto bulk:         ")
        .append(pad(recsSentToBulkProcessor.get())).append("\n\tbulk prepared:   ")
        .append(pad(recsBulkPrepared.get())).append("\n\tbulk deleted:    ")
        .append(pad(recsBulkDeleted.get())).append("\n\tbulk before:     ")
        .append(pad(recsBulkBefore.get())).append("\n\tbulk after:      ")
        .append(pad(recsBulkAfter.get())).append("\n\tbulk errors:     ")
        .append(pad(recsBulkError.get()));

    buf.append("\n]");
    return buf.toString();
  }

  public boolean isInitialLoad() {
    return initialLoad;
  }

  public void setInitialLoad(boolean initialLoad) {
    this.initialLoad = initialLoad;
  }

  public Date getLastChangeSince() {
    return lastChangeSince;
  }

  public void setLastChangeSince(Date lastChangeSince) {
    this.lastChangeSince = lastChangeSince;
  }

}
