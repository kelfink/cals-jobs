package gov.ca.cwds.jobs.component;

import gov.ca.cwds.dao.cms.BatchDaoImpl;
import gov.ca.cwds.data.std.ApiMarker;

public interface JobHibernateSettings extends ApiMarker {

  static final int DEFAULT_BATCH_WAIT = 25;
  static final int DEFAULT_BUCKETS = 1;

  static final int ES_BULK_SIZE = 5000;
  static final int SLEEP_MILLIS = 2500;
  static final int POLL_MILLIS = 3000;

  static final int DEFAULT_FETCH_SIZE = BatchDaoImpl.DEFAULT_FETCH_SIZE;


}
