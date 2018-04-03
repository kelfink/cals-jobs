package gov.ca.cwds.jobs.common.batch;

import java.util.List;

/**
 * Created by Alexander Serbin on 4/3/2018.
 */

@FunctionalInterface
public interface JobBatchIterator {

  default void init() {
  }

  default void setBatchSize(int batchSize) {
  }

  List<JobBatch> getNextPortion();

}
