package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchIteratorImpl;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LisBatchIterator extends JobBatchIteratorImpl {

  private AtomicInteger lastId = new AtomicInteger(0);

  @Override
  public List<JobBatch> getNextPortion() {
    List<ChangedEntityIdentifier> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getIntId));
    lastId.set(getLastId(identifiers));
    JobBatch jobBatch = new JobBatch(identifiers, getLastTimestamp(identifiers));
    return Collections.singletonList(jobBatch);
  }

  @Override
  protected List<ChangedEntityIdentifier> getNextPage() {
    PageRequest pageRequest = new PageRequest(getNextOffset().get(), getBatchSize(), lastId.get());
    return getNextPage(pageRequest);
  }

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
  }

  private static int getLastId(List<ChangedEntityIdentifier> identifiers) {
    return Integer.parseInt(identifiers.get(identifiers.size() - 1).getId());
  }

}
