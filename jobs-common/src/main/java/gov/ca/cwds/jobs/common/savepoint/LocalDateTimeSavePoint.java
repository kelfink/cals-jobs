package gov.ca.cwds.jobs.common.savepoint;

import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 6/30/2018.
 */
public class LocalDateTimeSavePoint extends TimestampSavePoint<LocalDateTime> {

  public LocalDateTimeSavePoint() {
    super();
  }

  public LocalDateTimeSavePoint(LocalDateTime timestamp) {
    super(timestamp);
  }

  @Override
  public int compareTo(TimestampSavePoint<LocalDateTime> o) {
    if (o.getTimestamp() == null) {
      return o.getTimestamp() == null ? 0 : -1;
    }
    return getTimestamp().compareTo(o.getTimestamp());
  }

}
