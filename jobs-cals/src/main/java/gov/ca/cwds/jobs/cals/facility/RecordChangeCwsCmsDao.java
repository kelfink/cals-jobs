package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.stream.Stream;

/**
 * @author CWDS TPT-2
 */
public class RecordChangeCwsCmsDao extends BaseDaoImpl<RecordChange> {

  @Inject
  public RecordChangeCwsCmsDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked") // because of getNamedNativeQuery
  public Stream<RecordChange> streamChangedFacilityRecords(final Date after) {
    QueryCreator<RecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(entityClass.getSimpleName() + ".findChangedFacilityRecordsInCWSCMS")
        .setParameter("dateAfter", after).setReadOnly(true);
    return new RecordChangesStreamer(this, queryCreator).createStream();
  }
}
