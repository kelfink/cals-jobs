package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class CwsChangedEntitiesIdentifiersService implements ChangedEntitiesIdentifiersService {

  @Inject
  private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest) {
    return getCwsCmsInitialLoadIdentifiers(pageRequest).collect(Collectors.toList());
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(
      LocalDateTime timeStampAfter, PageRequest pageRequest) {
    return getCwsCmsResumingInitialLoadIdentifiers(timeStampAfter, pageRequest);
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp,
      PageRequest pageRequest) {

    return getCwsCmsIncrementalLoadIdentifiers(timestamp, pageRequest);
  }

  @UnitOfWork(CMS)
  protected List<ChangedEntityIdentifier> getCwsCmsResumingInitialLoadIdentifiers(
      LocalDateTime timeStampAfter, PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers();
    recordChangeCwsCmsDao.getResumeInitialLoadStream(timeStampAfter, pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().distinct().filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @UnitOfWork(CMS)
  protected Stream<ChangedEntityIdentifier> getCwsCmsInitialLoadIdentifiers(
      PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers();
    recordChangeCwsCmsDao.getInitialLoadStream(pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().distinct().filter(Objects::nonNull);
  }

  @UnitOfWork(CMS)
  protected List<ChangedEntityIdentifier> getCwsCmsIncrementalLoadIdentifiers(
      LocalDateTime dateAfter, PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers();
    recordChangeCwsCmsDao.getIncrementalLoadStream(dateAfter, pageRequest).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().distinct().filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
