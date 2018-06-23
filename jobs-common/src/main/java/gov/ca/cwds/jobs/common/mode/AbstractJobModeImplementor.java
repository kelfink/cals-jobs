package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class AbstractJobModeImplementor<E, S extends SavePoint> implements
    JobModeImplementor<E, S> {

  @Inject
  private SavePointService<S> savePointService;

  @Inject
  private ChangedEntityService<E> changedEntityService;

  @Override
  public S loadSavePoint() {
    return savePointService.loadSavePoint();
  }

  @Override
  public void saveSavePoint(S savePoint) {
    savePointService.saveSavePoint(savePoint);
  }

  @Override
  public E loadEntity(ChangedEntityIdentifier identifier) {
    return changedEntityService.loadEntity(identifier);
  }

}
