package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.cals.facility.cws.CwsRecordChange;
import gov.ca.cwds.jobs.cals.facility.cws.dao.RecordChangeCwsCmsDao;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class CwsChangedEntitiesIdentifiersService implements
    ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> {

  @Inject
  private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForInitialLoad(
      PageRequest pageRequest) {
    return getCwsCmsInitialLoadIdentifiers(pageRequest).distinct().sorted()
        .collect(Collectors.toList());
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForResumingInitialLoad(
      TimestampSavePoint<LocalDateTime> timeStampAfter, PageRequest pageRequest) {
    return getCwsCmsResumingInitialLoadIdentifiers(timeStampAfter, pageRequest);
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForIncrementalLoad(
      TimestampSavePoint<LocalDateTime> savePoint,
      PageRequest pageRequest) {
    return getCwsCmsIncrementalLoadIdentifiers(savePoint, pageRequest);
  }

  @UnitOfWork(CMS)
  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsCmsResumingInitialLoadIdentifiers(
      TimestampSavePoint<LocalDateTime> timeStampAfter, PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers<TimestampSavePoint<LocalDateTime>> changedEntityIdentifiers =
        new ChangedFacilitiesIdentifiers<>();
    recordChangeCwsCmsDao.getResumeInitialLoadStream(timeStampAfter.getTimestamp(), pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull).distinct().sorted()
        .collect(Collectors.toList());
  }

  @UnitOfWork(CMS)
  protected Stream<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsCmsInitialLoadIdentifiers(
      PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers<TimestampSavePoint<LocalDateTime>> changedEntityIdentifiers =
        new ChangedFacilitiesIdentifiers<>();
    recordChangeCwsCmsDao.getInitialLoadStream(pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

  @UnitOfWork(CMS)
  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsCmsIncrementalLoadIdentifiers(
      TimestampSavePoint<LocalDateTime> dateAfter, PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers<TimestampSavePoint<LocalDateTime>> changedEntityIdentifiers =
        new ChangedFacilitiesIdentifiers<>();
    recordChangeCwsCmsDao.getIncrementalLoadStream(dateAfter.getTimestamp(), pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull).distinct().sorted()
        .collect(Collectors.toList());
  }

}
