package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class AbstractJobModeImplementor<E, S extends SavePoint, J extends JobMode> implements
    JobModeImplementor<E, S, J> {

  @Inject
  private SavePointService<S, J> savePointService;

  @Inject
  private ChangedEntityService<E> changedEntityService;

  @Inject
  private SavePointContainerService<S, J> savePointContainerService;

  @Override
  public S loadSavePoint(
      Class<? extends SavePointContainer<? extends S, J>> savePointContainerClass) {
    return savePointService.loadSavePoint(savePointContainerClass);
  }

  @Override
  public void saveSavePoint(S savePoint) {
    savePointService.saveSavePoint(savePoint);
  }

  @Override
  public E loadEntity(ChangedEntityIdentifier identifier) {
    return changedEntityService.loadEntity(identifier);
  }

  @Override
  public final void finalizeJob() {
    if (savePointContainerService.savePointContainerExists()) {
      doFinalizeJob();
    }
  }

}
