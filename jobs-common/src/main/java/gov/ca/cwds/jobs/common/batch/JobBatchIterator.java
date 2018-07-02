package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.List;

/**
 * Created by Alexander Serbin on 4/3/2018.
 */
@FunctionalInterface
public interface JobBatchIterator<S extends SavePoint> {

  default void init() {
  }

  /**
   * Iterates over target entities' identifiers.
   */
  List<JobBatch<S>> getNextPortion();

}
