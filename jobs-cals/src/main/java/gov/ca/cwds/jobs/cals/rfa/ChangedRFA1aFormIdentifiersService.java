package gov.ca.cwds.jobs.cals.rfa;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedRFA1aFormIdentifiersService implements ChangedIdentifiersService {

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad() {
        return null;
    }

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime localDateTime) {
        return null;
    }
}
