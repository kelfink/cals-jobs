package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.common.JobMode;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchIteratorImpl;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LisBatchIterator extends JobBatchIteratorImpl {

  @Override
  public List<JobBatch> getNextPortion() {
    List<ChangedEntityIdentifier> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    LocalDateTime lastTimeStamp = identifiers.get(identifiers.size() - 1).getTimestamp();
    if (lastTimeStamp == null) {
      getPageRequest().incrementPage();
      return Collections.singletonList(new JobBatch(identifiers));
    } else {
      return calculateNextPortion(identifiers);
    }
  }

  private List<ChangedEntityIdentifier> getNextPage() {
    return getNextPage(getPageRequest());
  }

  private List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    if (getJobMode() == JobMode.INITIAL_LOAD) {
      return getChangedEntitiesIdentifiersService().getIdentifiersForInitialLoad(pageRequest);
    } else if (getJobMode() == JobMode.INITIAL_LOAD_RESUME) {
      return getChangedEntitiesIdentifiersService()
          .getIdentifiersForResumingInitialLoad(getTimestampOperator().readTimestamp(), pageRequest);
    } else if (getJobMode() == JobMode.INCREMENTAL_LOAD) {
      return getChangedEntitiesIdentifiersService()
          .getIdentifiersForIncrementalLoad(getTimestampOperator().readTimestamp(), pageRequest);
    }
    throw new IllegalStateException("Unexpected job mode");
  }

  private List<JobBatch> calculateNextPortion(
      List<ChangedEntityIdentifier> identifiers) {
    List<JobBatch> nextPortion = new ArrayList<>();
    List<ChangedEntityIdentifier> nextIdentifiersPage = identifiers;
    while ((!nextIdentifiersPage.isEmpty() && getLastTimestamp(identifiers) ==
        getLastTimestamp(nextIdentifiersPage))) {
      getPageRequest().incrementPage();
      nextPortion.add(new JobBatch(nextIdentifiersPage));
      nextIdentifiersPage = getNextPage();
    }

    List<ChangedEntityIdentifier> nextIdentifier = getNextIdentifier();

    while (!nextIdentifier.isEmpty() &&
        (nextIdentifier.get(0).getTimestamp() == getLastTimestamp(identifiers))) {
      getPageRequest().increment();
      getPageRequest().setLastId(getLastId(identifiers));
      assert nextIdentifier.size() == 1;
      nextPortion.get(nextPortion.size() - 1).getChangedEntityIdentifiers()
          .add(nextIdentifier.get(0));
      nextIdentifier = getNextIdentifier();
    }
    nextPortion.get(nextPortion.size() - 1).setTimestamp(getLastTimestamp(identifiers));
    return nextPortion;
  }

  private List<ChangedEntityIdentifier> getNextIdentifier() {
    return getNextPage(new PageRequest(getPageRequest().getOffset(), 1));
  }

  private static int getLastId(List<ChangedEntityIdentifier> identifiers) {
    return Integer.valueOf(identifiers.get(identifiers.size() - 1).getId());
  }

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
  }

}
