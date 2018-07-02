package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.mode.JobMode;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class SavePointServiceImpl<S extends SavePoint, J extends JobMode> implements
    SavePointService<S, J> {

  @Inject
  private SavePointContainerService<S, J> savePointContainerService;

  @Override
  public S loadSavePoint(
      Class<? extends SavePointContainer<? extends S, J>> savePointContainerClass) {
    return savePointContainerService.readSavePointContainer(savePointContainerClass)
        .getSavePoint();
  }

  @Override
  public S defineSavepoint(JobBatch<S> jobBatch) {
    return jobBatch.getChangedEntityIdentifiers()
        .get(jobBatch.getChangedEntityIdentifiers().size() - 1).getSavePoint();
  }

}
