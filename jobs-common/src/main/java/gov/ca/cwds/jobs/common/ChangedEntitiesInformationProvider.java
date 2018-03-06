package gov.ca.cwds.jobs.common;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;

import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedEntitiesInformationProvider {

    @Inject
    private TimestampOperator timestampOperator;

    @Inject
    private ChangedIdentifiersService changedIdentifiersService;

    public Stream<ChangedEntityInformation> findChangedEntities() {
        if (!timestampOperator.timeStampExists()) {
            return changedIdentifiersService.getIdentifiersForInitialLoad();
        } else {
            return changedIdentifiersService.getIdentifiersForIncrementalLoad(
                    timestampOperator.readTimestamp());
        }
    }

}
