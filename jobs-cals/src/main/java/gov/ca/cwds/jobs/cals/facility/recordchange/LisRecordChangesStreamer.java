package gov.ca.cwds.jobs.cals.facility.recordchange;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.data.stream.ScalarResultsStreamer;

/**
 * @author CWDS TPT-2
 */
public final class LisRecordChangesStreamer extends ScalarResultsStreamer<LisRecordChange> {

  public LisRecordChangesStreamer(BaseDaoImpl<LisRecordChange> dao,
      QueryCreator<LisRecordChange> queryCreator) {
    super(dao, queryCreator);
  }
}
