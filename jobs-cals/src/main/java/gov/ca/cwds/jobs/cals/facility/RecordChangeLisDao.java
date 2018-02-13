package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.stream.QueryCreator;
import org.hibernate.SessionFactory;

import java.util.Date;
import java.util.stream.Stream;

/** @author CWDS CALS API Team */
public class RecordChangeLisDao extends BaseDaoImpl<RecordChange> {

  @Inject
  public RecordChangeLisDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked") // because of getNamedNativeQuery
  public Stream<RecordChange> streamChangedFacilityRecords(final Date after) {
    QueryCreator<RecordChange> queryCreator = (session, entityClass) -> session
        .getNamedNativeQuery(entityClass.getSimpleName() + ".findChangedFacilityRecordsInLIS")
// //TODO uncomment when LDU is ready     .setParameter("dateAfter", after) TEMPORARY COMMENTED OUT UNTIL LDU PROVIDES SOME DATES FOR THAT FIELD
        .setReadOnly(true);
    return new RecordChangesStreamer(this, queryCreator).createStream();
  }
}
