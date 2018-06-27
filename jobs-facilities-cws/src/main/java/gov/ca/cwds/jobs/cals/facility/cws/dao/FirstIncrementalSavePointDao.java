package gov.ca.cwds.jobs.cals.facility.cws.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.dao.CustomDao;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.CwsRecordChange;
import java.time.LocalDateTime;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 6/26/2018.
 */
public class FirstIncrementalSavePointDao extends CustomDao {

  @Inject
  public FirstIncrementalSavePointDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public LocalDateTime findMaxTimestamp() {
    return currentSession()
        .createNamedQuery(CwsRecordChange.CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME, LocalDateTime.class)
        .setMaxResults(1).uniqueResult();
  }

}
