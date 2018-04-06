package gov.ca.cwds.jobs.common.identifier;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.Constants;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedIdentifiersProviderImpl implements ChangedIdentifiersProvider {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ChangedIdentifiersProviderImpl.class);

  @Inject
  private TimestampOperator timestampOperator;

  @Inject
  private ChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Override
  public List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    if (!timestampOperator.timeStampExists()) {
      LOGGER.info("Processing initial load");
      return changedEntitiesIdentifiersService.getIdentifiersForInitialLoad(pageRequest);
    } else if (isTimestampMoreThanOneMonthOld()) {
      LOGGER.info("Processing initial load - resuming after save point {}", timestampOperator
          .readTimestamp());
      return changedEntitiesIdentifiersService
          .getIdentifiersForResumingInitialLoad(timestampOperator.readTimestamp(), pageRequest);
    } else {
      LocalDateTime timestamp = timestampOperator.readTimestamp();
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Processing incremental load after timestamp {}",
            Constants.DATE_TIME_FORMATTER.format(timestamp));
      }
      return changedEntitiesIdentifiersService
          .getIdentifiersForIncrementalLoad(timestamp, pageRequest);
    }
  }

  private boolean isTimestampMoreThanOneMonthOld() {
    return timestampOperator.readTimestamp().until(LocalDateTime.now(), ChronoUnit.MONTHS) > 1;
  }

}
