package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainer;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointService;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.mode.AbstractJobModeImplementor;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 7/2/2018.
 */
public class LisConnx11IncrementaJobModeImplementor extends
    AbstractJobModeImplementor<ChangedFacilityDto, TimestampSavePoint<BigInteger>, DefaultJobMode> {

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Inject
  private LisTimestampSavePointService lisTimestampSavePointService;

  @Override
  public void doFinalizeJob() {
    //empty
  }

  @Override
  public List<JobBatch<TimestampSavePoint<BigInteger>>> getNextPortion() {
    LisTimestampSavePoint savePoint = (LisTimestampSavePoint) lisTimestampSavePointService
        .loadSavePoint(LisTimestampSavePointContainer.class);
    List<JobBatch<TimestampSavePoint<BigInteger>>> portion = new ArrayList<>();
    JobBatch<TimestampSavePoint<BigInteger>> batch = new JobBatch<>(
        changedEntitiesIdentifiersService
            .getIdentifiersForIncrementalLoad(savePoint.getTimestamp()));
    if (!batch.isEmpty()) {
      portion.add(batch);
    }
    return portion;
  }

  @Override
  public TimestampSavePoint<BigInteger> defineSavepoint(
      JobBatch<TimestampSavePoint<BigInteger>> jobBatch) {
    List<ChangedEntityIdentifier<TimestampSavePoint<BigInteger>>> changedEntityIdentifiers =
        jobBatch.getChangedEntityIdentifiers();
    return changedEntityIdentifiers
        .get(changedEntityIdentifiers.size() - 1).getSavePoint();
  }
}
