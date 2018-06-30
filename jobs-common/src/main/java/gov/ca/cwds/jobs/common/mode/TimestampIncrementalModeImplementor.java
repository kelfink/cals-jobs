package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainer;
import java.util.List;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public class TimestampIncrementalModeImplementor<E> extends
    AbstractTimestampJobModeImplementor<E> {

  @Inject
  private ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint> changedEntitiesIdentifiersService;

  @Override
  protected List<ChangedEntityIdentifier<TimestampSavePoint>> getNextPage(PageRequest pageRequest) {
    TimestampSavePoint savePoint = loadSavePoint(TimestampSavePointContainer.class);
    return changedEntitiesIdentifiersService
        .getIdentifiersForIncrementalLoad(savePoint, pageRequest);
  }

}
