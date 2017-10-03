package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.JobOptions;

/**
 * Common features of all Elasticsearch indexing jobs.
 * 
 * @author CWDS API Team
 */
public interface AtomShared extends ApiMarker {

  /**
   * Date time format for last run date file.
   */
  static String LAST_RUN_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * Common timestamp format for legacy DB.
   */
  static String LEGACY_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  JobProgressTrack getTrack();

  ElasticsearchDao getEsDao();

  /**
   * Getter for the job's command line options.
   * 
   * @return this job's options
   */
  JobOptions getOpts();

  default void nameThread(final String title) {
    Thread.currentThread().setName(title);
  }

}
