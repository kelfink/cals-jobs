package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public abstract class AbstractTimestampJobModeImplementor<E> extends
    AbstractJobModeImplementor<E, TimestampSavePoint, DefaultJobMode> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(AbstractTimestampJobModeImplementor.class);

  private int offset = 0;

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  private ChangedEntityService<E> changedEntityService;

  @Inject
  private TimestampSavePointService savePointService;

  @Inject
  private TimestampSavePointContainerService savePointContainerService;

  @Override
  public void doFinalizeJob() {
    TimestampSavePoint timestampSavePoint = findNextModeSavePoint();
    LOGGER.info("Updating job save point to the last batch save point {}", timestampSavePoint);
    DefaultJobMode nextJobMode = DefaultJobMode.INCREMENTAL_LOAD;
    LOGGER.info("Updating next job mode to the {}", nextJobMode);
    TimestampSavePointContainer savePointContainer = new TimestampSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(timestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }

  protected TimestampSavePoint findNextModeSavePoint() {
    return savePointService.loadSavePoint(TimestampSavePointContainer.class);
  }

  @Override
  public E loadEntity(ChangedEntityIdentifier identifier) {
    LOGGER.info("Loading entity by identifier {}", identifier.getId());
    return changedEntityService.loadEntity(identifier);
  }

  @Override
  public TimestampSavePoint loadSavePoint(
      Class<? extends SavePointContainer<TimestampSavePoint, DefaultJobMode>> savePointContainerClass) {
    return savePointService.loadSavePoint(TimestampSavePointContainer.class);
  }

  @Override
  public TimestampSavePoint defineSavepoint(JobBatch<TimestampSavePoint> jobBatch) {
    return savePointService.defineSavepoint(jobBatch);
  }

  @Override
  public void saveSavePoint(TimestampSavePoint savePoint) {
    savePointService.saveSavePoint(savePoint);
  }

  @Override
  public List<JobBatch<TimestampSavePoint>> getNextPortion() {
    LOGGER.info("Getting next portion");
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    if (timeStampsAreEmpty(identifiers)) {
      LOGGER.info("Found page with all empty timestamps");
      offset += batchSize;
      return Collections.singletonList(new JobBatch<>(identifiers));
    } else {
      return calculateNextPortion(identifiers);
    }
  }

  private List<ChangedEntityIdentifier<TimestampSavePoint>> getNextPage() {
    return getNextPage(new PageRequest(offset, batchSize));
  }

  protected abstract List<ChangedEntityIdentifier<TimestampSavePoint>> getNextPage(
      PageRequest pageRequest);

  private List<JobBatch<TimestampSavePoint>> calculateNextPortion(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers) {
    // it can be several batches in portion when
    // there are many entities with equal last updated timestamps
    List<JobBatch<TimestampSavePoint>> batches = findBatchesPriorToBatchWithSavepoint(identifiers);
    List<ChangedEntityIdentifier<TimestampSavePoint>> lastIdentifiersWithSavepoint =
        findLastIdentifiersPriorToSavepoint(getLastTimestamp(identifiers));
    batches.get(batches.size() - 1).getChangedEntityIdentifiers()
        .addAll(lastIdentifiersWithSavepoint);
    LOGGER
        .info("Found batches to load {}, save point is {}", batches, getLastTimestamp(identifiers));
    return batches;
  }

  private List<ChangedEntityIdentifier<TimestampSavePoint>> findLastIdentifiersPriorToSavepoint(
      TimestampSavePoint savePoint) {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiersWithSavepoint = new ArrayList<>(
        batchSize);
    ChangedEntityIdentifier<TimestampSavePoint> nextIdentifier = getNextIdentifier();
    while (nextIdentifier != null &&
        (savePoint.equals(nextIdentifier.getSavePoint()))) {
      offset++;
      identifiersWithSavepoint.add(nextIdentifier);
      nextIdentifier = getNextIdentifier();
    }
    return identifiersWithSavepoint;
  }

  private List<JobBatch<TimestampSavePoint>> findBatchesPriorToBatchWithSavepoint(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers) {
    List<JobBatch<TimestampSavePoint>> nextPortion = new ArrayList<>();
    TimestampSavePoint timestampSavePoint = getLastTimestamp(identifiers);
    List<ChangedEntityIdentifier<TimestampSavePoint>> nextIdentifiersPage = identifiers;
    while ((!nextIdentifiersPage.isEmpty() && timestampSavePoint
        .equals(getLastTimestamp(nextIdentifiersPage)))) {
      offset += batchSize;
      nextPortion.add(new JobBatch<>(nextIdentifiersPage));
      nextIdentifiersPage = getNextPage();
    }
    return nextPortion;
  }

  private ChangedEntityIdentifier<TimestampSavePoint> getNextIdentifier() {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = getNextPage(
        new PageRequest(offset, 1));
    assert identifiers.size() == 1 || identifiers.isEmpty();
    return identifiers.isEmpty() ? null : identifiers.get(0);
  }

  private static TimestampSavePoint getLastTimestamp(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers) {
    return identifiers.get(identifiers.size() - 1).getSavePoint();
  }

  private boolean timeStampsAreEmpty(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers) {
    return getLastTimestamp(identifiers) == null;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

}
