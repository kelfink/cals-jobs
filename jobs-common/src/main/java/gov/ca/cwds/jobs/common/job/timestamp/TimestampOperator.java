package gov.ca.cwds.jobs.common.job.timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface TimestampOperator {

    String DATE_TIME_FORMAT = "yyyy-MM-dd-HH:mm:ss.SSSSSSSSS";

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    boolean timeStampExists();

    LocalDateTime readTimestamp();

    void writeTimestamp(LocalDateTime timestamp);

}
