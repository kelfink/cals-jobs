package gov.ca.cwds.jobs.common.api;

import gov.ca.cwds.jobs.common.job.TotalCountInformation;
import java.time.LocalDateTime;

/**
 * This service should provide information about how many entities are going to be loaded.
 * It helps to build predictions of how long job will take and makes possible to assure
 * that job run has been successful
 *
 * Created by Alexander Serbin on 4/6/2018.
 */
public interface TotalEntitiesCountProvider {

  /**
   * Calculates total entities count that expected to get loaded during initial load.
   *
   * @return bean with total entities count to be inserted/updated/deleted
   */
  TotalCountInformation getTotalsForInitialLoad();

  /**
   * Calculates total entities count that are expected to get loaded during resuming of
   * initial load.
   *
   * @param timestamp job timestamp - savepoint after which job failed to continue processing
   */
  TotalCountInformation getTotalsForResumingInitialLoad(LocalDateTime timestamp);

  /**
   * Calculates total entities count that are expected to get loaded during incremental load.
   *
   * @param timestamp after this timestamp changed entities will be loaded
   */
  TotalCountInformation getTotalsForIncrementalLoad(LocalDateTime timestamp);

}
