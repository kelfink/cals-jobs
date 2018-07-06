package gov.ca.cwds.jobs.cals.facility.lisfas.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS CALS API Team
 */
public class LicenseNumberIdentifierDao extends BaseDaoImpl<LicenseNumberIdentifier> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LicenseNumberIdentifierDao.class);

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  public LicenseNumberIdentifierDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getInitialLoadStream(
      int licenseNumber) {
    List<ChangedEntityIdentifier<LicenseNumberSavePoint>> identifiers = currentSession()
        .createNamedQuery(LicenseNumberIdentifier.LIS_INITIAL_LOAD_QUERY_NAME)
        .setMaxResults(batchSize)
        .setReadOnly(true).setParameter("facNbr", licenseNumber).list();
    LOGGER.info("Identifiers count {}", identifiers.size());
    LOGGER.info("Identifiers ", identifiers);
    return identifiers;
  }

}
