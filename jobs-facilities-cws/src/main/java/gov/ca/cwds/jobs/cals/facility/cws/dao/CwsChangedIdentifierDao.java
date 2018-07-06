package gov.ca.cwds.jobs.cals.facility.cws.dao;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsChangedIdentifierDao extends BaseDaoImpl<CwsChangedIdentifier> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsChangedIdentifierDao.class);

  @Inject
  public CwsChangedIdentifierDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getInitialLoadStream(
      PageRequest pageRequest) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers =
        getCwsChangedIdentifiers(LocalDateTime.of(1970, 1, 1, 1, 1),
            CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_QUERY_NAME, pageRequest);
    LOGGER.info("identifiers count {}", identifiers.size());
    LOGGER.info("identifiers = {}", identifiers);
    return identifiers;
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIncrementalLoadStream(
      final LocalDateTime dateAfter,
      PageRequest pageRequest) {
    return getCwsChangedIdentifiers(dateAfter,
        CwsChangedIdentifier.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
        pageRequest);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getResumeInitialLoadStream(
      LocalDateTime timeStampAfter,
      PageRequest pageRequest) {
    return getCwsChangedIdentifiers(timeStampAfter,
        CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_QUERY_NAME,
        pageRequest);
  }

  @SuppressWarnings("unchecked")
  private List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsChangedIdentifiers(
      LocalDateTime timeStampAfter,
      String queryName, PageRequest pageRequest) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers =
        currentSession().createNamedQuery(queryName)
            .setParameter("dateAfter", timeStampAfter)
            .setMaxResults(pageRequest.getLimit())
            .setFirstResult(pageRequest.getOffset())
            .setReadOnly(true).list();
    LOGGER.info("identifiers count {}", identifiers.size());
    LOGGER.info("identifiers = {}", identifiers);

    return identifiers;
  }

}
