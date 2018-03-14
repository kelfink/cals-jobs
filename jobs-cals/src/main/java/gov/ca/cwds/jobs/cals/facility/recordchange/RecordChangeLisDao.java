package gov.ca.cwds.jobs.cals.facility.recordchange;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import org.hibernate.SessionFactory;

import java.math.BigInteger;
import java.util.stream.Stream;

/** @author CWDS CALS API Team */
public class RecordChangeLisDao extends BaseDaoImpl<LisRecordChange> {

  @Inject
  public RecordChangeLisDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public Stream<LisRecordChange> getInitialLoadStream() {
    QueryCreator<LisRecordChange> queryCreator = (session, entityClass) -> session
            .getNamedNativeQuery(LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME)
            .setReadOnly(true);
    return new LisRecordChangesStreamer(this, queryCreator).createStream();
  }

  @SuppressWarnings("unchecked")
  public Stream<LisRecordChange> getIncrementalLoadStream(final BigInteger dateAfter) {
    QueryCreator<LisRecordChange> queryCreator = (session, entityClass) -> session
            .getNamedNativeQuery(LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME)
            .setParameter("dateAfter", dateAfter)
            .setReadOnly(true);
    return new LisRecordChangesStreamer(this, queryCreator).createStream();
  }


}
