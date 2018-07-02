package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainer;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.mode.AbstractTimestampJobModeImplementor;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;
import java.util.List;

/**
 * Lis Incremental Job Mode. Can be used only with CONNX version 12+ >
 * Created by Alexander Serbin on 7/1/2018.
 */
public class LisIncrementalJobModeImplementor extends
    AbstractTimestampJobModeImplementor<ChangedFacilityDto, BigInteger, DefaultJobMode> {

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Override
  public void doFinalizeJob() {
    //empty
  }

  @Override
  protected List<ChangedEntityIdentifier<TimestampSavePoint<BigInteger>>> getNextPage(
      PageRequest pageRequest) {
    TimestampSavePoint<BigInteger> savePoint = loadSavePoint(LisTimestampSavePointContainer.class);
    return changedEntitiesIdentifiersService
        .getIdentifiersForIncrementalLoad(savePoint.getTimestamp());
  }
}
