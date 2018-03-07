package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class TestChangeIdentifiersService implements ChangedIdentifiersService {

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad() {
        return Stream.empty();
    }

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime localDateTime) {
        return Stream.empty();
    }
}
