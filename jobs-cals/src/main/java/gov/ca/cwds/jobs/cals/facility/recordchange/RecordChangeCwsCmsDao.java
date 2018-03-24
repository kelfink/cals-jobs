package gov.ca.cwds.jobs.cals.facility.recordchange;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static gov.ca.cwds.jobs.cals.facility.recordchange.CwsRecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME;
import static gov.ca.cwds.jobs.cals.facility.recordchange.CwsRecordChange.CWSCMS_INITIAL_LOAD_QUERY_NAME;
import static gov.ca.cwds.jobs.cals.facility.recordchange.CwsRecordChange.CWSCMS_RESUME_INCREMENTAL_LOAD_QUERY_NAME;

/**
 * @author CWDS TPT-2
 */
public class RecordChangeCwsCmsDao extends BaseDaoImpl<CwsRecordChange> {

  @Inject
  public RecordChangeCwsCmsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public Stream<CwsRecordChange> getInitialLoadStream() {
    QueryCreator<CwsRecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(CWSCMS_INITIAL_LOAD_QUERY_NAME)
        .setReadOnly(true);
    return new CwsRecordChangesStreamer(this, queryCreator).createStream();
  }

  @SuppressWarnings("unchecked")
  public Stream<CwsRecordChange> getIncrementalLoadStream(final LocalDateTime dateAfter) {
    return getIncrementalLoadStream(dateAfter, CWSCMS_INCREMENTAL_LOAD_QUERY_NAME);
  }

  public Stream<CwsRecordChange> getResumeInitialLoadStream(LocalDateTime timeStampAfter) {
    return getIncrementalLoadStream(timeStampAfter, CWSCMS_RESUME_INCREMENTAL_LOAD_QUERY_NAME);
  }

  private Stream<CwsRecordChange> getIncrementalLoadStream(LocalDateTime timeStampAfter, String queryName) {
    QueryCreator<CwsRecordChange> queryCreator = (session, entityClass) -> session
            .getNamedNativeQuery(queryName)
            .setParameter("dateAfter", timeStampAfter)
            .setReadOnly(true);
    return new CwsRecordChangesStreamer(this, queryCreator).createStream();
  }

}
