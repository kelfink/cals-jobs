package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public interface ChangedIdentifiersService {

  List<ChangedEntityIdentifier> getIdentifiersForInitialLoad(PageRequest pageRequest);

  List<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(LocalDateTime localDateTime,
      PageRequest pageRequest);

  List<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime localDateTime,
      PageRequest pageRequest);

}
