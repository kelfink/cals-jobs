package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public class TestChangedIdentifiersService implements
    ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint> {

  private List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers;

  public TestChangedIdentifiersService(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers) {
    this.identifiers = identifiers;
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint>> getIdentifiersForInitialLoad(
      PageRequest pageRequest) {
    return getNextPage(pageRequest, identifiers);
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint>> getIdentifiersForResumingInitialLoad(
      TimestampSavePoint timestampSavePoint,
      PageRequest pageRequest) {
    return getNextPage(pageRequest, getFilteredIdentifiers(timestampSavePoint));
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint>> getIdentifiersForIncrementalLoad(
      TimestampSavePoint timestampSavePoint,
      PageRequest pageRequest) {
    return getNextPage(pageRequest, getFilteredIdentifiers(timestampSavePoint));
  }

  private List<ChangedEntityIdentifier<TimestampSavePoint>> getFilteredIdentifiers(
      TimestampSavePoint timestampSavePoint) {
    List<ChangedEntityIdentifier<TimestampSavePoint>> filteredIdentifiers = new ArrayList<>(
        identifiers);
    CollectionUtils.filter(filteredIdentifiers, id -> {
      return id.getSavePoint().getTimestamp() != null && id.getSavePoint().getTimestamp()
          .isAfter(timestampSavePoint.getTimestamp());
    });
    return filteredIdentifiers;
  }

  private static List<ChangedEntityIdentifier<TimestampSavePoint>> getNextPage(
      PageRequest pageRequest,
      List<ChangedEntityIdentifier<TimestampSavePoint>> filteredIdentifiers) {
    if (filteredIdentifiers.isEmpty()) {
      return Collections.emptyList();
    }
    int indexFrom = pageRequest.getOffset();
    int indexTo =
        pageRequest.getOffset() + pageRequest.getLimit() > filteredIdentifiers.size() ?
            filteredIdentifiers.size() : pageRequest.getOffset() + pageRequest.getLimit();
    return indexFrom < indexTo ? new ArrayList<>(filteredIdentifiers.subList(indexFrom, indexTo))
        : Collections.emptyList();
  }

}
