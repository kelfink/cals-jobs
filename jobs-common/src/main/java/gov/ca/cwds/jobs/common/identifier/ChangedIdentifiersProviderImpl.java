package gov.ca.cwds.jobs.common.identifier;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.Constants;
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
  private ChangedIdentifiersService changedIdentifiersService;

  @Override
  public List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest) {
    if (!timestampOperator.timeStampExists()) {
      LOGGER.info("Processing initial load");
      return changedIdentifiersService.getIdentifiersForInitialLoad(pageRequest);
    } else if (isTimestampMoreThanOneMonthOld()) {
      LOGGER.info("Processing initial load - resuming after save point " + timestampOperator
          .readTimestamp());
      return changedIdentifiersService
          .getIdentifiersForResumingInitialLoad(timestampOperator.readTimestamp(), pageRequest);
    } else {
      LocalDateTime timestamp = timestampOperator.readTimestamp();
      LOGGER.info("Processing incremental load after timestamp " +
          Constants.DATE_TIME_FORMATTER.format(timestamp));
      return changedIdentifiersService.getIdentifiersForIncrementalLoad(timestamp, pageRequest);
    }
  }

  private boolean isTimestampMoreThanOneMonthOld() {
    return timestampOperator.readTimestamp().until(LocalDateTime.now(), ChronoUnit.MONTHS) > 1;
  }

}
