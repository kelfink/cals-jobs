package gov.ca.cwds.jobs.cals.facility.lis;

import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

import com.google.inject.Inject;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import io.dropwizard.hibernate.UnitOfWork;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class LisChangedIdentifiersService implements ChangedIdentifiersService {

  @Inject
  private RecordChangeLisDao recordChangeLisDao;

  @UnitOfWork(LIS)
  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad() {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.LIS);
    recordChangeLisDao.getInitialLoadStream().
        map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(
      LocalDateTime timeStampAfter) {
    return getLisIncrementalLoadIdentifiers(timeStampAfter);
  }

  @Override
  public Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp) {
    return getLisIncrementalLoadIdentifiers(timestamp);
  }


  @UnitOfWork(LIS)
  protected Stream<ChangedEntityIdentifier> getLisIncrementalLoadIdentifiers(
      LocalDateTime timestampAfter) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.LIS);
    BigInteger dateAfter = new BigInteger(
        LisRecordChange.lisTimestampFormatter.format(timestampAfter));
    recordChangeLisDao.getIncrementalLoadStream(dateAfter).
        map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull);
  }

}
