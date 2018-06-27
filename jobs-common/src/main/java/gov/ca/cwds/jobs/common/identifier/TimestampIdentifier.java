package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class TimestampIdentifier extends ChangedEntityIdentifier<TimestampSavePoint> {

  public TimestampIdentifier(String id,
      TimestampSavePoint savePoint) {
    super(id, savePoint);
  }

  public TimestampIdentifier(String id,
      RecordChangeOperation recordChangeOperation,
      TimestampSavePoint savePoint) {
    super(id, recordChangeOperation, savePoint);
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<TimestampSavePoint> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }
}
