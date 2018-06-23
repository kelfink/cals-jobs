package gov.ca.cwds.jobs.common.savepoint;

import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public class TimestampSavePoint implements SavePoint {

  private LocalDateTime timestamp;

  public TimestampSavePoint() {}

  public TimestampSavePoint(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return timestamp != null ? timestamp.toString() : "Empty timestamp";
  }

}
