package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.jobs.cals.facility.cws.CwsRecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME;
import static gov.ca.cwds.jobs.cals.facility.cws.CwsRecordChange.CWSCMS_INITIAL_LOAD_QUERY_NAME;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class RecordChangeCwsCmsDao extends BaseDaoImpl<CwsRecordChange> {

  private static final String LIMIT = "limit";
  private static final String OFFSET = "offset";

  @Inject
  public RecordChangeCwsCmsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public Stream<CwsRecordChange> getInitialLoadStream(PageRequest pageRequest) {
    return loadStream(LocalDateTime.of(1970, 1, 1, 1, 1),
        CWSCMS_INITIAL_LOAD_QUERY_NAME, pageRequest);
  }

  @SuppressWarnings("unchecked")
  public Stream<CwsRecordChange> getIncrementalLoadStream(final LocalDateTime dateAfter,
      PageRequest pageRequest) {
    return loadStream(dateAfter, CWSCMS_INCREMENTAL_LOAD_QUERY_NAME, pageRequest);
  }

  public Stream<CwsRecordChange> getResumeInitialLoadStream(LocalDateTime timeStampAfter,
      PageRequest pageRequest) {
    return loadStream(timeStampAfter, CWSCMS_INITIAL_LOAD_QUERY_NAME, pageRequest);
  }

  private Stream<CwsRecordChange> loadStream(LocalDateTime timeStampAfter,
      String queryName, PageRequest pageRequest) {
    QueryCreator<CwsRecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(queryName)
        .setParameter("dateAfter", timeStampAfter)
        .setParameter(LIMIT, pageRequest.getLimit())
        .setParameter(OFFSET, pageRequest.getOffset())
        .setReadOnly(true);
    return new CwsRecordChangesStreamer(this, queryCreator).createStream();
  }

}
