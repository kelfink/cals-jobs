package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.common.JobMode;
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
    JobBatch jobBatch = new JobBatch(identifiers, getLastTimestamp(identifiers), lastId.get());
    return Collections.singletonList(jobBatch);
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

  private static LocalDateTime getLastTimestamp(List<ChangedEntityIdentifier> identifiers) {
    return identifiers.get(identifiers.size() - 1).getTimestamp();
  }

  private static int getLastId(List<ChangedEntityIdentifier> identifiers) {
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getId));
    return Integer.valueOf(identifiers.get(identifiers.size() - 1).getId());
  }

}
