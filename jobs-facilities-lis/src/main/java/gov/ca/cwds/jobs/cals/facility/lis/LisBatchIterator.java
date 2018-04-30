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
    LOGGER.info("Next page prepared. List size: {}. Last Id: {}", identifiers.size(), lastId.get());
    //TODO: remove after testing of bug hypothesis
    if (identifiers.size() > getBatchSize()) {
      identifiers = identifiers.subList(0, getBatchSize());
      lastId.set(getLastId(identifiers));
      LOGGER.info("Next page cut to the batch size. Adjusted list size: {}. Last Id: {}", identifiers.size(), lastId.get());
    }
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

  protected static int getLastId(List<ChangedEntityIdentifier> identifiers) {
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getIntId));
    return identifiers.get(identifiers.size() - 1).getIntId();
  }

}
