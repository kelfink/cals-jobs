package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class LicenseNumberIdentifier extends ChangedEntityIdentifier<LicenseNumberSavePoint> {

  public LicenseNumberIdentifier(int id,
      LicenseNumberSavePoint savePoint) {
    super(String.valueOf(id), savePoint);
  }

  public LicenseNumberIdentifier(int id,
      RecordChangeOperation recordChangeOperation,
      LicenseNumberSavePoint savePoint) {
    super(String.valueOf(id), recordChangeOperation, savePoint);
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<LicenseNumberSavePoint> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }

}
