package gov.ca.cwds.jobs.cals.facility.cws.savepoint;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.cws.dao.FirstIncrementalSavePointDao;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 6/26/2018.
 */
public class CwsTimestampSavePointService extends LocalDateTimeSavePointService {

  @Inject
  private FirstIncrementalSavePointDao dao;

  @UnitOfWork(CMS)
  public LocalDateTimeSavePoint findFirstIncrementalSavePoint() {
    return new LocalDateTimeSavePoint(dao.findMaxTimestamp());
  }

}
