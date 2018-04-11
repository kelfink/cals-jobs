package gov.ca.cwds.jobs.cals.facility.lis;

import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

import com.google.inject.Inject;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import io.dropwizard.hibernate.UnitOfWork;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class LisChangedEntitiesIdentifiersService implements ChangedEntitiesIdentifiersService {

  @Inject
  private RecordChangeLisDao recordChangeLisDao;

  @UnitOfWork(LIS)
  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.LIS);
    recordChangeLisDao.getInitialLoadStream(pageRequest).
        map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().distinct().filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(
      LocalDateTime timeStampAfter, PageRequest pageRequest) {
    return getLisIncrementalLoadIdentifiers(timeStampAfter, pageRequest);
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp,
      PageRequest pageRequest) {
    return getLisIncrementalLoadIdentifiers(timestamp, pageRequest);
  }


  @UnitOfWork(LIS)
  protected List<ChangedEntityIdentifier> getLisIncrementalLoadIdentifiers(
      LocalDateTime timestampAfter, PageRequest pageRequest) {
    ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(
        DataSourceName.LIS);
    BigInteger dateAfter = new BigInteger(
        LisRecordChange.lisTimestampFormatter.format(timestampAfter));
    recordChangeLisDao.getIncrementalLoadStream(dateAfter, pageRequest).
        map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().distinct().filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
