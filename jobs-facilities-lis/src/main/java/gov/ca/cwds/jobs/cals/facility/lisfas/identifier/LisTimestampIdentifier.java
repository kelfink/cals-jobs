package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.TimestampIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class LisTimestampIdentifier extends TimestampIdentifier<BigInteger> {

  public LisTimestampIdentifier(int id,
      TimestampSavePoint<BigInteger> savePoint) {
    super(String.valueOf(id), savePoint);
  }

  public LisTimestampIdentifier(int id,
      RecordChangeOperation recordChangeOperation,
      TimestampSavePoint<BigInteger> savePoint) {
    super(String.valueOf(id), recordChangeOperation, savePoint);
  }

}
