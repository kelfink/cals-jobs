package gov.ca.cwds.jobs.cals.facility.cws.mode;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.mode.TimestampInitialModeImplementor;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 6/23/2018.
 */
public class CwsJobInitialModeImplementor extends
    TimestampInitialModeImplementor<ChangedFacilityDto> {

  @Inject
  private CwsJobModeFinalizer jobModeFinalizer;

  @Override
  @UnitOfWork(CMS)
  public void doFinalizeJob() {
    jobModeFinalizer.doFinalizeJob();
  }

}
