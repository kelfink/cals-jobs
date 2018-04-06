package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedRfaFormIdentifiersService implements ChangedEntitiesIdentifiersService {

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest) {
    return Collections.emptyList();
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(
      LocalDateTime localDateTime, PageRequest pageRequest) {
    return Collections.emptyList();
  }

  @Override
  public List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(
      LocalDateTime localDateTime, PageRequest pageRequest) {
    return Collections.emptyList();
  }
}
