package gov.ca.cwds.jobs.common.job.timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface TimestampOperator {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSS");

    boolean timeStampExists();

    LocalDateTime readTimestamp();

    void writeTimestamp(LocalDateTime timestamp);

}
