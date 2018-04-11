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

  private PageRequest pageRequest;
  private JobMode jobMode;

  @Override
  public void init() {
    pageRequest = new PageRequest(0, batchSize);
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
    LocalDateTime lastTimeStamp = identifiers.get(identifiers.size() - 1).getTimestamp();
    if (lastTimeStamp == null) {
      pageRequest.incrementPage();
      return Collections.singletonList(new JobBatch(identifiers));
    } else {
      return calculateNextPortion(identifiers);
    }
  }

  private List<ChangedEntityIdentifier> getNextPage() {
    return getNextPage(pageRequest);
  }

  private List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
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
    List<JobBatch> nextPortion = new ArrayList<>();
    List<ChangedEntityIdentifier> nextIdentifiersPage = identifiers;
    while ((!nextIdentifiersPage.isEmpty() && getLastTimestamp(identifiers) ==
        getLastTimestamp(nextIdentifiersPage))) {
      pageRequest.incrementPage();
      nextPortion.add(new JobBatch(nextIdentifiersPage));
      nextIdentifiersPage = getNextPage();
    }

    List<ChangedEntityIdentifier> nextIdentifier = getNextIdentifier();

    while (!nextIdentifier.isEmpty() &&
        (nextIdentifier.get(0).getTimestamp() == getLastTimestamp(identifiers))) {
      pageRequest.increment();
      assert nextIdentifier.size() == 1;
      nextPortion.get(nextPortion.size() - 1).getChangedEntityIdentifiers()
          .add(nextIdentifier.get(0));
      nextIdentifier = getNextIdentifier();
    }
    nextPortion.get(nextPortion.size() - 1).setTimestamp(getLastTimestamp(identifiers));
    return nextPortion;
  }

  private List<ChangedEntityIdentifier> getNextIdentifier() {
    return getNextPage(new PageRequest(pageRequest.getOffset(), 1));
  }

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
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

  public void setPageRequest(PageRequest pageRequest) {
    this.pageRequest = pageRequest;
  }

  public void setJobMode(JobMode jobMode) {
    this.jobMode = jobMode;
  }
}
