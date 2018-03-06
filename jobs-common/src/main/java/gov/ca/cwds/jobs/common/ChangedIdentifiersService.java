package gov.ca.cwds.jobs.common;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public interface ChangedIdentifiersService {

    Stream<ChangedEntityInformation> getIdentifiersForInitialLoad();

    Stream<ChangedEntityInformation> getIdentifiersForIncrementalLoad(LocalDateTime localDateTime);

}
