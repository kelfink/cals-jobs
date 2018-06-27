package gov.ca.cwds.jobs.cals.facility.cws.mode;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.cws.service.CwsTimestampSavePointService;
import gov.ca.cwds.jobs.common.mode.TimestampInitialModeImplementor;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 6/23/2018.
 */
public class CwsJobInitialModeImplementor extends
    TimestampInitialModeImplementor<ChangedFacilityDto> {

  @Inject
  private CwsTimestampSavePointService service;

  @Override
  @UnitOfWork(CMS)
  public TimestampSavePoint findNextModeSavePoint() {
    return service.findFirstIncrementalSavePoint();
  }
}
