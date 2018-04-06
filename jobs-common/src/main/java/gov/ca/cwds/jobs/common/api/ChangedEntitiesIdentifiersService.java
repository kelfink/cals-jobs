package gov.ca.cwds.jobs.common.api;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This service provides next identifiers pages for every job mode.
 *
 * Created by Alexander Serbin on 3/5/2018.
 */
public interface ChangedEntitiesIdentifiersService {

  /**
   * Fetches next page of target entities' identifiers for initial load.
   */
  List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest);

  /**
   * Fetches next page of target entities' identifiers for resuming initial load.
   */
  List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(LocalDateTime timestamp,
      PageRequest pageRequest);

  /**
   * Fetches next page of target entities' identifiers for incremental load.
   */
  List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp,
      PageRequest pageRequest);

}
