package gov.ca.cwds.jobs.common.identifier;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public interface ChangedIdentifiersService {

    Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad();

    Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime localDateTime);

}
