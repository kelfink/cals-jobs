package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.common.JobMode;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchIteratorImpl;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LisBatchIterator extends JobBatchIteratorImpl {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LisBatchIterator.class);

  private AtomicInteger lastId = new AtomicInteger(0);


  @Override
  public List<JobBatch> getNextPortion() {
    List<ChangedEntityIdentifier> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    lastId.set(getLastId(identifiers));
    return Collections.singletonList(new JobBatch(identifiers));
  }

  private List<ChangedEntityIdentifier> getNextPage() {
    PageRequest pageRequest = new PageRequest(getNextOffset().get(), getBatchSize(), lastId.get());
    return getNextPage(pageRequest);
  }

  private List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    LOGGER.info("{}", pageRequest);
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
    while ((!nextIdentifiersPage.isEmpty() && getLastId(identifiers) ==
        getLastId(nextIdentifiersPage))) {
      lastId.set(getLastId(identifiers));
      nextPortion.add(new JobBatch(nextIdentifiersPage));
      nextIdentifiersPage = getNextPage();
    }

    List<ChangedEntityIdentifier> nextIdentifier = getNextIdentifier();

    while (!nextIdentifier.isEmpty() &&
        (nextIdentifier.get(0).getTimestamp() == getLastTimestamp(identifiers))) {
      getNextOffset().addAndGet(getBatchSize());
      lastId.set(getLastId(identifiers));
      assert nextIdentifier.size() == 1;
      nextPortion.get(nextPortion.size() - 1).getChangedEntityIdentifiers()
          .add(nextIdentifier.get(0));
      nextIdentifier = getNextIdentifier();
    }
    nextPortion.get(nextPortion.size() - 1).setTimestamp(getLastTimestamp(identifiers));
    return nextPortion;
  }

  private List<ChangedEntityIdentifier> getNextIdentifier() {
    PageRequest pageRequest = new PageRequest(getNextOffset().get(), 1);
    pageRequest.setLastId(lastId.get());
    return getNextPage();
  }

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
  }

  private static int getLastId(List<ChangedEntityIdentifier> identifiers) {
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getId));
    return Integer.valueOf(identifiers.get(identifiers.size() - 1).getId());
  }

}
