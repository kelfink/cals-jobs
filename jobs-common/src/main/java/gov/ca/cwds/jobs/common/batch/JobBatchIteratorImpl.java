package gov.ca.cwds.jobs.common.batch;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersProvider;
import gov.ca.cwds.jobs.common.inject.JobBatchSize;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/29/2018.
 */
public class JobBatchIteratorImpl implements JobBatchIterator {

  @Inject
  @JobBatchSize
  private int batchSize;

  private PageRequest pageRequest;

  @Inject
  private ChangedIdentifiersProvider changedIdentifiersProvider;

  @Override
  public void init() {
    pageRequest = new PageRequest(0, batchSize);
  }

  @Override
  public List<JobBatch> getNextPortion() {
    List<ChangedEntityIdentifier> identifiers =
        changedIdentifiersProvider.getNextPage(pageRequest);
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

  private List<JobBatch> calculateNextPortion(
      List<ChangedEntityIdentifier> identifiers) {
    List<JobBatch> nextPortion = new ArrayList<>();
    List<ChangedEntityIdentifier> nextIdentifiersPage = identifiers;
    while ((!nextIdentifiersPage.isEmpty() && getLastTimestamp(identifiers) ==
        getLastTimestamp(nextIdentifiersPage))) {
      pageRequest.incrementPage();
      nextPortion.add(new JobBatch(nextIdentifiersPage));
      nextIdentifiersPage = changedIdentifiersProvider.getNextPage(pageRequest);
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
    return changedIdentifiersProvider.getNextPage(
        new PageRequest(pageRequest.getOffset(), 1));
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

  public void setChangedIdentifiersProvider(
      ChangedIdentifiersProvider changedIdentifiersProvider) {
    this.changedIdentifiersProvider = changedIdentifiersProvider;
  }

}
