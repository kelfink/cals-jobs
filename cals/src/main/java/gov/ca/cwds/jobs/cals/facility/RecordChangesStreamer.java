package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.data.stream.ScalarResultsStreamer;

/**
 * @author CWDS TPT-2
 */
public final class RecordChangesStreamer extends ScalarResultsStreamer<RecordChange> {
  public RecordChangesStreamer(BaseDaoImpl<RecordChange> dao, QueryCreator<RecordChange> queryCreator) {
    super(dao, queryCreator);
  }
}
