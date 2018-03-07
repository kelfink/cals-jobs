package gov.ca.cwds.jobs.common.identifier.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedIdentifiersProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangedIdentifiersProvider.class);

    @Inject
    private TimestampOperator timestampOperator;

    @Inject
    private ChangedIdentifiersService changedIdentifiersService;

    public Stream<ChangedEntityIdentifier> get() {
        if (!timestampOperator.timeStampExists()) {
            LOGGER.info("Processing initial load");
            return changedIdentifiersService.getIdentifiersForInitialLoad();
        } else {
            LocalDateTime timestamp = timestampOperator.readTimestamp();
            LOGGER.info("Processing incremental load after timestamp " +
                    TimestampOperator.DATE_TIME_FORMATTER.format(timestamp));
            return changedIdentifiersService.getIdentifiersForIncrementalLoad(timestamp);
        }
    }

}
