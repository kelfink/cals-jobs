package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersProvider;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public class TestChangedIdentifiersProvider implements ChangedIdentifiersProvider {

  private List<ChangedEntityIdentifier> identifiers;

  public TestChangedIdentifiersProvider(List<ChangedEntityIdentifier> identifiers) {
    this.identifiers = identifiers;
  }

  @Override
  public List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
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
