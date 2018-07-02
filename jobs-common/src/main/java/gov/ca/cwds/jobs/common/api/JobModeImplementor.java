package gov.ca.cwds.jobs.common.api;

import gov.ca.cwds.jobs.common.batch.JobBatchIterator;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;

/**
 * Every job mode must have its implementor covering
 * See ConcreteImplementor from Bridge Pattern.
 *
 * Created by Alexander Serbin on 6/19/2018.
 */
public interface JobModeImplementor<E, S extends SavePoint, J extends JobMode> extends
    SavePointService<S, J>,
    ChangedEntityService<E>,
    JobBatchIterator<S>,
    JobModeFinalizer {

  /**
   * Switches to the next job mode
   * Saves last save point in proper format.
   * Last save point may be in format of next job mode.
   */
  void finalizeJob();

}
