package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class CwsChangedIdentifiersService implements ChangedIdentifiersService {

  @Inject
  private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad() {
    return getCwsCmsInitialLoadIdentifiers();
  }

  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(
      LocalDateTime timeStampAfter) {
    return getCwsCmsResumingInitialLoadIdentifiers(timeStampAfter);
  }

  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp) {
    return getCwsCmsIncrementalLoadIdentifiers(timestamp);
  }

  @UnitOfWork(CMS)
  protected Stream<ChangedEntityIdentifier> getCwsCmsResumingInitialLoadIdentifiers(
      LocalDateTime timeStampAfter) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.CWS);
    recordChangeCwsCmsDao.getResumeInitialLoadStream(timeStampAfter).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

  @UnitOfWork(CMS)
  protected Stream<ChangedEntityIdentifier> getCwsCmsInitialLoadIdentifiers() {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.CWS);
    recordChangeCwsCmsDao.getInitialLoadStream().
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

  @UnitOfWork(CMS)
  protected Stream<ChangedEntityIdentifier> getCwsCmsIncrementalLoadIdentifiers(
      LocalDateTime dateAfter) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.CWS);
    recordChangeCwsCmsDao.getIncrementalLoadStream(dateAfter).
        map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

}
