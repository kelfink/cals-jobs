package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.stream.Stream;

/**
 * @author CWDS CALS API Team
 */
public class RecordChangeFasDao extends BaseDaoImpl<RecordChange> {

  @Inject
  public RecordChangeFasDao(@FasSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked") // because of getNamedNativeQuery
  public Stream<RecordChange> streamChangedFacilityRecords(final boolean initialLoad,
      final Date after) {
    QueryCreator<RecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(entityClass.getSimpleName() + ".findChangedFacilityRecordsInFAS")
        .setParameter("initialLoad", initialLoad ? 1 : 0)
        .setParameter("dateAfter", after)
        .setReadOnly(true);
    return new RecordChangesStreamer(this, queryCreator).createStream();
  }
}
