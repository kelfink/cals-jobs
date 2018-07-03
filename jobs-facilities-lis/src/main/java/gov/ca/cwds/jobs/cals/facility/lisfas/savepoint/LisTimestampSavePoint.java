package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public class LisTimestampSavePoint extends TimestampSavePoint<BigInteger> {

  public LisTimestampSavePoint() {
  }

  public LisTimestampSavePoint(BigInteger timestamp) {
    super(timestamp);
  }

  @Override
  public int compareTo(TimestampSavePoint<BigInteger> o) {
    if (o.getTimestamp() == null) {
      return getTimestamp() == null ? 0 : -1;
    }
    return getTimestamp().compareTo(o.getTimestamp());
  }

}
