package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import org.hibernate.SessionFactory;

import java.math.BigInteger;
import java.util.stream.Stream;

/** @author CWDS CALS API Team */
public class RecordChangeLisDao extends BaseDaoImpl<RecordChange> {

  @Inject
  public RecordChangeLisDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public Stream<RecordChange> getInitialLoadStream() {
    QueryCreator<RecordChange> queryCreator = (session, entityClass) -> session
            .getNamedNativeQuery(RecordChange.LIS_INITIAL_LOAD_QUERY_NAME)
            .setReadOnly(true);
    return new RecordChangesStreamer(this, queryCreator).createStream();
  }

  @SuppressWarnings("unchecked")
  public Stream<RecordChange> getIncrementalLoadStream(final BigInteger dateAfter) {
    QueryCreator<RecordChange> queryCreator = (session, entityClass) -> session
            .getNamedNativeQuery(RecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME)
            .setParameter("dateAfter", dateAfter)
            .setReadOnly(true);
    return new RecordChangesStreamer(this, queryCreator).createStream();
  }


}
