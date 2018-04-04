package gov.ca.cwds.jobs.cals.facility.lis;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import java.math.BigInteger;
import java.util.stream.Stream;
import org.hibernate.SessionFactory;

/**
 * @author CWDS CALS API Team
 */
public class RecordChangeLisDao extends BaseDaoImpl<LisRecordChange> {

  private static final String LIMIT = "limit";
  private static final String OFFSET = "offset";

  @Inject
  public RecordChangeLisDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public Stream<LisRecordChange> getInitialLoadStream(
      PageRequest pageRequest) {
    QueryCreator<LisRecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME)
        .setParameter(LIMIT, pageRequest.getLimit())
        .setParameter(OFFSET, pageRequest.getOffset())
        .setReadOnly(true);
    return new LisRecordChangesStreamer(this, queryCreator).createStream();
  }

  @SuppressWarnings("unchecked")
  public Stream<LisRecordChange> getIncrementalLoadStream(final BigInteger dateAfter,
      PageRequest pageRequest) {
    QueryCreator<LisRecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME)
        .setParameter("dateAfter", dateAfter)
        .setParameter(LIMIT, pageRequest.getLimit())
        .setParameter(OFFSET, pageRequest.getOffset())
        .setReadOnly(true);
    return new LisRecordChangesStreamer(this, queryCreator).createStream();
  }


}
