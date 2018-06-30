package gov.ca.cwds.jobs.cals.facility.lisfas.dao;

import static gov.ca.cwds.jobs.cals.facility.lisfas.LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME;
import static gov.ca.cwds.jobs.cals.facility.lisfas.LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisRecordChange;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import java.math.BigInteger;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 * @author CWDS CALS API Team
 */
public class RecordChangeLisDao extends BaseDaoImpl<LisRecordChange> {

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  public RecordChangeLisDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public List<LisRecordChange> getInitialLoadStream(
      int licenseNumber) {
    Query<LisRecordChange> query = currentSession()
        .createNamedQuery(LIS_INITIAL_LOAD_QUERY_NAME, LisRecordChange.class)
        .setMaxResults(batchSize)
        .setReadOnly(true);
    query.setParameter("facNbr", licenseNumber);
    return query.list();
  }

  public List<LisRecordChange> getIncrementalLoadStream(final BigInteger dateAfter) {
    Query<LisRecordChange> query = currentSession()
        .createNamedQuery(LIS_INCREMENTAL_LOAD_QUERY_NAME, LisRecordChange.class)
        .setMaxResults(batchSize)
        .setReadOnly(true);
    query.setParameter("dateAfter", dateAfter);
    return query.list();
  }

}
