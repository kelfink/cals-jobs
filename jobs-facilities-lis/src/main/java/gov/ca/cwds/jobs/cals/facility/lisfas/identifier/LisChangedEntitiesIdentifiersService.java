package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilitiesIdentifiers;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisRecordChange;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.FirstIncrementalSavePointDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.RecordChangeLisDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class LisChangedEntitiesIdentifiersService {

  @Inject
  private RecordChangeLisDao recordChangeLisDao;

  @Inject
  private FirstIncrementalSavePointDao firstIncrementalSavePointDao;

  @UnitOfWork(LIS)
  public List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getIdentifiersForInitialLoad(
      int lastId) {
    ChangedFacilitiesIdentifiers<LicenseNumberSavePoint> changedEntityIdentifiers = new ChangedFacilitiesIdentifiers<>();
    recordChangeLisDao.getInitialLoadStream(lastId).stream()
        .map(LisRecordChange::toLicenseNumberIdentifier).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull).sorted()
        .collect(Collectors.toList());
  }

  @UnitOfWork(LIS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<BigInteger>>> getIdentifiersForIncrementalLoad(
      BigInteger savePoint) {
    ChangedFacilitiesIdentifiers<TimestampSavePoint<BigInteger>> changedEntityIdentifiers =
        new ChangedFacilitiesIdentifiers<>();
    recordChangeLisDao.getIncrementalLoadStream(savePoint).stream().
        map(LisRecordChange::toLisTimestampIdentifier).forEach(changedEntityIdentifiers::add);
    return changedEntityIdentifiers.newStream().filter(Objects::nonNull).sorted()
        .collect(Collectors.toList());
  }

  @UnitOfWork(LIS)
  public BigInteger findMaxTimestamp() {
    return firstIncrementalSavePointDao.findMaxTimestamp();
  }

}
