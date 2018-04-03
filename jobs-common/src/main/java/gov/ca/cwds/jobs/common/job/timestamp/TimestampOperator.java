package gov.ca.cwds.jobs.common.job.timestamp;

import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface TimestampOperator {

  boolean timeStampExists();

  LocalDateTime readTimestamp();

  void writeTimestamp(LocalDateTime timestamp);

}
