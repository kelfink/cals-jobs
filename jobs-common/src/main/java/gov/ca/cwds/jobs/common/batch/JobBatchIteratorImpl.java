package gov.ca.cwds.jobs.common.batch;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.Constants;
import gov.ca.cwds.jobs.common.JobMode;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.JobBatchSize;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/29/2018.
 */
public class JobBatchIteratorImpl implements JobBatchIterator {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(JobBatchIteratorImpl.class);

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  private ChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Inject
  private TimestampOperator timestampOperator;

  private int offset = 0;

  private JobMode jobMode;

  @Override
  public void init() {
    jobMode = defineJobMode();
  }

  private JobMode defineJobMode() {
    if (!timestampOperator.timeStampExists()) {
      LOGGER.info("Processing initial load");
      return JobMode.INITIAL_LOAD;
    } else if (isTimestampMoreThanOneMonthOld()) {
      LOGGER.info("Processing initial load - resuming after save point {}", timestampOperator
          .readTimestamp());
      return JobMode.INITIAL_LOAD_RESUME;
    } else {
      LocalDateTime timestamp = timestampOperator.readTimestamp();
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Processing incremental load after timestamp {}",
            Constants.DATE_TIME_FORMATTER.format(timestamp));
        return JobMode.INCREMENTAL_LOAD;
      }
      return JobMode.INCREMENTAL_LOAD;
    }
  }

  private boolean isTimestampMoreThanOneMonthOld() {
    return timestampOperator.readTimestamp().until(LocalDateTime.now(), ChronoUnit.MONTHS) > 1;
  }

  @Override
  public List<JobBatch> getNextPortion() {
    List<ChangedEntityIdentifier> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    if (timeStampsAreEmpty(identifiers)) {
      offset += batchSize;
      return Collections.singletonList(new JobBatch(identifiers));
    } else {
      return calculateNextPortion(identifiers);
    }
  }

  protected List<ChangedEntityIdentifier> getNextPage() {
    return getNextPage(new PageRequest(offset, batchSize));
  }

  protected List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    LOGGER.info("{}", pageRequest);
    if (jobMode == JobMode.INITIAL_LOAD) {
      return changedEntitiesIdentifiersService.getIdentifiersForInitialLoad(pageRequest);
    } else if (jobMode == JobMode.INITIAL_LOAD_RESUME) {
      return changedEntitiesIdentifiersService
          .getIdentifiersForResumingInitialLoad(timestampOperator.readTimestamp(), pageRequest);
    } else if (jobMode == JobMode.INCREMENTAL_LOAD) {
      return changedEntitiesIdentifiersService
          .getIdentifiersForIncrementalLoad(timestampOperator.readTimestamp(), pageRequest);
    }
    throw new IllegalStateException("Unexpected job mode");
  }

  private List<JobBatch> calculateNextPortion(
      List<ChangedEntityIdentifier> identifiers) {
    // it can be several batches in portion when
    // there are many entities with equal last updated timestamps
    LocalDateTime savePoint = getLastTimestamp(identifiers);
    List<JobBatch> batches = findBatchesPriorToBatchWithSavepoint(identifiers);
    List<ChangedEntityIdentifier> lastIdentifiersWithSavepoint =
        findLastIdentifiersPriorToSavepoint(savePoint);
    batches.get(batches.size() - 1).getChangedEntityIdentifiers()
        .addAll(lastIdentifiersWithSavepoint);
    batches.get(batches.size() - 1).setTimestamp(savePoint);
    return batches;
  }

  private List<ChangedEntityIdentifier> findLastIdentifiersPriorToSavepoint(LocalDateTime savePoint) {
    List<ChangedEntityIdentifier> identifiersWithSavepoint = new ArrayList<>(batchSize);
    ChangedEntityIdentifier nextIdentifier = getNextIdentifier();
    while (nextIdentifier != null &&
        (nextIdentifier.getTimestamp() == savePoint)) {
      offset++;
      identifiersWithSavepoint.add(nextIdentifier);
      nextIdentifier = getNextIdentifier();
    }
    return identifiersWithSavepoint;
  }

  private List<JobBatch> findBatchesPriorToBatchWithSavepoint(List<ChangedEntityIdentifier> identifiers) {
    List<JobBatch> nextPortion = new ArrayList<>();
    LocalDateTime savePoint = getLastTimestamp(identifiers);
    List<ChangedEntityIdentifier> nextIdentifiersPage = identifiers;
    while ((!nextIdentifiersPage.isEmpty() && savePoint ==
        getLastTimestamp(nextIdentifiersPage))) {
      offset += batchSize;
      nextPortion.add(new JobBatch(nextIdentifiersPage));
      nextIdentifiersPage = getNextPage();
    }
    return nextPortion;
  }

  private ChangedEntityIdentifier getNextIdentifier() {
    List<ChangedEntityIdentifier> identifiers = getNextPage(new PageRequest(offset, 1));
    assert identifiers.size() == 1 || identifiers.isEmpty();
    return identifiers.isEmpty() ? null : identifiers.get(0);
  }

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
  }

  private boolean timeStampsAreEmpty(List<ChangedEntityIdentifier> identifiers) {
    return getLastTimestamp(identifiers) == null;
  }

  @Override
  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setChangedEntitiesIdentifiersService(
      ChangedEntitiesIdentifiersService changedEntitiesIdentifiersService) {
    this.changedEntitiesIdentifiersService = changedEntitiesIdentifiersService;
  }

  public void setTimestampOperator(
      TimestampOperator timestampOperator) {
    this.timestampOperator = timestampOperator;
  }

  public void setJobMode(JobMode jobMode) {
    this.jobMode = jobMode;
  }

  public JobMode getJobMode() {
    return jobMode;
  }

  public ChangedEntitiesIdentifiersService getChangedEntitiesIdentifiersService() {
    return changedEntitiesIdentifiersService;
  }

  public TimestampOperator getTimestampOperator() {
    return timestampOperator;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}
