package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainer;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LisInitialResumeModeImplementor extends AbstractLisInitialModeImplementor {

  @Override
  public void init() {
    lastId = loadSavePoint(LicenseNumberSavePointContainer.class).getLicenseNumber();
  }

}
