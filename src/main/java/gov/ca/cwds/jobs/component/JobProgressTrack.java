package gov.ca.cwds.jobs.component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.schedule.NeutronJobExecutionStatus;
import gov.ca.cwds.jobs.util.JobDateUtil;

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

  private String jobName;

  /**
   * Official start time.
   */
  private long startTime = System.currentTimeMillis();

  /**
   * Official end time.
   */
  private long endTime;

  private boolean initialLoad;

  private Date lastChangeSince; // last change only

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

  // initial load only
  private final List<Pair<String, String>> initialLoadRangesStarted = new ArrayList<>();

  // initial load only
  private final List<Pair<String, String>> initialLoadRangesCompleted = new ArrayList<>();

  // last change only
  private final Queue<String> affectedDocumentIds = new CircularFifoQueue<>();

  public int getCurrentQueuedToIndex() {
    return this.recsSentToIndexQueue.get();
  }

  public int getCurrentNormalized() {
    return this.rowsNormalized.get();
  }

  public int getCurrentBulkDeleted() {
    return this.recsBulkDeleted.get();
  }

  public int getCurrentBulkPrepared() {
    return this.recsBulkPrepared.get();
  }

  public int getCurrentBulkError() {
    return this.recsBulkError.get();
  }

  public int addToQueuedToIndex(int addMe) {
    return this.recsSentToIndexQueue.getAndAdd(addMe);
  }

  public int addToNormalized(int addMe) {
    return this.rowsNormalized.getAndAdd(addMe);
  }

  public int addToBulkDeleted(int addMe) {
    return this.recsBulkDeleted.getAndAdd(addMe);
  }

  public int addToBulkPrepared(int addMe) {
    return this.recsBulkPrepared.getAndAdd(addMe);
  }

  public int addToBulkError(int addMe) {
    return this.recsBulkError.getAndAdd(addMe);
  }

  public int addToBulkAfter(int addMe) {
    return this.recsBulkAfter.getAndAdd(addMe);
  }

  public int addToBulkBefore(int addMe) {
    return this.recsBulkBefore.getAndAdd(addMe);
  }

  public int trackQueuedToIndex() {
    return this.recsSentToIndexQueue.incrementAndGet();
  }

  public int trackNormalized() {
    return this.rowsNormalized.incrementAndGet();
  }

  public int trackBulkDeleted() {
    return this.recsBulkDeleted.incrementAndGet();
  }

  public int trackBulkPrepared() {
    return this.recsBulkPrepared.incrementAndGet();
  }

  public int trackBulkError() {
    return this.recsBulkError.incrementAndGet();
  }

  public void trackRangeStart(final Pair<String, String> pair) {
    initialLoadRangesStarted.add(pair);
  }

  public void trackRangeComplete(final Pair<String, String> pair) {
    initialLoadRangesCompleted.add(pair);
  }

  public void start() {
    if (this.status == NeutronJobExecutionStatus.NOT_STARTED) {
      this.status = NeutronJobExecutionStatus.RUNNING;
      startTime = System.currentTimeMillis();
    }
  }

  public void fail() {
    if (this.status != NeutronJobExecutionStatus.FAILED) {
      this.status = NeutronJobExecutionStatus.FAILED;
      this.endTime = System.currentTimeMillis();
    }
  }

  public void done() {
    if (this.status == NeutronJobExecutionStatus.RUNNING) {
      this.status = NeutronJobExecutionStatus.SUCCEEDED;
      this.endTime = System.currentTimeMillis();
    }
  }

  public List<Pair<String, String>> getInitialLoadRangesStarted() {
    final ImmutableList.Builder<Pair<String, String>> results = new ImmutableList.Builder<>();
    results.addAll(initialLoadRangesStarted);
    return results.build();
  }

  public List<Pair<String, String>> getInitialLoadRangesCompleted() {
    final ImmutableList.Builder<Pair<String, String>> results = new ImmutableList.Builder<>();
    results.addAll(initialLoadRangesCompleted);
    return results.build();
  }

  private String pad(Integer padme) {
    return StringUtils.leftPad(new DecimalFormat("###,###,###").format(padme.intValue()), 8, ' ');
  }

  public boolean isInitialLoad() {
    return initialLoad;
  }

  public void setInitialLoad(boolean initialLoad) {
    this.initialLoad = initialLoad;
  }

  public Date getLastChangeSince() {
    return JobDateUtil.freshDate(lastChangeSince);
  }

  public void setLastChangeSince(Date lastChangeSince) {
    this.lastChangeSince = JobDateUtil.freshDate(lastChangeSince);
  }

  public void addAffectedDocumentId(String docId) {
    affectedDocumentIds.add(docId);
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public NeutronJobExecutionStatus getStatus() {
    return status;
  }

  public String[] getAffectedDocumentIds() {
    return affectedDocumentIds.toArray(new String[0]);
  }

  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder();
    buf.append("[\n    JOB STATUS: ").append(status).append(":\t").append(jobName);

    if (initialLoad) {
      buf.append("\n\n    INITIAL LOAD:\n\tranges started:  ")
          .append(pad(initialLoadRangesStarted.size())).append("\n\tranges completed:")
          .append(pad(initialLoadRangesCompleted.size()));
    } else {
      buf.append("\n\n    LAST CHANGE:\n\tchanged since:          ").append(this.lastChangeSince);
    }

    buf.append("\n\n    RUN TIME:\n\tstart:                  ").append(new Date(startTime));
    if (endTime > 0L) {
      buf.append("\n\tend:                    ").append(new Date(endTime))
          .append("\n\telapsed (seconds):      ").append((endTime - startTime) / 1000);
    }

    buf.append("\n\n    RECORDS RETRIEVED:").append("\n\tdenormalized:    ")
        .append(pad(recsSentToIndexQueue.get())).append("\n\tnormalized:      ")
        .append(pad(rowsNormalized.get())).append("\n\n    ELASTICSEARCH:")
        .append("\n\tto bulk:         ").append(pad(recsSentToBulkProcessor.get()))
        .append("\n\tbulk prepared:   ").append(pad(recsBulkPrepared.get()))
        .append("\n\tbulk deleted:    ").append(pad(recsBulkDeleted.get()))
        .append("\n\tbulk before:     ").append(pad(recsBulkBefore.get()))
        .append("\n\tbulk after:      ").append(pad(recsBulkAfter.get()))
        .append("\n\tbulk errors:     ").append(pad(recsBulkError.get()));

    if (!initialLoad && !affectedDocumentIds.isEmpty()) {
      buf.append("\n\n    SAMPLE DOCUMENTS:").append("\n\tdocument id's:    ")
          .append(StringUtils.join(getAffectedDocumentIds()));
    }

    buf.append("\n]");
    return buf.toString();
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
