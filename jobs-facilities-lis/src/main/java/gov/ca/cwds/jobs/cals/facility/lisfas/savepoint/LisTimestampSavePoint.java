package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.math.BigInteger;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public class LisTimestampSavePoint implements SavePoint, Comparable<LisTimestampSavePoint> {

  private BigInteger timestamp;

  public LisTimestampSavePoint() {
  }

  public LisTimestampSavePoint(BigInteger timestamp) {
    this.timestamp = timestamp;
  }

  public BigInteger getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(BigInteger timestamp) {
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

  @Override
  public int compareTo(LisTimestampSavePoint o) {
    if (timestamp == null) {
      return o.timestamp == null ? 0 : -1;
    }
    return timestamp.compareTo(o.timestamp);
  }

}
