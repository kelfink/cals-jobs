package gov.ca.cwds.jobs.cals.facility.recordchange;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.data.stream.ScalarResultsStreamer;

/**
 * @author CWDS TPT-2
 */
public final class CwsRecordChangesStreamer extends ScalarResultsStreamer<CwsRecordChange> {

  public CwsRecordChangesStreamer(BaseDaoImpl<CwsRecordChange> dao,
      QueryCreator<CwsRecordChange> queryCreator) {
    super(dao, queryCreator);
  }
}
