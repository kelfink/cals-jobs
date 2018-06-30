package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class LisTimestampIdentifier extends ChangedEntityIdentifier<LisTimestampSavePoint> {

  public LisTimestampIdentifier(int id,
      LisTimestampSavePoint savePoint) {
    super(String.valueOf(id), savePoint);
  }

  public LisTimestampIdentifier(int id,
      RecordChangeOperation recordChangeOperation,
      LisTimestampSavePoint savePoint) {
    super(String.valueOf(id), recordChangeOperation, savePoint);
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<LisTimestampSavePoint> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }
}
