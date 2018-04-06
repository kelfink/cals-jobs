package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public class TestChangedIdentifiersService implements ChangedEntitiesIdentifiersService {

  private List<ChangedEntityIdentifier> identifiers;

  public TestChangedIdentifiersService(List<ChangedEntityIdentifier> identifiers) {
    this.identifiers = identifiers;
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest) {
    return getNextPage(pageRequest);
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(LocalDateTime timestamp,
      PageRequest pageRequest) {
    return getNextPage(pageRequest);
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp,
      PageRequest pageRequest) {
    return getNextPage(pageRequest);
  }

  private List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    int indexFrom = pageRequest.getOffset();
    int indexTo =
        pageRequest.getOffset() + pageRequest.getLimit() > identifiers.size() ?
            identifiers.size() : pageRequest.getOffset() + pageRequest.getLimit();
    return indexFrom < indexTo ? identifiers.subList(indexFrom, indexTo) : Collections.emptyList();
  }

}
